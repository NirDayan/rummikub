package javafxrummikub.components;

import javafx.scene.control.ListCell;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafxrummikub.utils.ImageUtils;
import logic.tile.Tile;

public class TileView extends ListCell<Tile> {
    
    private static final int TILE_WIDTH = 45;
    private static final int PLUS_TILE_WIDTH = 25;
    private static final int TILE_HEIGHT = 65;
    
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
        result.setFitHeight(TILE_HEIGHT);
        if (tile.isPlusTile()) {
            result.setFitWidth(PLUS_TILE_WIDTH);
        } else {
            result.setFitWidth(TILE_WIDTH);
        }
        
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
            return ImageUtils.getImage("plus_tile.png");                        
        }
        
        return ImageUtils.getImage("tiles/" + color + "/" + tile.getValue() + ".png");
    }
}
