package logic;

import java.util.ArrayList;

public class Game {
    
    private ArrayList<Player> players;
    private TilesDeck tilesDeck;
    private Board board;
    private Player currentPlayer;
    
    public Game() {
        this.players = new ArrayList<Player>();
        this.currentPlayer = null;
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
        players.add(player);
    }
}
