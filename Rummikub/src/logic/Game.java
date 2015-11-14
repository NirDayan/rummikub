package logic;

import java.util.ArrayList;
import logic.tile.*;

public class Game {
    private final ArrayList<Player> players;
    private final Deck tilesDeck;
    private final Board board;
    private Player currentPlayer;
    private Player winner;
    private static int nextPlayerID = 1;
    private final String COMPUTER_NAME_PREFIX = "Computer#";
    private final int INITIAL_TILES_COUNT = 14;

    public Game(GameDetails gameDetails) {
        players = new ArrayList<>();
        currentPlayer = null;
        tilesDeck = new Deck();
        board = new Board();
        
        createPlayers(gameDetails);
        distributeTiles();
    }

    public void reset() {
        for(Player player : players) {
            player.reset();
        }
        board.reset();
        tilesDeck.reset();
        winner = null;
        currentPlayer = players.get(0);
    }

    public void addPlayer(Player player) {
        if (currentPlayer == null) {
            currentPlayer = player;
        }
        players.add(player);
    }

    public boolean checkIsGameOver() {        
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
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public Player getWinner() {
        return winner;
    }
    
    public void moveToNextPlayer() {
        int currPlayerIndex = players.indexOf(currentPlayer);
        if (currPlayerIndex == players.size() - 1) {
            currentPlayer = players.get(0);
        }
        else {
            currentPlayer = players.get(currPlayerIndex + 1);
        }
    }
    
    public void pullTileFromDeck(int playerID) {
        Player player = getPlayerByID(playerID);
        if (player != null)
            player.addTile(tilesDeck.pullTile());
    }
    
    private void distributeTiles() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_TILES_COUNT; i++) {
                player.addTile(tilesDeck.pullTile());
            }
        }
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
    
    public boolean moveTile(MoveTileData data) {
        return board.moveTile(data);
    }
    
    public boolean addTile(int playerID, MoveTileData addTileData) {
        Player player = getPlayerByID(playerID);
        Tile tile;
        int seqIndex = addTileData.getTargetSequenceIndex();
        int seqPosition = addTileData.getTargetSequencePosition();
        
        if (player != null && board.isTargetValid(seqIndex, seqPosition)) {
            tile = player.removeTile(addTileData.getSourceSequencePosition());
            if (tile != null) {
                return board.addTile(addTileData.getTargetSequenceIndex(), 
                        addTileData.getTargetSequencePosition(), tile);
            }
        }
        
        return false;
    }
    
    public void playerResign(int playerID) {
        Player player = getPlayerByID(playerID);
        if (player != null) {
            player.setIsResign(true);
        }
    }

    private int generatePlayerId() {
        return (nextPlayerID)++;
    }
    
    private Player getPlayerByID (int playerID) {
        for(Player pl : players) {
            if (pl.getID() == playerID) 
                return pl;
        }
        
        return null;
    }
}