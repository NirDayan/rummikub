package javafxrummikub.scenes.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import ws.rummikub.Event;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.PlayerDetails;
import ws.rummikub.RummikubWebService;
import ws.rummikub.Tile;

public class GamePlayEventsMgr {
    private String playerName;
    private static final int INTERVAL = 5;
    private IGamePlayEventHandler eventsHandler;
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

    public void updateEvents() {
        try {
            //get events from server.
            eventsList = server.getEvents(playerID, eventIndex);
            //for each event, handle it with the eventsHandler.
            for (Event event : eventsList) {
                eventIndex++;
                switch (event.getType()) {
                    case GAME_OVER:
                        break;
                    case GAME_START:
                        handleGameStart();
                        break;
                    case GAME_WINNER:
                        break;
                    case PLAYER_FINISHED_TURN:
                        break;
                    case PLAYER_RESIGNED:
                        break;
                    case PLAYER_TURN:
                        break;
                    case REVERT:
                        break;
                    case SEQUENCE_CREATED:
                        break;
                    case TILE_ADDED:
                        break;
                    case TILE_MOVED:
                        break;
                    case TILE_RETURNED:
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void handleGameStart() throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        PlayerDetails currPlayerDetails = server.getPlayerDetails(playerID);
        playerName = currPlayerDetails.getName();
        List<Tile> currPlayerTiles = currPlayerDetails.getTiles();
        
        List<PlayerDetails> playersDetails = server.getPlayersDetails(gameName);
        List<String> allPlayerNames = new ArrayList<>();
        
        for (PlayerDetails details : playersDetails) {
            allPlayerNames.add(details.getName());
        }
        
        eventsHandler.gameStart(playerName, allPlayerNames,currPlayerTiles);
    }

}
