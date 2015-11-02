package logic;

import java.util.ArrayList;
import controllers.IController;

public class Game {
    
    private static ArrayList<Player> allPlayers = new ArrayList<Player>();
    private ArrayList<Player> gamePlayers;
    private TilesDeck tilesDeck;
    private Board board;
    private Player currentPlayer;
    private IController controller;
    private static int nextPlayerID = 1;
    private final int MAX_PLAYERS_NUM = 4;
    private final String COMPUTER_NAME_PREFIX = "Computer#";
    
    public static enum Status {
        WAIT,
        ACTIVE
    };
    
    public Game(IController controller) {
        this.gamePlayers = new ArrayList<Player>();
        this.currentPlayer = null;
        this.controller = controller;
    }
    
    public Game init() {
        Game result = null;
        boolean isGameInitialized = false;
        
        while (!isGameInitialized) {
            GameDetails initialUserInput = controller.getInitialGameInput();
            if (isGameInputValid(initialUserInput)) {
                createPlayers(initialUserInput);
                isGameInitialized = true;
            }
            else {
                controller.showWrongInitialGameInput();
            }
        }        
        
        return result;
    }
    
    public TilesSequence createSequence (int playerID, Tile[] tiles) {
        return null;
    }
    
    public void addTile (int playerID, Tile tile, int sequenceIndex, int sequencePosition) {
        
    }
    
    public void moveTile (int playerID, int sourceSequenceIndex,
            int sourceSequencePosition, int targetSequenceIndex,
            int targetSequencePosition) {
    }
    
    public void finishTurn (int playerID) {
        
    }
    
    public void resign (int playerID) {
        
    }
    
    public void addPlayer(Player player) {
        if (currentPlayer == null) {
            currentPlayer = player;            
        }
        allPlayers.add(player);
        gamePlayers.add(player);
    }
    
    //TODO: more edge cases???
    //TODO: check load from file flow.. currently it unhandeled
    private boolean isGameInputValid (GameDetails input) {
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
    
    private void createPlayers(GameDetails gameDetails) {
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
            
            addPlayer(currPlayer);
        }        
    }
    
    private int generatePlayerId() {
        return (nextPlayerID)++;
    }
}
