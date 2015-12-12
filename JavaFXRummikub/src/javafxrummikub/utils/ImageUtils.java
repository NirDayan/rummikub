package javafxrummikub.utils;

import javafx.scene.image.Image;

public class ImageUtils {
    private static final String RESOURCES_DIR = "/resources/";
    private static final String IMAGES_DIR = RESOURCES_DIR + "images/";
    
    public static Image getImage (String filename){
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        return new Image(ImageUtils.class.getResourceAsStream(IMAGES_DIR + filename));
    }
}