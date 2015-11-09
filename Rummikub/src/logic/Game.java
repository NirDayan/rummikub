package logic;

import java.util.ArrayList;
import controllers.IController;
import java.util.Iterator;
import java.util.List;
import logic.tile.*;
import logic.Board.sequenceNotFoundException;
import logic.persistency.FileDetails;
import logic.persistency.IPersistencyListener;
import logic.persistency.PersistencyEvent;

public class Game {
    private ArrayList<Player> players;
    private Deck tilesDeck;
    private Board board;
    private Player currentPlayer;
    private Player winner;
    private IController controller;
    private static int nextPlayerID = 1;
    private final int MAX_PLAYERS_NUM = 4;
    private final String COMPUTER_NAME_PREFIX = "Computer#";
    private final int INITIAL_TILES_COUNT = 14;
    private boolean isPersisted;
    private List<IPersistencyListener> saveListeners;

    public Game(IController controller) {
        this.controller = controller;
        saveListeners = new ArrayList<IPersistencyListener>();
    }

    public Game initNewGame() {
        players = new ArrayList<Player>();
        currentPlayer = null;
        tilesDeck = new Deck();
        board = new Board();
        isPersisted = false;
        Game result = null;
        boolean isGameInitialized = false;

        while (!isGameInitialized) {
            GameDetails initialUserInput = controller.getNewGameInput();
            if (isGameInputValid(initialUserInput)) {
                createPlayers(initialUserInput);
                distributeTiles();
                isGameInitialized = true;
            } else {
                controller.showWrongNewGameInput();
            }
        }

        return result;
    }
    
    public void reset() {
        for(Player player : players) {
            player.reset();
        }
        board.reset();
        tilesDeck.reset();
        winner = null;
        currentPlayer = players.get(0);
        isPersisted = false;
    }

    // Returns the Sequence index to the controller
    public int createSequence(int playerID, Tile[] tiles) {
        return 0;
    }

    public void addTile(AddTileData data) {
        board.addTile(data);
    }

    public void moveTile(MoveTileData data) {
        board.moveTile(data);
    }

    public void finishTurn(int playerID) {
        board.finishTurn();
    }

    public void resign(int playerID) {

    }

    public void addPlayer(Player player) {
        if (currentPlayer == null) {
            currentPlayer = player;
        }
        players.add(player);
    }

    public void distributeTiles() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_TILES_COUNT; i++) {
                player.addTile(tilesDeck.pullTile());
            }
        }
    }
    
    public void play() {
        while (!checkIsGameOver()) {
            //loop over the players starting from the currentPlayer
            if (!currentPlayer.isResign()) {
                controller.showGameStatus(board, currentPlayer);
                if (controller.isPlayerResign(currentPlayer)) {//What do we need to do with the player tiles?
                     currentPlayer.setIsResign(true);
                 }
                else {
                    handleGameSaving(currentPlayer);
                    //TODO: continue the game flow...
                }
            }
            setNextPlayer();
        }        
        controller.showEndOfGame(winner);
    }
    
    public void setNextPlayer() {
        int currPlayerIndex = players.indexOf(currentPlayer);
        if (currPlayerIndex == players.size() - 1) {
            currentPlayer = players.get(0);
        }
        else {
            currentPlayer = players.get(currPlayerIndex + 1);
        }
    }
    
    public synchronized void addEventListener(IPersistencyListener listener)  {
      saveListeners.add(listener);
    }
    
    public synchronized void removeEventListener(IPersistencyListener listener)   {
      saveListeners.remove(listener);
    }
    
    public void handleGameSaving(Player player) {
        if (player instanceof HumanPlayer) {
            FileDetails fileDetails = controller.askUserToSaveGame(isPersisted);
            if (fileDetails != null && fileDetails.isNewFile()) {
                fireSaveEvent(fileDetails);
                isPersisted = true;
            }
        }
    }

    private boolean checkIsGameOver() {        
        //if there is a winner
        if (currentPlayer.isFinished()) {
            winner = currentPlayer;
            return true;
        }
        //if the deck is empty
        if (tilesDeck.isEmpty()) {
            return true;
        }
        //if all players resigned
        int resignedPlayersCount = 0;
        for(Player player : players) {
            if (player.isResign()) {
                resignedPlayersCount++;
            }
        }
        if (resignedPlayersCount == players.size())
            return true;
        
        return false;
    }

    //TODO: more edge cases???
    //TODO: check load from file flow.. currently it unhandeled
    //Lior: Maybe a better place is in the controller / gameManager ?
    private boolean isGameInputValid(GameDetails input) {
        String[] playerNames = input.getPlayersNames();

        if (input.getTotalPlayersNumber() < 2 || input.getTotalPlayersNumber() > MAX_PLAYERS_NUM) {
            return false;
        }
        if (playerNames.length < input.getHumenPlayersNum()) {
            return false;
        }
        //check names validity and each name is unique
        for (int i = 0; i < input.getHumenPlayersNum(); i++) {
            if (playerNames[i].isEmpty()) {
                return false;
            }
            if (playerNames[i].startsWith(COMPUTER_NAME_PREFIX)) {
                return false;
            }
            //check names are unique
            for (int j = i + 1; j < input.getHumenPlayersNum(); j++) {
                if (playerNames[i].equals(playerNames[j])) {
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
            } else {
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
    
    private synchronized void fireSaveEvent(FileDetails fileDetails) {
        PersistencyEvent event = new PersistencyEvent(this, fileDetails);
        Iterator listeners = saveListeners.iterator();
        while( listeners.hasNext() ) {
            ((IPersistencyListener) listeners.next()).handleSaveGame(event);
        }
    }
}