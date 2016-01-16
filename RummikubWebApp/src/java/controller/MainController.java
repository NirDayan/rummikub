package controller;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.Game;
import logic.MoveTileData;
import logic.Player;
import logic.WSObjToGameObjConverter;
import static logic.WSObjToGameObjConverter.convertGameColorIntoGeneratedColor;
import logic.persistency.GamePersistency;
import logic.tile.Sequence;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.Event;
import ws.rummikub.EventType;
import ws.rummikub.GameDetails;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.GameStatus;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.PlayerType;
import ws.rummikub.Tile;
import javax.swing.Timer;
import logic.ComputerAI;

public class MainController {

    private static final String DUP_GAME_NAME_ERR_MSG = "Could not create game due to duplicate game name";
    private static final String INVALID_NEW_GAME_PARAMS_ERR_MSG = "Could not create new game due to wrong game parameters";
    private static final String GAME_NOT_EXIST_ERR_MSG = "Could not find game name in games list";
    private static final String PLAYER_NOT_FOUND_ERR_MSG = "Could not find player name";
    private static final String CANT_JOIN_ACTIVE_GAME_ERR_MSG = "Could not join into active game";
    private static final String EVENT_ID_NOT_FOUND_ERR_MSG = "Could not find event_id for the game of the specific player";
    private static final String EMPTY_TILES_LIST_ERR_MSG = "Empty tiles list parameter is invalid";
    private static final String NULL_TILE_ERR_MSG = "Null tile parameter is invalid";
    private static final String INVALID_SOURCE_SEQUENCE_INDEX_ERR_MSG = "Invalid source sequence index parameter";
    private static final String INVALID_SOURCE_SEQUENCE_POSITION_ERR_MSG = "Invalid source sequence position parameter";
    private static final String INVALID_TARGET_SEQUENCE_INDEX_ERR_MSG = "Invalid target sequence index parameter";
    private static final String INVALID_TARGET_SEQUENCE_POSITION_ERR_MSG = "Invalid target sequence position parameter";
    private static final String INVALID_ADD_TILE_PARAMETERS_ERR_MSG = "Invalid add tile parameters";
    private static final String INVALID_MOVE_TILE_PARAMETERS_ERR_MSG = "Invalid move tile parameters";
    private static final String INVALID_TAKE_BACK_TILE_PARAMETERS_ERR_MSG = "Invalid take back tile parameters";
    private static final String RESIGN_AFTER_TIMER_ERR_MSG = "Failed on Resign operation";
    private static final String NOT_CURRENT_PLAYER_ERR_MSG = "Player is parameter does not match current player";
    private static final String COMPUTERIZED_PLAYER_ERR_MSG = "Computer player action was corrupted";
    private static final String EMPTY_PLAYER_NAME = "";
    private static final int TIMEOUT_DELAY_MS = 5 * 60 * 1000;//5 seconds
    private static final int MAX_PLAYERS_NUMBER = 4;
    private static final int MIN_PLAYERS_NUMBER = 2;
    private static final int MIN_HUMAN_PLAYERS_NUMBER = 1;
    private static final int FIRST_PLAYER_ID = 1;
    private static final int FIRST_EVENT_ID = 1;
    private static final int COMPUTER_THINK_TIME_MS = 5 * 1000;//5 seconds
    private static final String COMPUTER_NAME_PREFIX = "Computer #";
    private final Map<Integer, Player> playersIDs;
    private final AtomicInteger generatedID;
    private final Map<Game, List<Event>> gamesEventsMap;
    private final Map<Game, AtomicInteger> eventIDMap;
    private final Map<Game, List<Event>> currentPlayerActionsMap;
    final private Map<Game, Timer> timersMap;
    private final ComputerAI computerAI = new ComputerAI();

    public MainController() {
        playersIDs = new HashMap<>();
        generatedID = new AtomicInteger(FIRST_PLAYER_ID);
        eventIDMap = new HashMap<>();
        gamesEventsMap = new HashMap<>();
        currentPlayerActionsMap = new HashMap<>();
        timersMap = new HashMap<>();
    }

    public List<Event> getEvents(int playerId, int eventId) throws InvalidParameters_Exception {
        List<Event> eventsResult = new ArrayList<>();

        Game game = checkGetEventsInputParams(playerId, eventId);
        if (game != null) {
            for (Event event : gamesEventsMap.get(game)) {
                if (event.getId() > eventId) {
                    eventsResult.add(event);
                }
            }
        }

        return eventsResult;
    }

    public String createGameFromXML(String xmlData) throws InvalidParameters_Exception, InvalidXML_Exception, DuplicateGameName_Exception {
        Game game = GamePersistency.load(xmlData);
        if (getGameByName(game.getName()) != null) {
            throw new DuplicateGameName_Exception(DUP_GAME_NAME_ERR_MSG, null);
        }
        setIDsToPlayers(game.getPlayers());
        gamesEventsMap.put(game, new ArrayList<>());
        eventIDMap.put(game, new AtomicInteger(FIRST_EVENT_ID));

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
        gamesEventsMap.put(game, new ArrayList<>());
        eventIDMap.put(game, new AtomicInteger(FIRST_EVENT_ID));
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
        Player player = getPlayerById(playerId);

        return createPlayerDetailsFromPlayer(player);
    }

    public void createSequence(int playerId, List<Tile> tiles) throws InvalidParameters_Exception {
        Player player = getPlayerById(playerId);
        if (tiles.isEmpty()) {
            throw new InvalidParameters_Exception(EMPTY_TILES_LIST_ERR_MSG, null);
        }
        Game game = getGameByPlayerID(playerId);
        if (game.getCurrentPlayer().getID() != playerId) {
            throw new InvalidParameters_Exception(NOT_CURRENT_PLAYER_ERR_MSG, null);
        }
        startTimer(game);
        backupTurn(game);
        List<logic.tile.Tile> tilesList = WSObjToGameObjConverter.convertGeneratedTilesListIntoGameTiles(tiles);
        if (game.createSequenceByTilesList(playerId, tilesList)) {
            createSequenceCreatedEvent(game, player.getName(), tilesList);
        }
    }

    public void addTile(int playerId, Tile tile, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        Player player = getPlayerById(playerId);
        Game game = getGameByPlayerID(player.getID());
        validateAddTileParameters(game, player, tile, sequenceIndex, sequencePosition);
        startTimer(game);
        logic.tile.Tile logicTile = WSObjToGameObjConverter.convertWSTileIntoGameTile(tile);
        if (game.isSplitRequired(sequenceIndex, sequencePosition)) {
            createSplitEvents(game, playerId, logicTile, sequenceIndex, sequencePosition);
        }
        game.addTile(player.getID(), logicTile, sequenceIndex, sequencePosition);
        createAddTileEvent(game, player, tile, sequenceIndex, sequencePosition);
    }

    public void takeBackTile(int playerId, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        Player player = getPlayerById(playerId);
        if (sequenceIndex < 0) {
            throw new InvalidParameters_Exception(INVALID_SOURCE_SEQUENCE_INDEX_ERR_MSG, null);
        }
        if (sequencePosition < 0) {
            throw new InvalidParameters_Exception(INVALID_SOURCE_SEQUENCE_POSITION_ERR_MSG, null);
        }
        Game game = getGameByPlayerID(player.getID());
        if (game.getCurrentPlayer().getID() != playerId) {
            throw new InvalidParameters_Exception(NOT_CURRENT_PLAYER_ERR_MSG, null);
        }
        startTimer(game);
        logic.tile.Tile tile = game.takeBackTile(playerId, sequenceIndex, sequencePosition);
        if (tile == null) {
            throw new InvalidParameters_Exception(INVALID_TAKE_BACK_TILE_PARAMETERS_ERR_MSG, null);
        }
        createTileReturnedEvent(game, playerId, sequenceIndex, sequencePosition, tile);
    }

    public void moveTile(int playerId, int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition) throws InvalidParameters_Exception {
        Player player = getPlayerById(playerId);
        MoveTileData moveTileData = new MoveTileData(playerId, sourceSequenceIndex, sourceSequencePosition, targetSequenceIndex, targetSequencePosition);
        validateMoveTileParameters(moveTileData);
        Game game = getGameByPlayerID(player.getID());
        if (game.getCurrentPlayer().getID() != playerId) {
            throw new InvalidParameters_Exception(NOT_CURRENT_PLAYER_ERR_MSG, null);
        }
        startTimer(game);
        if (game.moveTile(moveTileData)) {
            createMoveTileEvent(game, player.getName(), moveTileData);
        } else {
            throw new InvalidParameters_Exception(INVALID_MOVE_TILE_PARAMETERS_ERR_MSG, null);
        }
    }

    public void finishTurn(int playerId) throws InvalidParameters_Exception {
        Player player = getPlayerById(playerId);
        Game game = getGameByPlayerID(player.getID());
        if (game.getCurrentPlayer().getID() != playerId) {
            throw new InvalidParameters_Exception(NOT_CURRENT_PLAYER_ERR_MSG, null);
        }
        if (isPlayerPerformedAnyChange(playerId)) {
            createPlayerFinishedTurnEvent(game, playerId, new ArrayList<>());
            if (game.getBoard().isValid() == false) {
                punishPlayer(game, playerId);
                startTimer(game);
                moveToNextPlayer(game);
                return;
            }
            if (game.isPlayerFirstStep(playerId)) {
                if (isFirstStepCompleted(playerId)) {
                    game.setPlayerCompletedFirstStep(playerId);
                } else {
                    punishPlayer(game, playerId);
                }
            }
        } else {
            //Here the player chose to finishe the turn without performing any action.
            //In this case he will pull tile from deck.
            List<logic.tile.Tile> tilesList = new ArrayList<>();
            tilesList.add(game.pullTileFromDeck(playerId));
            createPlayerFinishedTurnEvent(game, playerId, tilesList);
        }
        startTimer(game);
        moveToNextPlayer(game);
    }

    public void resign(int playerId) throws InvalidParameters_Exception {
        Player player = getPlayerById(playerId);
        Game game = getGameByPlayerID(player.getID());
        game.playerResign(playerId);
        createPlayerResignedEvent(game, player.getName());
        if (game.checkIsGameOver()) {
            createGameOverEvent(game);
            stopTimer(game);
        } else {
            if (isPlayerPerformedAnyChange(playerId)) {
                if (game.getBoard().isValid() == false) {
                    game.restoreFromBackup();
                    createRevertEvent(game, playerId, new ArrayList<>());
                    createBoardSequencesEvents(game);
                }
            }
            //move to the next player if the current player is resigned
            if (game.getCurrentPlayer().getID() == playerId) {
                startTimer(game);
                moveToNextPlayer(game);
            }
        }
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
        addPlayerTilesIntoPlayerDetails(player, playerDetails);
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
        int playerId;
        //Different handling for saved game regarding the player which already exist for saved games
        if (game.isLoadedFromFile()) {
            playerId = game.joinPlayerIntoSavedGame(playerName);
        } else {
            playerId = generatedID.getAndIncrement();
            Player player = game.joinPlayerIntoNewGame(playerName, playerId);
            playersIDs.put(playerId, player);
        }
        //If the game status has been changed to ACTIVE, add GAME_START event
        if (game.getStatus().equals(GameStatus.ACTIVE)) {
            createGameStartEvent(game);
            createBoardSequencesEvents(game);
            createPlayerTurnEvent(game, game.getCurrentPlayer());
            startTimer(game);
            if (!game.getCurrentPlayer().isHuman()) {
                playComputerTurn(game);
            }
        }

        return playerId;
    }

    private Game checkGetEventsInputParams(int playerId, int eventId) throws InvalidParameters_Exception {
        Player player = playersIDs.get(playerId);
        if (player == null) {
            throw new InvalidParameters_Exception(PLAYER_NOT_FOUND_ERR_MSG, null);
        }
        Game game = getGameByPlayerID(playerId);
        //game should not be null here since we already has the player with the ID
        if (eventId < 0
                || (eventId > 0)
                && !gamesEventsMap.get(game).stream().anyMatch((event) -> (event.getId() == eventId))) {
            throw new InvalidParameters_Exception(EVENT_ID_NOT_FOUND_ERR_MSG, null);
        }

        return game;
    }

    private Game getGameByPlayerID(int playerId) {
        for (Game game : gamesEventsMap.keySet()) {
            if (game.getPlayers().stream().anyMatch((player) -> (player.getID() == playerId))) {
                return game;
            }
        }

        return null;
    }

    private void addPlayerTilesIntoPlayerDetails(Player player, PlayerDetails playerDetails) {
        List<Tile> tilesList = playerDetails.getTiles();
        tilesList.addAll(WSObjToGameObjConverter.convertGameTilesListIntoGeneratedTilesList(player.getTiles()));
    }

    private Player getPlayerById(int playerId) throws InvalidParameters_Exception {
        Player player = playersIDs.get(playerId);
        if (player == null) {
            throw new InvalidParameters_Exception(PLAYER_NOT_FOUND_ERR_MSG, null);
        }

        return player;
    }

    private void createSequenceCreatedEvent(Game game, String playerName, List<logic.tile.Tile> tilesList) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.SEQUENCE_CREATED);
        event.setPlayerName(playerName);
        event.getTiles().addAll(WSObjToGameObjConverter.convertGameTilesListIntoGeneratedTilesList(tilesList));
        gamesEventsMap.get(game).add(event);
        if (!playerName.isEmpty()) {
            addCurrentPlayerEvent(game, event);
        }
    }

    private void createGameStartEvent(Game game) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.GAME_START);
        gamesEventsMap.get(game).add(event);
    }

    private void validateAddTileParameters(Game game, Player player, Tile tile, int sequenceIndex, int sequencePosition) throws InvalidParameters_Exception {
        if (tile == null) {
            throw new InvalidParameters_Exception(NULL_TILE_ERR_MSG, null);
        }
        logic.tile.Tile logicTile = WSObjToGameObjConverter.convertWSTileIntoGameTile(tile);
        if (!game.checkAddTileValidity(player, logicTile, sequenceIndex, sequencePosition)) {
            throw new InvalidParameters_Exception(INVALID_ADD_TILE_PARAMETERS_ERR_MSG, null);
        }
        if (game.getCurrentPlayer().getID() != player.getID()) {
            throw new InvalidParameters_Exception(NOT_CURRENT_PLAYER_ERR_MSG, null);
        }
    }

    private void createAddTileEvent(Game game, Player player, Tile tile, int sequenceIndex, int sequencePosition) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.TILE_ADDED);
        event.setPlayerName(player.getName());
        event.setTargetSequenceIndex(sequenceIndex);
        event.setTargetSequencePosition(sequencePosition);
        event.getTiles().add(tile);
        gamesEventsMap.get(game).add(event);
        addCurrentPlayerEvent(game, event);
    }

    private void validateMoveTileParameters(MoveTileData moveTileData) throws InvalidParameters_Exception {
        if (moveTileData.getSourceSequenceIndex() < 0) {
            throw new InvalidParameters_Exception(INVALID_SOURCE_SEQUENCE_INDEX_ERR_MSG, null);
        }
        if (moveTileData.getSourceSequencePosition() < 0) {
            throw new InvalidParameters_Exception(INVALID_SOURCE_SEQUENCE_POSITION_ERR_MSG, null);
        }
        if (moveTileData.getTargetSequenceIndex() < 0) {
            throw new InvalidParameters_Exception(INVALID_TARGET_SEQUENCE_INDEX_ERR_MSG, null);
        }
        if (moveTileData.getTargetSequencePosition() < 0) {
            throw new InvalidParameters_Exception(INVALID_TARGET_SEQUENCE_POSITION_ERR_MSG, null);
        }
    }

    private void createMoveTileEvent(Game game, String playerName, MoveTileData moveTileData) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.TILE_MOVED);
        event.setPlayerName(playerName);
        event.setSourceSequenceIndex(moveTileData.getSourceSequenceIndex());
        event.setSourceSequencePosition(moveTileData.getSourceSequencePosition());
        event.setTargetSequenceIndex(moveTileData.getTargetSequenceIndex());
        event.setTargetSequencePosition(moveTileData.getTargetSequencePosition());
        gamesEventsMap.get(game).add(event);
        if (!playerName.isEmpty()) {
            addCurrentPlayerEvent(game, event);
        }
    }

    private void createPlayerResignedEvent(Game game, String playerName) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.PLAYER_RESIGNED);
        event.setPlayerName(playerName);
        gamesEventsMap.get(game).add(event);
        clearCurrentPlayerActionsList(game);
    }

    private boolean isFirstStepCompleted(int playerId) {
        Game game = getGameByPlayerID(playerId);
        List<Event> playerActions = currentPlayerActionsMap.get(game);
        if (playerActions != null) {
            boolean isNewSequenceCreated = playerActions.stream().anyMatch(event -> event.getType().equals(EventType.SEQUENCE_CREATED));
            if (isNewSequenceCreated) {
                if (isFirstStepCompleted(game, playerId)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isPlayerPerformedAnyChange(int playerId) {
        Game game = getGameByPlayerID(playerId);
        List<Event> playerActions = currentPlayerActionsMap.get(game);
        return playerActions != null && playerActions.size() > 0;
    }

    private boolean isFirstStepCompleted(Game game, int playerId) {
        List<Sequence> sequences = game.getBoard().getSequences();
        if (sequences.isEmpty()) {
            return false;
        }
        Sequence lastSequence = sequences.get(sequences.size() - 1);
        return game.checkSequenceValidity(playerId, lastSequence.toList());
    }

    private void createBoardSequencesEvents(Game game) {
        List<Sequence> sequences = game.getBoard().getSequences();

        sequences.stream().forEach((sequence) -> {
            createSequenceCreatedEvent(game, EMPTY_PLAYER_NAME, sequence.toList());
        });
    }

    private void punishPlayer(Game game, int playerId) {
        game.restoreFromBackup();
        //Punish player MUST be after restore from backup, otherwise the restore operation will remove additional tiles
        List<logic.tile.Tile> tilesToAdd = game.punishPlayer(playerId);
        createRevertEvent(game, playerId, tilesToAdd);
        createBoardSequencesEvents(game);
    }

    private void createRevertEvent(Game game, int playerId, List<logic.tile.Tile> tilesToAdd) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.REVERT);
        event.setPlayerName(playersIDs.get(playerId).getName());
        event.getTiles().addAll(WSObjToGameObjConverter.convertGameTilesListIntoGeneratedTilesList(tilesToAdd));
        gamesEventsMap.get(game).add(event);
    }

    private void backupTurn(Game game) {
        if (isBackupNeeded(game)) {
            game.storeBackup();
        }
    }

    private boolean isBackupNeeded(Game game) {
        List<Event> playerActions = currentPlayerActionsMap.get(game);

        return playerActions == null || playerActions.isEmpty();
    }

    private void createPlayerFinishedTurnEvent(Game game, int playerId, List<logic.tile.Tile> tilesToAdd) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.PLAYER_FINISHED_TURN);
        event.setPlayerName(playersIDs.get(playerId).getName());
        event.getTiles().addAll(WSObjToGameObjConverter.convertGameTilesListIntoGeneratedTilesList(tilesToAdd));
        gamesEventsMap.get(game).add(event);
    }

    private void moveToNextPlayer(Game game) {
        clearCurrentPlayerActionsList(game);
        if (game.checkIsGameOver()) {
            if (game.getWinner() != null) {
                createGameWinnerEvent(game);
            } else {
                createGameOverEvent(game);
            }
            return;
        }
        game.moveToNextPlayer();
        while (game.getCurrentPlayer().isResign()) {
            game.moveToNextPlayer();
        }
        Player currentPlayer = game.getCurrentPlayer();
        createPlayerTurnEvent(game, currentPlayer);
        startTimer(game);
        if (!game.getCurrentPlayer().isHuman()) {
            playComputerTurn(game);
        }
    }

    private void createGameOverEvent(Game game) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.GAME_OVER);
        gamesEventsMap.get(game).add(event);
    }

    private void clearCurrentPlayerActionsList(Game game) {
        List<Event> playerActions = currentPlayerActionsMap.get(game);
        if (playerActions != null) {
            playerActions.clear();
        }
    }

    private void createPlayerTurnEvent(Game game, Player player) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.PLAYER_TURN);
        event.setPlayerName(player.getName());
        gamesEventsMap.get(game).add(event);
    }

    private void addCurrentPlayerEvent(Game game, Event event) {
        List<Event> playerActions = currentPlayerActionsMap.get(game);
        if (playerActions == null) {
            currentPlayerActionsMap.put(game, new ArrayList<>());
        }
        currentPlayerActionsMap.get(game).add(event);
    }

    private void createGameWinnerEvent(Game game) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.GAME_WINNER);
        event.setPlayerName(game.getWinner().getName());
        gamesEventsMap.get(game).add(event);
    }

    private void createTileReturnedEvent(Game game, int playerId, int sequenceIndex, int sequencePosition, logic.tile.Tile tile) {
        Event event = new Event();
        event.setId(eventIDMap.get(game).getAndIncrement());
        event.setType(EventType.TILE_RETURNED);
        event.setPlayerName(playersIDs.get(playerId).getName());
        event.setSourceSequenceIndex(sequenceIndex);
        event.setSourceSequencePosition(sequencePosition);
        Tile returnedTile = new ws.rummikub.Tile();
        returnedTile.setColor(convertGameColorIntoGeneratedColor(tile.getColor()));
        returnedTile.setValue(tile.getValue());
        event.getTiles().add(returnedTile);
        gamesEventsMap.get(game).add(event);
        addCurrentPlayerEvent(game, event);
    }

    /**
     * This function creates split operation events BEFORE performing the split
     * on the game object. game.addTile is responsible for the split operation.
     *
     * @param game
     * @param playerId
     * @param tile
     * @param sequenceIndex
     * @param sequencePosition
     */
    private void createSplitEvents(Game game, int playerId, logic.tile.Tile tile, int sequenceIndex, int sequencePosition) {
        List<logic.tile.Tile> emptyTilesList = new ArrayList<>();
        createSequenceCreatedEvent(game, EMPTY_PLAYER_NAME, emptyTilesList);
        List<MoveTileData> movedTilesList = game.getMovedTileListForSplitOperation(sequenceIndex, sequencePosition);

        for (MoveTileData moveTileData : movedTilesList) {
            createMoveTileEvent(game, EMPTY_PLAYER_NAME, moveTileData);
        }
    }

    /**
     * Start a timer after player action. Call player resign function if timeout
     * is reached;
     *
     * @param game
     */
    private void startTimer(Game game) {
        //Stop previous timer first
        stopTimer(game);
        //Then create a new timer
        Timer timer = new Timer(TIMEOUT_DELAY_MS, (ActionEvent event) -> {
            try {
                ((Timer) event.getSource()).stop();
                int currentPlayerId = game.getCurrentPlayer().getID();
                resign(currentPlayerId);
            } catch (InvalidParameters_Exception ex) {
                //We should not get into here at all
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, RESIGN_AFTER_TIMER_ERR_MSG, ex);
            }
        });
        timer.start();
        //Add the timer into timers map
        timersMap.put(game, timer);
    }

    private void stopTimer(Game game) {
        Timer timer = timersMap.get(game);
        if (timer != null) {
            timer.stop();
        }
    }

    private void playComputerTurn(Game game) {
        Runnable task = () -> {
            try {
                Player compPlayer = game.getCurrentPlayer();
                List<logic.tile.Tile> logicTileList = computerAI.getRelevantTiles(compPlayer.getTiles());
                List<Tile> wsTilesList;

                while (logicTileList != null) {
                    // Simulate Computer "Thinking..."
                    Thread.sleep(COMPUTER_THINK_TIME_MS);
                    wsTilesList = WSObjToGameObjConverter.convertGameTilesListIntoGeneratedTilesList(logicTileList);
                    createSequence(compPlayer.getID(), wsTilesList);
                    logicTileList = computerAI.getRelevantTiles(game.getCurrentPlayer().getTiles());
                }
                finishTurn(compPlayer.getID());
            } catch (Exception ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, COMPUTERIZED_PLAYER_ERR_MSG, ex);
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
