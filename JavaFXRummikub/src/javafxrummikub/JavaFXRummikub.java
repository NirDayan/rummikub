package javafxrummikub;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafxrummikub.scenes.gameplay.GamePlaySceneController;
import javafxrummikub.scenes.newGame.NewGameSceneController;

public class JavaFXRummikub extends Application {
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    Stage primaryStage;
    private Scene newGameScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        gamePlayFxmlLoader = getFXMLLoaderByRelativePath(GAME_PLAY_SCENE_FILE_PATH);
        createNewGameScene();

        primaryStage.setTitle("Welcome to Rumikub!");
        primaryStage.setResizable(false);
        primaryStage.setScene(newGameScene);
        primaryStage.show();
    }

    private void createNewGameScene() {
        FXMLLoader fxmlLoader = getFXMLLoaderByRelativePath(NEW_GAME_SCENE_FILE_PATH);
        Parent newGameRoot = (Parent) fxmlLoader.getRoot();
        NewGameSceneController newGameSceneController = getNewGameSceneController(fxmlLoader);
        registerGamePlayToStartPlayButton(newGameSceneController);
        if (newGameRoot != null) {
            newGameScene = new Scene(newGameRoot, SCENE_WIDTH, SCENE_HEIGHT);
        }
        else {
            //TODO
        }
    }

    private void registerGamePlayToStartPlayButton(NewGameSceneController newGameSceneController) {
        newGameSceneController.isStartPlayPressed().addListener((source, oldValue, isFinished) -> {
            if (isFinished) {
                Scene gamePlayScene = getGamePlayScene();
                GamePlaySceneController gamePlayConroller = getGamePlaySceneController(gamePlayFxmlLoader);
                gamePlayConroller.setGame(newGameSceneController.getGame());
                registerNewGameSceneToMainMenuButton(newGameSceneController, gamePlayConroller);
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
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            //TODO
        }
        return fxmlLoader;
    }

    private NewGameSceneController getNewGameSceneController(FXMLLoader fxmlLoader) {
        NewGameSceneController controller = (NewGameSceneController) fxmlLoader.getController();
        return controller;
    }

    private GamePlaySceneController getGamePlaySceneController(FXMLLoader fxmlLoader) {
        GamePlaySceneController controller = (GamePlaySceneController) fxmlLoader.getController();
        return controller;
    }

    private void registerNewGameSceneToMainMenuButton(NewGameSceneController newGameSceneController, GamePlaySceneController gamePlayConroller) {
        gamePlayConroller.IsMainMenuButtonPressed().addListener((source, oldValue, isExitToMainMenu) -> {
            if (isExitToMainMenu) {
                primaryStage.setScene(newGameScene);
            }
        });
    }

    private Scene getGamePlayScene() {
        Parent gamePlayRoot = (Parent) gamePlayFxmlLoader.getRoot();
        return new Scene(gamePlayRoot, SCENE_WIDTH, SCENE_HEIGHT);
    }
    private FXMLLoader gamePlayFxmlLoader;
}
