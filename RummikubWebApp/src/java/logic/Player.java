package logic;

import java.util.ArrayList;
import java.util.List;
import logic.tile.Tile;
import ws.rummikub.PlayerStatus;

public class Player {

    private final PlayerDetails details;
    private ArrayList<Tile> tiles;
    private ArrayList<Tile> backupTiles;
    private boolean isResign;
    private boolean isFirstStep;

    public Player(int ID, String name, boolean isHuman) {
        this.details = new PlayerDetails(ID, name, isHuman, PlayerStatus.RETIRED);
        this.tiles = new ArrayList<>();
        this.isFirstStep = true;
    }
    
    public Player(PlayerDetails playerDetails) {
        this.details = playerDetails;
        this.tiles = new ArrayList<>();
        this.isFirstStep = true;
    }

    public void setIsResign(boolean isResign) {
        this.isResign = isResign;
    }

    public boolean isResign() {
        return isResign;
    }

    public void addTile(Tile tile) {
        if (tile != null)
            tiles.add(tile);
    }

    public boolean isFinished() {
        return tiles.isEmpty();
    }

    public String getName() {
        return details.getName();
    }

    public int getID() {
        return details.getID();
    }
    
    public void setID(int ID) {
        details.setID(ID);
    }

    public void reset() {
        tiles.clear();
        isResign = false;
        isFirstStep = true;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public boolean removeTile(Tile tile) {
        return tiles.remove(tile);
    }

    public boolean isFirstStep() {
        return isFirstStep;
    }

    public void setFirstStepCompleted(boolean isCompleted) {
        isFirstStep = !isCompleted;
    }

    public List<Tile> getTilesByIndices(List<Integer> tilesIndices) {
        if (!isTileIndicesValid(tilesIndices))
            return null;

        List<Tile> list = new ArrayList<>();
        for (Integer index : tilesIndices) {
            list.add(tiles.get(index));
        }

        return list;
    }

    public void removeTilesByIndices(List<Integer> tilesIndices) {
        if (tilesIndices != null) {
            List<Tile> tilesToRemove = new ArrayList<>();
            for (Integer index : tilesIndices) {
                tilesToRemove.add(tiles.get(index));
            }
            tiles.removeAll(tilesToRemove);
        }
    }
    
    public void removeTiles(List<Tile> tilesToRemove) {
        tiles.removeAll(tilesToRemove);
    }

    private boolean isTileIndicesValid(List<Integer> tilesIndices) {
        if (tilesIndices == null)
            return false;

        for (Integer index : tilesIndices) {
            if (index < 0 || index >= tiles.size())
                return false;
        }

        return true;
    }
    
    public boolean isHuman(){
        return details.getIsHuman();
    }

    public void storeBackup() {
        backupTiles = new ArrayList<>(tiles);
    }
    
    public void restoreFromBackup() {
        if (backupTiles != null) {
            tiles = backupTiles;
        }        
    }
    
    public PlayerStatus getStatus() {
        return details.getStatus();
    }
    
    public void setStatus(PlayerStatus status) {
        details.setStatus(status);
    }
}
