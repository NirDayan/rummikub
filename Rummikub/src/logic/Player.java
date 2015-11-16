package logic;

import java.util.ArrayList;
import java.util.List;
import logic.tile.Sequence;
import logic.tile.Tile;

public abstract class Player {

    private final String name;
    private final int ID;
    private final ArrayList<Tile> tiles;
    private boolean isResign;
    private boolean isFirstStep;

    Player(int ID, String name) {
        this.ID = ID;
        this.name = name;
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
        return name;
    }

    public int getID() {
        return ID;
    }

    public void reset() {
        tiles.clear();
        isResign = false;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public Tile removeTile(int index) {
        if (index < tiles.size() && index >= 0) {
            return tiles.remove(index);
        }

        return null;
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

    public void removeTiles(List<Integer> tilesIndices) {
        if (tilesIndices != null) {
            List<Tile> tilesToRemove = new ArrayList<>();
            for (Integer index : tilesIndices) {
                tilesToRemove.add(tiles.get(index));
            }
            tiles.removeAll(tilesToRemove);
        }
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
}
