package logic.tile;

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
}
