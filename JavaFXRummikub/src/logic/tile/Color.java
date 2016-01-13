package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Color {

    Blue,
    Red,
    Yellow,
    Black;

    static public List<Color> getColorsList() {
        List<Color> list = new ArrayList<>();
        list.addAll(Arrays.asList(Color.values()));
        return list;
    }

    static public Color toColor(ws.rummikub.Color color) {
        switch (color) {
            case BLACK:
                return Black;
            case BLUE:
                return Blue;
            case RED:
                return Red;
            case YELLOW:
            default:
                return Yellow;
        }
    }
}
