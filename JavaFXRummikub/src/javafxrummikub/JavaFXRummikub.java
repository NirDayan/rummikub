/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxrummikub;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafxrummikub.scenes.newGame.NewGameSceneController;

public class JavaFXRummikub extends Application {
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final String STYLE_FILE_PATH = "/resources/style.css";
    
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = getNewGameFXMLLoader();
        Parent newGameRoot = null;
        try {
            newGameRoot = getNewGameRoot(fxmlLoader);
        } catch (IOException ex) {
            //TODO
        }
        NewGameSceneController newGameController = getNewGameSceneController(fxmlLoader, primaryStage);

        if (newGameRoot != null) {
            Scene scene = new Scene(newGameRoot, 800,600);
            scene.getStylesheets().add(getClass().getResource(STYLE_FILE_PATH).toExternalForm());
            primaryStage.setTitle("Wellcome to Rumikub!");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        else {
            //TODO
        }        
    }

    public static void main(String[] args) {
        launch(args);
    }    

    private FXMLLoader getNewGameFXMLLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(NEW_GAME_SCENE_FILE_PATH);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }

    private Parent getNewGameRoot(FXMLLoader fxmlLoader) throws IOException {
        return (Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream());
    }

    private NewGameSceneController getNewGameSceneController(FXMLLoader fxmlLoader, Stage primaryStage) {
        NewGameSceneController newGameSceneController = (NewGameSceneController) fxmlLoader.getController();
        
        return newGameSceneController;
    }
}
