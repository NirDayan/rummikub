package logic.persistency;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logic.Game;
import logic.GameDetails;
import logic.Player;
import logic.tile.Sequence;

class XSDObjToGameObjConverter {
    private static Game game;
    private static Rummikub rummikubXSDObj;

    static {
        game = null;
        rummikubXSDObj = null;
    }

    static GameDetails getGameDetailsFromXSDObj(Rummikub rummikubXSDObj) {
        int humanPlayersCount = 0;
        int computerPlayersCount = 0;
        LinkedList<String> names = new LinkedList<>();
        for (Players.Player player : rummikubXSDObj.players.getPlayer()) {
            if (player.getType() == PlayerType.HUMAN) {
                humanPlayersCount++;
                names.addFirst(player.getName());
            }
            else {
                computerPlayersCount++;
                names.addLast(player.getName());
            }
        }
        return new GameDetails(computerPlayersCount, humanPlayersCount, rummikubXSDObj.getName(), names, null);
    }

    static void createGameFromXSDObj(Game game, Rummikub rummikubXSDObj) {
        XSDObjToGameObjConverter.game = game;
        XSDObjToGameObjConverter.rummikubXSDObj = rummikubXSDObj;
        distributeTilesToPlayers();
        distributeTilesToBoard();
        Player currPlayer = getCurrPlayerFromXSDObj();
        game.setCurrentPlayer(currPlayer);
    }

    private static Player getCurrPlayerFromXSDObj() {
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(rummikubXSDObj.currentPlayer))
                return player;
        }
        throw new RuntimeException("Current player name not found");
    }

    private static Player getGamePlayerByName(String name) {
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(name))
                return player;
        }
        throw new RuntimeException("getGamePlayerByName did not find the player");
    }

    private static void distributeTilesToPlayers() {
        for (Players.Player player : rummikubXSDObj.getPlayers().getPlayer()) {
            logic.Player gamePlayer = getGamePlayerByName(player.getName());
            List<Tile> xsdTiles = player.getTiles().getTile();

            addTilesToGamePlayer(gamePlayer, xsdTiles);
        }
    }

    private static void addTilesToGamePlayer(Player gamePlayer, List<Tile> xsdTiles) {
        for (Tile xsdTile : xsdTiles) {

            logic.tile.Tile gameTile = pullTileFromDeckByXSDTile(xsdTile);

            gamePlayer.addTile(gameTile);
        }
    }

    private static logic.tile.Color xsdToGameColorConverter(Color color) {
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

    private static void distributeTilesToBoard() {
        for (Board.Sequence xsdSeq : rummikubXSDObj.getBoard().getSequence()) {
            List<logic.tile.Tile> gameSeqTiles = getGameSeqTilesFromXSDSeq(xsdSeq);
            logic.tile.Sequence gameSequence = new Sequence(gameSeqTiles);
            game.getBoard().addSequence(gameSequence);
        }
    }

    private static List<logic.tile.Tile> getGameSeqTilesFromXSDSeq(Board.Sequence xsdSeq) {
        List<logic.tile.Tile> gameSeqTiles = new ArrayList<>();
        for (Tile xsdTile : xsdSeq.getTile()) {
            logic.tile.Tile gameTile = pullTileFromDeckByXSDTile(xsdTile);
            gameSeqTiles.add(gameTile);
        }
        return gameSeqTiles;
    }

    private static logic.tile.Tile pullTileFromDeckByXSDTile(Tile xsdTile) {
        logic.tile.Color tileColor = xsdToGameColorConverter(xsdTile.getColor());
        int tileValue = xsdTile.getValue();
        return game.getTilesDeck().pullTile(tileColor, tileValue);
    }

}
