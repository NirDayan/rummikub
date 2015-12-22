package javafxrummikub.components;

import javafx.scene.control.ListCell;
import javafx.scene.effect.BlendMode;
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
            setGraphic(createImage(tile));
            getStyleClass().add("TileCellView");
            this.setOnDragOver(event -> {
                if (tile.isPlusTile()) {
                    tile.setHovered(true);
                    this.setBlendMode(BlendMode.GREEN);
                }            
            });
            
            this.setOnDragExited(event -> {
                tile.setHovered(false);
                this.setBlendMode(null);
            });
        } else {
            setGraphic(null);
        }
    }

    private ImageView createImage(Tile tile) {
        ImageView result = new ImageView(getImage(tile));
        final double heightWidthRatio = 0.7018;
        int tileHeight = 65;
        result.setFitHeight(tileHeight);
        result.setFitWidth(tileHeight * heightWidthRatio);
        return result;
    }

    private Image getImage(Tile tile) {
        String color = null;
        switch(tile.getColor()) {
            case Blue:
                color = "blue";
                break;
            case Red:
                color = "red";
                break;
            case Yellow:
                color = "yellow";
                break;
            case Black:
                color = "black";
                break;
        }
        if (tile.isJoker()) {
            return ImageUtils.getImage("tiles/" + color + "/joker" + ".png");
        } else if (tile.isPlusTile()) {
            return ImageUtils.getImage("empty_tile.png");                        
        }
        
        return ImageUtils.getImage("tiles/" + color + "/" + tile.getValue() + ".png");
    }
}
