package servlets.utils;

import ws.rummikub.Color;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.Tile;

public class GameObjectsConvertor {

    private static final String BLACK = "BLACK";
    private static final String BLUE = "BLUE";
    private static final String RED = "RED";
    private static final String YELLOW = "YELLOW";
    private static final String INVALID_COLOR_ERR_MSG = "Invalid Color";
    
    public static Tile getTile(String color, int value) throws InvalidParameters_Exception {
        Tile tile = new Tile();
        tile.setValue(value);

        switch (color.toUpperCase()) {
            case BLUE:
                tile.setColor(Color.BLUE);
                break;
            case BLACK:
                tile.setColor(Color.BLACK);
                break;
            case YELLOW:
                tile.setColor(Color.YELLOW);
                break;
            case RED:
                tile.setColor(Color.RED);
                break;
            default:
                throw new InvalidParameters_Exception(INVALID_COLOR_ERR_MSG, null);
        }

        return tile;
    }
}
