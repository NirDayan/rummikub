package controller;

import java.util.ArrayList;
import java.util.List;
import logic.Game;
import logic.persistency.GamePersistency;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.Event;
import ws.rummikub.GameDetails;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.Tile;

public class MainController {

    private static final String DUP_GAME_NAME_ERR_MSG = "Could not load file due to duplicate game name";
    private static final String GAME_NOT_EXIST_ERR_MSG = "Could not load file due to duplicate game name";
    List<Game> games;

    public MainController() {
        games = new ArrayList<>();
    }

    public List<Event> getEvents(int playerId, int eventId) throws InvalidParameters_Exception {
        return null;
    }

    public String createGameFromXML(String xmlData) throws InvalidParameters_Exception, InvalidXML_Exception, DuplicateGameName_Exception {
        Game game = GamePersistency.load(xmlData);
        if (getGameByName(game.getName()) != null) {
            throw new DuplicateGameName_Exception(DUP_GAME_NAME_ERR_MSG, null);
        }
        games.add(game);

        return game.getName();
    }

    public List<PlayerDetails> getPlayersDetails(String gameName) throws GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void createGame(String name, int humanPlayers, int computerizedPlayers) throws InvalidParameters_Exception, ws.rummikub.DuplicateGameName_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public GameDetails getGameDetails(String gameName) throws GameDoesNotExists_Exception {
        Game game = getGameByName(gameName);
        if (game == null) {
            throw new GameDoesNotExists_Exception(GAME_NOT_EXIST_ERR_MSG, null);
        }
        GameDetails gameDetails = new GameDetails();
        gameDetails.setHumanPlayers(game.getHumanPlayers().size());
        gameDetails.setComputerizedPlayers(game.getComputerizedPlayers().size());
        gameDetails.setLoadedFromXML(game.isLoadedFromFile());
        gameDetails.setStatus(game.getStatus());
        gameDetails.setName(game.getName());

        return gameDetails;
    }

    public List<String> getWaitingGames() {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public int joinGame(String gameName, String playerName) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public PlayerDetails getPlayerDetails(int playerId) throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void createSequence(int playerId, List<Tile> tiles) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void addTile(int playerId, Tile tile, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void takeBackTile(int playerId, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void moveTile(int playerId, int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void finishTurn(int playerId) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void resign(int playerId) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private Game getGameByName(String gameName) {
        for (Game game : games) {
            if (game.getName().toLowerCase().equals(gameName.toLowerCase())) {
                return game;
            }
        }

        return null;
    }
}
