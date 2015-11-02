package logic;

import controllers.IController;
import java.util.ArrayList;
import java.util.Map;

public class GamesManager {

    private final IController controller;
    private Map<Game, ArrayList<GameAction>> gamesActions;
    private ArrayList<Game> games;
    
    public GamesManager (IController controller) {
        this.games = new ArrayList<Game>();
        this.controller = controller;
    }
    
    public void start() {       
        Game game = new Game(controller);
        game.init();
        games.add(game);
        
        manageGames();
    }
    
    public void createGame (GameDetails gameDetails) {        

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
        for(Game game : games) {
            
        }
    }    
}
