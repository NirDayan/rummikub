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
import logic.Game;

public class JavaFXRummikub extends Application {
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    Stage primaryStage;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene newGameScene = getNewGameScene();

        primaryStage.setTitle("Welcome to Rumikub!");
        primaryStage.setResizable(false);
        primaryStage.setScene(newGameScene);
        primaryStage.show();
    }

    private Scene getNewGameScene() {
        Scene newGameScene = null;
        FXMLLoader fxmlLoader = getFXMLLoaderByRelativePath(NEW_GAME_SCENE_FILE_PATH);
        Parent newGameRoot = (Parent) fxmlLoader.getRoot();
        NewGameSceneController newGameSceneController = getNewGameSceneController(fxmlLoader);
        registerGamePlayToStartPlayButton(newGameSceneController);
        if (newGameRoot != null) {
            newGameScene = new Scene(newGameRoot, SCENE_WIDTH, SCENE_HEIGHT);
        } else {
            //TODO
        }
        
        return newGameScene;
    }

    private NewGameSceneController getNewGameSceneController(FXMLLoader fxmlLoader) {
        NewGameSceneController controller = (NewGameSceneController) fxmlLoader.getController();
        return controller;
    }

    private void registerGamePlayToStartPlayButton(NewGameSceneController newGameSceneController) {
        newGameSceneController.isStartPlayPressed().addListener((source, oldValue, isFinished) -> {
            if (isFinished) {
                Scene gamePlayScene = getGamePlayScene(newGameSceneController.getGame());
                primaryStage.setScene(gamePlayScene);
            }
        });
    }

    private Scene getGamePlayScene(Game game) {
        FXMLLoader gamePlayFxmlLoader = getFXMLLoaderByRelativePath(GAME_PLAY_SCENE_FILE_PATH);
        Parent gamePlayRoot = (Parent) gamePlayFxmlLoader.getRoot();
        GamePlaySceneController gamePlayConroller = getGamePlaySceneController(gamePlayFxmlLoader);
        gamePlayConroller.setGame(game);
        registerNewGameSceneToMainMenuButton(gamePlayConroller);
        return new Scene(gamePlayRoot, SCENE_WIDTH, SCENE_HEIGHT);
    }
    
    private GamePlaySceneController getGamePlaySceneController(FXMLLoader fxmlLoader) {
        GamePlaySceneController controller = (GamePlaySceneController) fxmlLoader.getController();
        return controller;
    }

    private void registerNewGameSceneToMainMenuButton(GamePlaySceneController gamePlayConroller) {
        gamePlayConroller.IsMainMenuButtonPressed().addListener((source, oldValue, isExitToMainMenu) -> {
            if (isExitToMainMenu) {
                primaryStage.setScene(getNewGameScene());
            }
        });
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
}
