package logic.persistency;

import static controllers.console.GameMainController.checkPlayersNameValidity;
import xml.Rummikub;
import xml.Tile;
import xml.Players;
import xml.PlayerType;
import xml.Color;
import xml.Board;
import java.util.ArrayList;
import java.util.List;
import logic.Game;
import logic.GameDetails;
import logic.Player;
import logic.PlayerDetails;
import logic.persistency.GamePersistency.PersistencyException;
import logic.tile.Sequence;

class XSDObjToGameObjConverter {
    private Game game;
    private Rummikub rummikubXSDObj;

    XSDObjToGameObjConverter(Rummikub rummikubXSDObj) {
        this.rummikubXSDObj = rummikubXSDObj;
    }
    
    Game convert() {
        GameDetails gameDetails = getGameDetailsFromXSDObj();
        checkPlayersNameValidity(gameDetails.getPlayersNames());
        game = new Game(gameDetails);
        createGameFromXSDObj();
        return game;
    }

    private GameDetails getGameDetailsFromXSDObj() {
        int ID = 1;
        List<logic.PlayerDetails> gamePlayersDetails = new ArrayList<>();
        for (Players.Player player : rummikubXSDObj.getPlayers().getPlayer()) {
            if (player.getType() == PlayerType.HUMAN) {
                gamePlayersDetails.add(new PlayerDetails(ID, player.getName(), true));
            }
            else {
                gamePlayersDetails.add(new PlayerDetails(ID, player.getName(), false));
            }
            ID++;
        }
        return new GameDetails(rummikubXSDObj.getName(), gamePlayersDetails, new FileDetails(null, null, true));
    }

    private void createGameFromXSDObj() {
        distributeTilesToPlayers();
        distributeTilesToBoard();
        Player currPlayer = getCurrPlayerFromXSDObj();
        game.setCurrentPlayer(currPlayer);
        setAllGamePlayerFirstStep();
    }

    private void distributeTilesToPlayers() {
        for (Players.Player player : rummikubXSDObj.getPlayers().getPlayer()) {
            logic.Player gamePlayer = getGamePlayerByName(player.getName());
            List<Tile> xsdTiles = player.getTiles().getTile();

            addTilesToGamePlayer(gamePlayer, xsdTiles);
        }
    }

    private void distributeTilesToBoard() {
        for (Board.Sequence xsdSeq : rummikubXSDObj.getBoard().getSequence()) {
            List<logic.tile.Tile> gameSeqTiles = getGameSeqTilesFromXSDSeq(xsdSeq);
            logic.tile.Sequence gameSequence = new Sequence(gameSeqTiles);
            game.getBoard().addSequence(gameSequence);
        }
    }

    private Player getCurrPlayerFromXSDObj() {
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(rummikubXSDObj.getCurrentPlayer()))
                return player;
        }
        throw new PersistencyException("Current player name is not one of the players");
    }

    private Player getGamePlayerByName(String name) {
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(name))
                return player;
        }
        throw new RuntimeException("getGamePlayerByName did not find the player");
    }

    private void addTilesToGamePlayer(Player gamePlayer, List<Tile> xsdTiles) {
        for (Tile xsdTile : xsdTiles) {

            logic.tile.Tile gameTile = pullTileFromDeckByXSDTile(xsdTile);
            if (gameTile == null) {
                throw new PersistencyException(getErrorMsg_TileAppearTooMuch(xsdTile));
            }
            gamePlayer.addTile(gameTile);
        }
    }

    private List<logic.tile.Tile> getGameSeqTilesFromXSDSeq(Board.Sequence xsdSeq) {
        List<logic.tile.Tile> gameSeqTiles = new ArrayList<>();
        for (Tile xsdTile : xsdSeq.getTile()) {
            logic.tile.Tile gameTile = pullTileFromDeckByXSDTile(xsdTile);
            if (gameTile == null) {
                throw new PersistencyException(getErrorMsg_TileAppearTooMuch(xsdTile));
            }
            gameSeqTiles.add(gameTile);
        }
        return gameSeqTiles;
    }

    private logic.tile.Tile pullTileFromDeckByXSDTile(Tile xsdTile) {
        logic.tile.Color tileColor = xsdToGameColorConverter(xsdTile.getColor());
        int tileValue = xsdTile.getValue();
        return game.getTilesDeck().pullTile(tileColor, tileValue);
    }

    private String getErrorMsg_TileAppearTooMuch(Tile xsdTile) {
        return "The tile " + xsdTile.getValue() + " " + xsdTile.getColor().name() + " appear too much times";
    }

    private logic.tile.Color xsdToGameColorConverter(Color color) {
        switch (color) {
            case BLACK:
                return logic.tile.Color.Black;
            case BLUE:
                return logic.tile.Color.Blue;
            case RED:
                return logic.tile.Color.Red;
            case YELLOW:
                return logic.tile.Color.Yellow;
            default:
                return logic.tile.Color.Black;
        }
    }

    private void setAllGamePlayerFirstStep() {
        for (Players.Player xsdPlayer : rummikubXSDObj.getPlayers().getPlayer()) {
            Player gamePlayer = getGamePlayerByName(xsdPlayer.getName());
            gamePlayer.setFirstStepCompleted(xsdPlayer.isPlacedFirstSequence());
        }
    }
}
