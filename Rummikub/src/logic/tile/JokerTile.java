package logic.tile;

public class JokerTile extends Tile {

    public static final int JOKER_INITIAL_VALUE = 0;

    public JokerTile() {
        super(Color.Black, JOKER_INITIAL_VALUE);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void initialize() {
        this.value = JOKER_INITIAL_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        //Joker is any Joker, therefore fields are not importnant
        return getClass() == obj.getClass();
    }
}
