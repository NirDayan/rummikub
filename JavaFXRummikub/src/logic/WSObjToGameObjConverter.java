package logic;

import java.util.ArrayList;
import java.util.List;
import ws.rummikub.Tile;

public class WSObjToGameObjConverter {

    public static ws.rummikub.Color convertGameColorIntoGeneratedColor(logic.tile.Color color) {
        switch (color) {
            case Black:
                return ws.rummikub.Color.BLACK;
            case Red:
                return ws.rummikub.Color.RED;
            case Blue:
                return ws.rummikub.Color.BLUE;
            case Yellow:
                return ws.rummikub.Color.YELLOW;
            default:
                return ws.rummikub.Color.BLACK;
        }
    }

    public static logic.tile.Color convertGeneratedColorIntoGameColor(ws.rummikub.Color color) {
        switch (color) {
            case BLACK:
                return logic.tile.Color.Black;
            case RED:
                return logic.tile.Color.Red;
            case BLUE:
                return logic.tile.Color.Blue;
            case YELLOW:
                return logic.tile.Color.Yellow;
            default:
                return logic.tile.Color.Black;
        }
    }

    public static List<logic.tile.Tile> convertGeneratedTilesListIntoGameTiles(List<ws.rummikub.Tile> tiles) {
        List<logic.tile.Tile> tilesList = new ArrayList<>();
        logic.tile.Tile newTile;
        for (ws.rummikub.Tile tile : tiles) {
            newTile = convertWSTileIntoGameTile(tile);
            tilesList.add(newTile);
        }

        return tilesList;
    }

    public static List<ws.rummikub.Tile> convertGameTilesListIntoGeneratedTilesList(List<logic.tile.Tile> tiles) {
        List<ws.rummikub.Tile> tilesList = new ArrayList<>();
        for (logic.tile.Tile tile : tiles) {
            Tile newTile = new ws.rummikub.Tile();
            newTile.setColor(convertGameColorIntoGeneratedColor(tile.getColor()));
            newTile.setValue(tile.getValue());
            tilesList.add(newTile);
        }

        return tilesList;
    }

    public static logic.tile.Tile convertWSTileIntoGameTile(ws.rummikub.Tile tile) {
        logic.tile.Color color = WSObjToGameObjConverter.convertGeneratedColorIntoGameColor(tile.getColor());
        logic.tile.Tile logicTile = new logic.tile.Tile(color, tile.getValue());

        return logicTile;
    }

    public static ws.rummikub.Tile convertGameTileIntoGeneratedTile(logic.tile.Tile tile) {
        Tile newTile = new ws.rummikub.Tile();
        newTile.setColor(convertGameColorIntoGeneratedColor(tile.getColor()));
        newTile.setValue(tile.getValue());

        return newTile;
    }
}
