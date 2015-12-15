package javafxrummikub.components;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafxrummikub.utils.ImageUtils;
import logic.tile.Tile;

public class TileView extends ListCell<Tile> {
    @Override
    public void updateItem(Tile tile, boolean empty) {
        super.updateItem(tile, empty);
        if (tile != null) {
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
}
