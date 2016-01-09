package logic.persistency;

import generated.Rummikub;
import generated.Tile;
import generated.Players;
import generated.PlayerType;
import generated.Color;
import generated.Board;
import java.util.ArrayList;
import java.util.List;
import logic.Game;
import logic.Player;
import logic.tile.Sequence;
import ws.rummikub.GameDetails;
import ws.rummikub.GameStatus;
import ws.rummikub.InvalidXML_Exception;

class XSDObjToGameObjConverter {

    private static final int INITIAL_PLAYER_ID = -1;

    public static Game createGameFromXSDObj(Rummikub rummikubXSDObj) throws InvalidXML_Exception {
        if (!checkGameValidity(rummikubXSDObj)) {
            throw new InvalidXML_Exception(null, null);
        }
        GameDetails gameDetails = getGameDetailsFromXSDObj(rummikubXSDObj);
        Game game = new Game(gameDetails);
        addPlayers(game, rummikubXSDObj);
        distributeTilesToPlayers(game, rummikubXSDObj);
        distributeTilesToBoard(game, rummikubXSDObj);
        setCurrPlayerFromXSDObj(game, rummikubXSDObj);
        setAllGamePlayerFirstStep(game, rummikubXSDObj);
        
        return game;
    }

    private static GameDetails getGameDetailsFromXSDObj(Rummikub rummikubXSDObj) {
        GameDetails gameDetails = new GameDetails();
        int humanPlayersNum = 0;
        int computerizedPlayersNum = 0;

        for (Players.Player player : rummikubXSDObj.getPlayers().getPlayer()) {
            if (player.getType() == PlayerType.HUMAN) {
                humanPlayersNum++;
            } else {
                computerizedPlayersNum++;
            }
        }
        gameDetails.setComputerizedPlayers(computerizedPlayersNum);
        gameDetails.setHumanPlayers(humanPlayersNum);
        gameDetails.setName(rummikubXSDObj.getName());
        gameDetails.setJoinedHumanPlayers(0);
        gameDetails.setStatus(GameStatus.WAITING);
        gameDetails.setLoadedFromXML(true);

        return gameDetails;
    }

    private static void distributeTilesToPlayers(Game game, Rummikub rummikubXSDObj) throws InvalidXML_Exception {
        for (Players.Player player : rummikubXSDObj.getPlayers().getPlayer()) {
            logic.Player gamePlayer = getGamePlayerByName(game, player.getName());
            List<Tile> xsdTiles = player.getTiles().getTile();

            addTilesToGamePlayer(game, gamePlayer, xsdTiles);
        }
    }

    private static void distributeTilesToBoard(Game game, Rummikub rummikubXSDObj) throws InvalidXML_Exception {
        for (Board.Sequence xsdSeq : rummikubXSDObj.getBoard().getSequence()) {
            List<logic.tile.Tile> gameSeqTiles = getGameSeqTilesFromXSDSeq(game, xsdSeq);
            logic.tile.Sequence gameSequence = new Sequence(gameSeqTiles);
            game.getBoard().addSequence(gameSequence);
        }
        
        //perform borad validity check
        if (!game.getBoard().isValid()) {
            throw new InvalidXML_Exception(null, null);
        }
    }

    private static void setCurrPlayerFromXSDObj(Game game, Rummikub rummikubXSDObj) {
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(rummikubXSDObj.getCurrentPlayer())) {
                game.setCurrentPlayer(player);
                return;
            }
        }
    }

    private static Player getGamePlayerByName(Game game, String name) {
        for (Player player : game.getPlayers()) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        //We should not get here since we created the logic game according to saved game,
        //so all players names exist in the logic game
        return null;
    }

    private static void addTilesToGamePlayer(Game game, Player gamePlayer, List<Tile> xsdTiles) throws InvalidXML_Exception {
        for (Tile xsdTile : xsdTiles) {
            logic.tile.Tile gameTile = pullTileFromDeckByXSDTile(game, xsdTile);
            if (gameTile == null) {
                throw new InvalidXML_Exception(null, null);
            }
            gamePlayer.addTile(gameTile);
        }
    }

    private static List<logic.tile.Tile> getGameSeqTilesFromXSDSeq(Game game, Board.Sequence xsdSeq) throws InvalidXML_Exception {
        List<logic.tile.Tile> gameSeqTiles = new ArrayList<>();
        for (Tile xsdTile : xsdSeq.getTile()) {
            logic.tile.Tile gameTile = pullTileFromDeckByXSDTile(game, xsdTile);
            if (gameTile == null) {
                throw new InvalidXML_Exception(null, null);
            }
            gameSeqTiles.add(gameTile);
        }
        return gameSeqTiles;
    }

    private static logic.tile.Tile pullTileFromDeckByXSDTile(Game game, Tile xsdTile) {
        logic.tile.Color tileColor = xsdToGameColorConverter(xsdTile.getColor());
        int tileValue = xsdTile.getValue();
        return game.getTilesDeck().pullTile(tileColor, tileValue);
    }

    private static String getErrorMsg_TileAppearTooMuch(Tile xsdTile) {
        return "The tile " + xsdTile.getValue() + " " + xsdTile.getColor().name() + " appear too much times";
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

    private static void setAllGamePlayerFirstStep(Game game, Rummikub rummikubXSDObj) {
        for (Players.Player xsdPlayer : rummikubXSDObj.getPlayers().getPlayer()) {
            Player gamePlayer = getGamePlayerByName(game, xsdPlayer.getName());
            gamePlayer.setFirstStepCompleted(xsdPlayer.isPlacedFirstSequence());
        }
    }

    private static boolean validatePlayersNames(Rummikub rummikubXSDObj) {
        List<Players.Player> players = rummikubXSDObj.getPlayers().getPlayer();
        List<String> playersNames = new ArrayList<>();

        for (Players.Player player : players) {
            playersNames.add(player.getName());
        }

        return Game.checkPlayersNameValidity(playersNames);
    }

    private static boolean validateAtleastOneHumanPlayer(Rummikub rummikubXSDObj) {
        for (Players.Player player : rummikubXSDObj.getPlayers().getPlayer()) {
            if (player.getType().value().equals(PlayerType.HUMAN.value())) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkGameValidity(Rummikub rummikubXSDObj) {
        boolean isPlayerNamesValid = validatePlayersNames(rummikubXSDObj);
        boolean isAtleastOneHumanPlayer = validateAtleastOneHumanPlayer(rummikubXSDObj);

        return isPlayerNamesValid && isAtleastOneHumanPlayer;
    }

    private static void addPlayers(Game game, Rummikub rummikubXSDObj) {
        List<Players.Player> players = rummikubXSDObj.getPlayers().getPlayer();
        Player logicPlayer;
        boolean isHuman;

        for (Players.Player player : players) {
            isHuman = player.getType().equals(PlayerType.HUMAN);
            //This is only initial player ID. To be changed by MainController
            //Which handle the players IDs
            logicPlayer = new Player(INITIAL_PLAYER_ID, player.getName(), isHuman);
            game.addPlayer(logicPlayer);
        }
    }
}
