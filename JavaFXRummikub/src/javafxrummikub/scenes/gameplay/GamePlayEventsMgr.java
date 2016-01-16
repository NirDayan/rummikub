package javafxrummikub.scenes.gameplay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import ws.rummikub.Event;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.RummikubWebService;
import ws.rummikub.Tile;
import static logic.WSObjToGameObjConverter.*;

public class GamePlayEventsMgr {
    private String playerName;
    private static final int INTERVAL = 1;
    private final IGamePlayEventHandler eventsHandler;
    private final RummikubWebService server;
    private final int playerID;
    private int eventIndex;
    private List<Event> eventsList;
    private final String gameName;
    private ScheduledThreadPoolExecutor threadPool;
    private ScheduledFuture<?> updateTask = null;

    public GamePlayEventsMgr(IGamePlayEventHandler eventsHandler, RummikubWebService rummikubGameWS, int playerID, String gameName) {
        this.eventsHandler = eventsHandler;
        this.server = rummikubGameWS;
        this.playerID = playerID;
        this.gameName = gameName;
        eventIndex = 0;
    }

    public void start() {
        threadPool = new ScheduledThreadPoolExecutor(1);
        updateTask = threadPool.scheduleAtFixedRate(
                () -> Platform.runLater(this::updateEvents),
                0, // initial delay
                INTERVAL,
                TimeUnit.SECONDS);
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel(true);
        }
        threadPool.shutdown();
    }

    private void updateEvents() {
        try {
            //get events from server.
            eventsList = server.getEvents(playerID, eventIndex);
            for (Event event : eventsList) {
                eventIndex++;
                switch (event.getType()) {
                    case GAME_OVER:
                        handleGameOver();
                        break;
                    case GAME_START:
                        handleGameStart();
                        break;
                    case GAME_WINNER:
                        handleGameWinner(event);
                        break;
                    case PLAYER_FINISHED_TURN:
                        handlePlayerFinishedTurn(event);
                        break;
                    case PLAYER_RESIGNED:
                        handlePlayerResigned(event);
                        break;
                    case PLAYER_TURN:
                        handlePlayerTurn(event);
                        break;
                    case REVERT:
                        handleRevert(event);
                        break;
                    case SEQUENCE_CREATED:
                        handleSequenceCreated(event);
                        break;
                    case TILE_ADDED:
                        handleTileAdded(event);
                        break;
                    case TILE_MOVED:
                        handleTileMoved(event);
                        break;
                    case TILE_RETURNED:
                        handleTileReturned(event);
                        break;
                    default:
                        break;
                }
            }
            eventsList.clear();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void handleGameOver() {
        eventsHandler.gameOver();
    }

    private void handleGameStart() throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        PlayerDetails currPlayerDetails = server.getPlayerDetails(playerID);
        playerName = currPlayerDetails.getName();
        List<logic.tile.Tile> currPlayerTiles = convertWS2GameTiles(currPlayerDetails.getTiles());

        List<PlayerDetails> playersDetails = server.getPlayersDetails(gameName);
        List<String> allPlayerNames = new ArrayList<>();

        for (PlayerDetails details : playersDetails) {
            allPlayerNames.add(details.getName());
        }

        eventsHandler.gameStart(playerName, allPlayerNames, currPlayerTiles);
    }

    private void handleGameWinner(Event event) {
        if (!isPlayerNameExistInEvent(event))
            return;

        eventsHandler.gameWinner(event.getPlayerName());
    }

    private void handlePlayerFinishedTurn(Event event) {
        if (!isPlayerNameExistInEvent(event)) {
            return;
        }
        List<logic.tile.Tile> tiles = convertWS2GameTiles(event.getTiles());
        eventsHandler.playerFinishTurn(tiles, event.getPlayerName());
    }

    private void handlePlayerResigned(Event event) {
        if (!isPlayerNameExistInEvent(event))
            return;

        eventsHandler.playerResigned(event.getPlayerName());
    }

    private void handlePlayerTurn(Event event) {
        if (!isPlayerNameExistInEvent(event))
            return;

        eventsHandler.PlayerTurn(event.getPlayerName());
    }

    private void handleRevert(Event event) {
        if (!isPlayerNameExistInEvent(event))
            return;
        eventsHandler.revert(event.getPlayerName());
    }

    private void handleSequenceCreated(Event event) {
        List<logic.tile.Tile> tiles = convertWS2GameTiles(event.getTiles());
        eventsHandler.sequenceCreated(tiles, event.getPlayerName());
    }

    private void handleTileAdded(Event event) {
        if (!isPlayerNameExistInEvent(event))
            return;

        logic.tile.Tile tile = convertWSTileIntoGameTile(event.getTiles().get(0));
        eventsHandler.addTile(event.getPlayerName(),
                event.getTargetSequenceIndex(),
                event.getTargetSequencePosition(),
                tile
        );
    }

    private void handleTileMoved(Event event) {
        eventsHandler.moveTile(
                event.getSourceSequenceIndex(),
                event.getSourceSequencePosition(),
                event.getTargetSequenceIndex(),
                event.getTargetSequencePosition()
        );
    }

    private void handleTileReturned(Event event) {
        if (!isPlayerNameExistInEvent(event))
            return;
        
        logic.tile.Tile tile = convertWSTileIntoGameTile(event.getTiles().get(0));
        eventsHandler.tileReturned(
                event.getPlayerName(),
                event.getSourceSequenceIndex(),
                event.getSourceSequencePosition(),
                tile);
    }

    private List<logic.tile.Tile> convertWS2GameTiles(List<ws.rummikub.Tile> tiles) {
        List<logic.tile.Tile> resList = new ArrayList<>(tiles.size());
        for (Tile tile : tiles) {
            resList.add(new logic.tile.Tile(tile));
        }
        return resList;
    }
    
        private boolean isPlayerNameExistInEvent(Event event) {
        if (playerName == null || playerName.isEmpty()) {
            System.err.println(event.getType().toString() + " came with no playerName");
            return false;
        }

        return true;
    }
}
