package logic;

import controllers.IController;
import java.util.ArrayList;
import java.util.Map;

public class GamesManager {
    
    private final IController controller;
    private Map<Game, ArrayList<GameAction>> gamesActions;
    private ArrayList<Game> games;
    
    public GamesManager (IController controller) {
        this.controller = controller;
    }
    
    public void start() {
        GameDetails initialUserInput = controller.getInitialGameInput();
        if (Game.isGameInputValid(initialUserInput)){
            //TODO: continue...
        }
        else {
            //TODO: continue...
        }
    }
    
    public Game createGame (String gameName, int computerPlayers, int humanPlayers) {
        return null;
    }
    
    public String createGameFromXML (String xmlData) {
        return "";
    }
    
    public GameDetails getGameDetails (String gameName) {
        return null;
    }
    
    public String[] getWaitingGames() {
        return null;
    }
    
    public int joinGame (String gameName, String playerName) {
        return 0;
    }
    
    public PlayerDetails[] getPlayersDetails(String gameName) {
        return null;
    }
    
    public PlayerDetails getPlayerDetails(int playerID) {
        return null;
    }
    
    public Event[] getEvents (int playerID, int eventID) {
        return null;
    }
    
    public void manageGames() {
        
    }
}
