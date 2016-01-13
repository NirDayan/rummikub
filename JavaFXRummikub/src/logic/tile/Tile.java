package logic.tile;

import java.util.Objects;

public class Tile implements Comparable<Tile> {
    public static final int JOKER_VALUE = 0;
    public static int PLUS_TILE = -1;
    protected Color color;
    protected int value;
    private boolean isHovered;

    public Tile(Color color, int value) {
        this.color = color;
        this.value = value;
    }
    
    public Tile(ws.rummikub.Tile tile){
        this.color = Color.toColor(tile.getColor());
        this.value = tile.getValue();
    }

    public boolean isJoker() {
        return value == JOKER_VALUE;
    }
    
    public boolean isPlusTile() {
        return value == PLUS_TILE;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
    
    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
    }
    
    public boolean isHovered() {
        return isHovered;
    }

    @Override
    public int compareTo(Tile other) {
        return this.value - other.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile) obj;
        if (this.value != other.value) {
            return false;
        }
        if (isJoker()) {
            return true;
        }
        if (this.color != other.color) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.color);
        hash = 97 * hash + this.value;
        return hash;
    }
    
}
