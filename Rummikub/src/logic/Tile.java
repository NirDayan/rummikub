package logic;

enum Color{
    Blue,
    Red,
    Yellow,
    Black,
    Joker
}

public class Tile {
    private final Color color;
    private final int value;

    public Tile(Color color){
        this(Color.Joker,1);
        if (color != Color.Joker)
            throw new InvalidTile();
    }
    
    public Tile(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }    
    
    public class InvalidTile extends RuntimeException{
    }
}
