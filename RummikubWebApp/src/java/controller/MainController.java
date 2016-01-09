package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import logic.Game;
import logic.Player;
import logic.persistency.GamePersistency;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.Event;
import ws.rummikub.EventType;
import ws.rummikub.GameDetails;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.GameStatus;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.PlayerStatus;
import ws.rummikub.PlayerType;
import ws.rummikub.Tile;

public class MainController {

    private static final String DUP_GAME_NAME_ERR_MSG = "Could not create game due to duplicate game name";
    private static final String INVALID_NEW_GAME_PARAMS_ERR_MSG = "Could not create new game due to wrong game parameters";
    private static final String GAME_NOT_EXIST_ERR_MSG = "Could not find game name in games list";
    private static final String PLAYER_NOT_FOUND_ERR_MSG = "Could not find player name";
    private static final String CANT_JOIN_ACTIVE_GAME_ERR_MSG = "Could not join into active game";
    private static final int MAX_PLAYERS_NUMBER = 4;
    private static final int MIN_PLAYERS_NUMBER = 2;
    private static final int MIN_HUMAN_PLAYERS_NUMBER = 1;
    private static final int FIRST_PLAYER_ID = 1;
    private static final int FIRST_EVENT_ID = 1;
    private static final String COMPUTER_NAME_PREFIX = "Computer #";
    private Map<Integer, Player> playersIDs;
    private AtomicInteger generatedID;
    private Map<Game, List<Event>> gamesEventsMap;
    private AtomicInteger eventID;

    public MainController() {
        playersIDs = new HashMap<Integer, Player>();
        generatedID = new AtomicInteger(FIRST_PLAYER_ID);
        eventID = new AtomicInteger(FIRST_EVENT_ID);
        gamesEventsMap = new HashMap<Game, List<Event>>();
    }

    public List<Event> getEvents(int playerId, int eventId) throws InvalidParameters_Exception {
        return null;
    }

    public String createGameFromXML(String xmlData) throws InvalidParameters_Exception, InvalidXML_Exception, DuplicateGameName_Exception {
        Game game = GamePersistency.load(xmlData);
        if (getGameByName(game.getName()) != null) {
            throw new DuplicateGameName_Exception(DUP_GAME_NAME_ERR_MSG, null);
        }
        setIDsToPlayers(game.getPlayers());
        gamesEventsMap.put(game, new ArrayList<Event>());

        return game.getName();
    }

    public List<PlayerDetails> getPlayersDetails(String gameName) throws GameDoesNotExists_Exception {
        Game game = getGameByName(gameName);
        if (game == null) {
            throw new GameDoesNotExists_Exception(GAME_NOT_EXIST_ERR_MSG, null);
        }

        return getPlayersList(game);
    }

    public void createGame(String name, int humanPlayers, int computerizedPlayers) throws InvalidParameters_Exception, ws.rummikub.DuplicateGameName_Exception {
        if (isGameNameValid(name) == false
                || isPlayersNumberValid(humanPlayers, computerizedPlayers) == false) {
            throw new InvalidParameters_Exception(INVALID_NEW_GAME_PARAMS_ERR_MSG, null);
        }

        if (getGameByName(name) != null) {
            throw new DuplicateGameName_Exception(DUP_GAME_NAME_ERR_MSG, null);
        }

        GameDetails gameDetails = createNewGameDetails(name, humanPlayers, computerizedPlayers, 0, false, GameStatus.WAITING);
        Game game = new Game(gameDetails);
        createComputerizedPlayers(game);
        gamesEventsMap.put(game, new ArrayList<Event>());
    }

    public GameDetails getGameDetails(String gameName) throws GameDoesNotExists_Exception {
        Game game = getGameByName(gameName);
        if (game == null) {
            throw new GameDoesNotExists_Exception(GAME_NOT_EXIST_ERR_MSG, null);
        }
        GameDetails gameDetails = createNewGameDetails(game.getName(), game.getHumanPlayersNum(), game.getComputerizedPlayersNum(), game.getJoinedHumanPlayersNum(), game.isLoadedFromFile(), game.getStatus());

        return gameDetails;
    }

    public List<String> getWaitingGames() {
        List<String> waitingGames = new ArrayList<>();

        gamesEventsMap.keySet().stream().filter((game) -> (game.getStatus().equals(GameStatus.WAITING))).forEach((game) -> {
            waitingGames.add(game.getName());
        });

        return waitingGames;
    }

    public int joinGame(String gameName, String playerName) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        Game game = getGameByName(gameName);
        if (game == null) {
            throw new GameDoesNotExists_Exception(GAME_NOT_EXIST_ERR_MSG, null);
        }
        if (game.getStatus().equals(GameStatus.ACTIVE)) {
            throw new InvalidParameters_Exception(CANT_JOIN_ACTIVE_GAME_ERR_MSG, null);
        }
        return playerJoinIntoGame(playerName, game);
    }

    public PlayerDetails getPlayerDetails(int playerId) throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        Player player = playersIDs.get(playerId);
        if (player == null) {
            //TODO: throw PlayerNotFound Exception...
            // I think there is a mistake here with the Exceptions definitions.
            //Mail will be send to Liron about it.
        }
        return createPlayerDetailsFromPlayer(player);
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
        for (Game game : gamesEventsMap.keySet()) {
            if (game.getName().toLowerCase().equals(gameName.toLowerCase())) {
                return game;
            }
        }

        return null;
    }

    private boolean isGameNameValid(String name) {
        return (name != null && name.trim().length() > 0);
    }

    private boolean isPlayersNumberValid(int humanPlayers, int computerizedPlayers) {
        if (humanPlayers < MIN_HUMAN_PLAYERS_NUMBER || computerizedPlayers < 0) {
            return false;
        }
        if (humanPlayers + computerizedPlayers < MIN_PLAYERS_NUMBER) {
            return false;
        }

        if (humanPlayers + computerizedPlayers > MAX_PLAYERS_NUMBER) {
            return false;
        }

        return true;
    }

    private GameDetails createNewGameDetails(String name, int humanPlayers, int computerizedPlayers, int joinedHumanPlayers, boolean isFromFile, GameStatus status) {
        GameDetails gameDetails = new GameDetails();
        gameDetails.setHumanPlayers(humanPlayers);
        gameDetails.setComputerizedPlayers(computerizedPlayers);
        gameDetails.setLoadedFromXML(isFromFile);
        gameDetails.setStatus(status);
        gameDetails.setName(name);
        gameDetails.setJoinedHumanPlayers(joinedHumanPlayers);

        return gameDetails;
    }

    private PlayerDetails createPlayerDetailsFromPlayer(Player player) {
        PlayerDetails playerDetails = new PlayerDetails();
        playerDetails.setName(player.getName());
        playerDetails.setNumberOfTiles(player.getTiles().size());
        playerDetails.setPlayedFirstSequence(player.isFirstStep());
        playerDetails.setStatus(player.getStatus());
        if (player.isHuman()) {
            playerDetails.setType(PlayerType.HUMAN);
        } else {
            playerDetails.setType(PlayerType.COMPUTER);
        }

        return playerDetails;
    }

    private List<PlayerDetails> getPlayersList(Game game) {
        List<PlayerDetails> playersList = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            playersList.add(createPlayerDetailsFromPlayer(player));
        }

        return playersList;
    }

    private void setIDsToPlayers(List<Player> players) {
        int currPlayerID;

        for (Player player : players) {
            currPlayerID = generatedID.getAndIncrement();
            player.setID(currPlayerID);
            playersIDs.put(currPlayerID, player);
        }
    }

    private void createComputerizedPlayers(Game game) {
        Player player;
        int currPlayerID;

        for (int i = 0; i < game.getComputerizedPlayersNum(); i++) {
            currPlayerID = generatedID.getAndIncrement();
            player = new Player(currPlayerID, COMPUTER_NAME_PREFIX + currPlayerID, false);
            game.addPlayer(player);
            playersIDs.put(currPlayerID, player);
        }
    }

    private int playerJoinIntoGame(String playerName, Game game) throws InvalidParameters_Exception {
        int playerID;
        //Different handling for saved game regarding the player which already exist for saved games
        if (game.isLoadedFromFile()) {
            playerID = playerJoinIntoSavedGame(game, playerName);
        } else {
            playerID = playerJoinIntoNewGame(game, playerName);
        }
        //If the game status has been changed to ACTIVE, add GAME_START event
        if (game.getStatus().equals(GameStatus.ACTIVE)) {
            Event event = new Event();
            event.setId(eventID.getAndIncrement());
            event.setType(EventType.GAME_START);
            gamesEventsMap.get(game).add(event);
        }
        
        return playerID;
    }

    private int playerJoinIntoSavedGame(Game game, String playerName) throws InvalidParameters_Exception {
        Player player = null;
        for (Player currPlayer : game.getPlayers()) {
            if (currPlayer.getName().toLowerCase().equals(playerName.toLowerCase())) {
                player = currPlayer;
                break;
            }
        }
        //If player not found or this is not a human player, throw exception
        if (player == null || (player != null && !player.isHuman())) {
            throw new InvalidParameters_Exception(PLAYER_NOT_FOUND_ERR_MSG, null);
        }
        player.setStatus(PlayerStatus.JOINED);
        game.incJoinedHumanPlayersNum();
        return player.getID();
    }

    private int playerJoinIntoNewGame(Game game, String playerName) {
        int playerID = generatedID.getAndIncrement();
        Player player = new Player(playerID, playerName, true);
        game.addPlayer(player);
        playersIDs.put(playerID, player);
        return playerID;
    }
}
