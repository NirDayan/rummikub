package javafxrummikub.utils;

import javafx.scene.image.Image;

public class ImageUtils {
    private static final String RESOURCES_DIR = "/resources/";
    private static final String IMAGES_DIR = RESOURCES_DIR + "images/";
    private static final String IMAGE_EXTENSION = ".jpg";
    
    public static Image getImage (String filename){
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        if (!filename.endsWith(IMAGE_EXTENSION)){
            filename = filename + IMAGE_EXTENSION;
        }
        
        return new Image(ImageUtils.class.getResourceAsStream(IMAGES_DIR + filename));
    }
}