package javafxrummikub;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafxrummikub.scenes.gameplay.GamePlayScene;
import javafxrummikub.scenes.newGame.NewGameSceneController;

public class JavaFXRummikub extends Application {
    Stage primaryStage;
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final String STYLE_FILE_PATH = "/resources/style.css";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = getFXMLLoaderByRelativePath(NEW_GAME_SCENE_FILE_PATH);
        Parent newGameRoot = null;
        try {
            newGameRoot = getParentByXmlLoader(fxmlLoader);
        } catch (IOException ex) {
            //TODO
        }
        NewGameSceneController newGameController = getNewGameSceneController(fxmlLoader);

        if (newGameRoot != null) {
            Scene scene = new Scene(newGameRoot, 800, 600);
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

    private FXMLLoader getFXMLLoaderByRelativePath(String path) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(path);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }

    private Parent getParentByXmlLoader(FXMLLoader fxmlLoader) throws IOException {
        return (Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream());
    }

    private NewGameSceneController getNewGameSceneController(FXMLLoader fxmlLoader) {
        NewGameSceneController newGameSceneController = (NewGameSceneController) fxmlLoader.getController();
        newGameSceneController.getFinishedInit().addListener((source, oldValue, isFinished) -> {
            if (isFinished) {
                primaryStage.setScene(getGamePlayScene());
            }
        });
        return newGameSceneController;
    }

    private Scene getGamePlayScene() {
        FXMLLoader fxmlLoader = getFXMLLoaderByRelativePath(GAME_PLAY_SCENE_FILE_PATH);
        Parent gamePlayRoot = null;
        try {
            gamePlayRoot = getParentByXmlLoader(fxmlLoader);
        } catch (IOException ex) {
            //TODO
        }
        return new GamePlayScene(gamePlayRoot, 800, 600);
    }
}
