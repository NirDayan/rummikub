package logic.tile;

import java.util.Objects;

public class Tile implements Comparable<Tile> {
    public static final int JOKER_VALUE = 0;
    protected Color color;
    protected int value;

    public Tile(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    public boolean isJoker() {
        return value == JOKER_VALUE;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
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
