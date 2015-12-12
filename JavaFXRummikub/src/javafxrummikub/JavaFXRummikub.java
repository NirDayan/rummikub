package javafxrummikub;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafxrummikub.scenes.gameplay.GamePlaySceneController;
import javafxrummikub.scenes.newGame.NewGameSceneController;

public class JavaFXRummikub extends Application {
    Stage primaryStage;
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

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
        NewGameSceneController newGameSceneController = getNewGameSceneController(fxmlLoader);
        registerGamePlayToStartPlayButton(newGameSceneController);

        if (newGameRoot != null) {
            Scene scene = new Scene(newGameRoot, SCENE_WIDTH, SCENE_HEIGHT);
            primaryStage.setTitle("Welcome to Rumikub!");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        else {
            //TODO
        }
    }

    private void registerGamePlayToStartPlayButton(NewGameSceneController newGameSceneController) {
        newGameSceneController.isStartPlayPressed().addListener((source, oldValue, isFinished) -> {
            if (isFinished) {
                FXMLLoader gamePlayFxmlLoader = getFXMLLoaderByRelativePath(GAME_PLAY_SCENE_FILE_PATH);
                Parent gamePlayRoot = null;
                try {
                    gamePlayRoot = getParentByXmlLoader(gamePlayFxmlLoader);
                } catch (IOException ex) {
                    System.err.println("getParentByXmlLoader(gamePlayFxmlLoader) was failed");
                }
                Scene gamePlayScene = new Scene(gamePlayRoot, SCENE_WIDTH, SCENE_HEIGHT);
                GamePlaySceneController gamePlayConroller = getGamePlaySceneController(gamePlayFxmlLoader);
                gamePlayConroller.setGame(newGameSceneController.getGame());
                primaryStage.setScene(gamePlayScene);
            }
        });
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
        NewGameSceneController controller = (NewGameSceneController) fxmlLoader.getController();
        return controller;
    }

    private GamePlaySceneController getGamePlaySceneController(FXMLLoader fxmlLoader) {
        GamePlaySceneController controller = (GamePlaySceneController) fxmlLoader.getController();
        return controller;
    }
}
