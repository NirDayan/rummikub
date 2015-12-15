package javafxrummikub.components;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafxrummikub.utils.ImageUtils;
import logic.tile.Tile;

public class TileView extends Label {
    private final Tile tile;

    public TileView(Tile tile) {
        this.tile = tile;
        setAlignment(Pos.CENTER_LEFT);
        setGraphic(createImage());
        if (tile.isJoker()) {
            setText("J");
        }
        else {
            setText(String.format("%d", tile.getValue()));
            Paint paint = getColorByTileData();
            setTextFill(paint);
        }
    }

    public void drawTile() {
        setTextAlignment(TextAlignment.CENTER);
        setGraphic(createImage());
        setContentDisplay(ContentDisplay.TOP);
        if (tile.isJoker()) {
            setText("J");
        } else {
            setText(String.format("%d", tile.getValue()));
//            Paint paint = tilegetColorByTileData();
//            setTextFill(paint);
        }
        getStyleClass().add("TileCellView");
    }
    
    public boolean isJoker() {
        return tile.isJoker();
    }

    public ImageView createImage() {
        ImageView result = new ImageView(getImage());
        final double heightWidthRatio = 0.7018;
        int tileHeight = 70;
        result.setFitHeight(tileHeight);
        result.setFitWidth(tileHeight * heightWidthRatio);
        return result;
    }

    private Image getImage() {
        return ImageUtils.getImage("empty_tile.png");
    }

    private Color getColorByTileData() {
        switch (tile.getColor()) {
            case Black:
                return Color.BLACK;
            case Blue:
                return Color.BLUE;
            case Red:
                return Color.RED;
            case Yellow:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }

    public int getValue() {
        return tile.getValue();
    }
}
