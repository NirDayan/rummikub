package logic.tile;

import java.util.Objects;

public class Tile implements Comparable<Tile>{
    protected Color color;
    protected int value;
    
    Tile(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }    

    void initialize() {}
    
    class InvalidTile extends RuntimeException{
    }
    
    @Override
    public int compareTo(Tile other){
        return this.value - other.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.color);
        hash = 79 * hash + this.value;
        return hash;
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
        if (this.color != other.color) {
            return false;
        }
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
    
}
