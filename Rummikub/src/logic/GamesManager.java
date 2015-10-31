package logic;

import controllers.IController;
import java.util.ArrayList;
import java.util.Map;

public class GamesManager {
    
    private final int FIRST_PLAYER_ID = 1;
    private final int MAX_PLAYERS_NUM = 4;
    private final String COMPUTER_NAME_PREFIX = "Computer#";
    private final IController controller;
    private Map<Game, ArrayList<GameAction>> gamesActions;
    private ArrayList<Game> games;
    private ArrayList<Player> players;
    private int nextPlayerId;
    
    public GamesManager (IController controller) {
        this.players = new ArrayList<Player>();
        this.games = new ArrayList<Game>();
        this.controller = controller;
        this.nextPlayerId = FIRST_PLAYER_ID;
    }
    
    public void start() {
        GameDetails initialUserInput = controller.getInitialGameInput();
        if (isGameInputValid(initialUserInput)){
            createGame(initialUserInput);
        }
        else {
            //TODO: continue...
        }
    }
    
    public void createGame (GameDetails gameDetails) {        
        Game game = new Game();
        createPlayers(gameDetails, game);        
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
    
    private void createPlayers(GameDetails gameDetails, Game game) {
        int currPlayerID;
        String currPlayerName;
        Player currPlayer;
        int humanPlayersNum = gameDetails.getHumenPlayersNum();
        int computerPlayerIndex = 1;
        
        for (int i = 0; i < gameDetails.getTotalPlayersNumber(); i++) {
            currPlayerID = generatePlayerId();
            //first create human players
            if (humanPlayersNum > 0) {
                currPlayerName = gameDetails.getPlayersNames()[i];
                currPlayer = new HumanPlayer(currPlayerID, currPlayerName);
                humanPlayersNum--;                
            }
            else {
                //create computer player
                currPlayerName = COMPUTER_NAME_PREFIX + computerPlayerIndex;
                currPlayer = new ComputerPlayer(currPlayerID, currPlayerName);
                computerPlayerIndex++;
            }
            
            players.add(currPlayer);
            game.addPlayer(currPlayer);//add the current player into the game
        }        
    }
    
    private int generatePlayerId() {
        return (nextPlayerId)++;
    }
    
    //TODO: more edge cases???
    //TODO: check load from file flow.. currently it unhandeled
    public boolean isGameInputValid (GameDetails input) {
        String [] playerNames = input.getPlayersNames();
                
        if (input.getTotalPlayersNumber() < 2 || input.getTotalPlayersNumber() > MAX_PLAYERS_NUM)
            return false;
        if (playerNames.length < input.getHumenPlayersNum())
            return false;        
        //check names validity and each name is unique
        for (int i = 0; i < input.getHumenPlayersNum(); i++) {
            if (playerNames[i].isEmpty())
                return false;
            if (playerNames[i].startsWith(COMPUTER_NAME_PREFIX))
                return false;
            //check names are unique
            for (int j = i + 1; j < input.getHumenPlayersNum(); j++) {
                if (playerNames[i] == playerNames[j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
