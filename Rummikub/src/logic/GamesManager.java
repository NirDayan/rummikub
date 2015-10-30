package logic;

import java.util.ArrayList;
import java.util.Map;

public class GamesManager {
    
    private Map<Game, ArrayList<GameAction>> gamesActions;
    
    private ArrayList<Game> games;
    
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
