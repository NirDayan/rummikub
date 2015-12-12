package javafxrummikub.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafxrummikub.utils.ImageUtils;
import logic.tile.Tile;

public class TileView extends HBox {
    private final Tile tile;

    public TileView(Tile tile) {
        this.tile = tile;
        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(createImage(), createLabel());
    }

    private Label createLabel() {
        Label result = new Label();
        if (tile.isJoker()) {
            result.setText("J");
        }
        else {
            result.setText(String.format("%d", tile.getValue()));
            Paint paint = getColorByTileData();
            result.setTextFill(paint);
        }
        return result;
    }

    private ImageView createImage() {
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
}
