package javafxrummikub.scenes.gameplay;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import ws.rummikub.Event;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.RummikubWebService;

public class GamePlayEventsMgr {
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

}
