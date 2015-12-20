package javafxrummikub;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafxrummikub.scenes.gameplay.GamePlaySceneController;
import javafxrummikub.scenes.newGame.NewGameSceneController;
import javafxrummikub.scenes.winner.WinnerSceneController;
import logic.Game;

public class JavaFXRummikub extends Application {
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final String WINNER_SCENE_FILE_PATH = "/javafxrummikub/scenes/winner/winnerScene.fxml";

    private final int sceneWidth = 800;
    private int sceneHeight;
    private static final int HEIGHT_PADDING = 50;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        sceneHeight = (int) (screenBounds.getHeight() - HEIGHT_PADDING);
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
            newGameScene = new Scene(newGameRoot, sceneWidth, sceneHeight);
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
        registerNewGameSceneToMainMenuButton(gamePlayConroller.IsMainMenuButtonPressed());
        registerWinnerSceneToIsGameOver(gamePlayConroller);
        return new Scene(gamePlayRoot, sceneWidth, sceneHeight);
    }

    private GamePlaySceneController getGamePlaySceneController(FXMLLoader fxmlLoader) {
        GamePlaySceneController controller = (GamePlaySceneController) fxmlLoader.getController();
        return controller;
    }

    private void registerNewGameSceneToMainMenuButton(SimpleBooleanProperty isMainMenu) {
        isMainMenu.addListener((source, oldValue, isExitToMainMenu) -> {
            if (isExitToMainMenu) {
                primaryStage.setScene(getNewGameScene());
            }
        });
    }

    private void registerWinnerSceneToIsGameOver(GamePlaySceneController gamePlayConroller) {
        gamePlayConroller.IsGameOver().addListener((source, oldValue, isGameOver) -> {
            if (isGameOver) {
                primaryStage.setScene(getWinnerScene(gamePlayConroller.getWinnerName()));
            }
        });
    }

    private Scene getWinnerScene(String winnerName) {
        FXMLLoader winnerSceneFxmlLoader = getFXMLLoaderByRelativePath(WINNER_SCENE_FILE_PATH);
        Parent winnerSceneRoot = (Parent) winnerSceneFxmlLoader.getRoot();
        WinnerSceneController winnerSceneController = getWinnerSceneController(winnerSceneFxmlLoader);
        winnerSceneController.setWinnerName(winnerName);
        registerNewGameSceneToMainMenuButton(winnerSceneController.isMainMenuButtonPressed());
        return new Scene(winnerSceneRoot, sceneWidth, sceneHeight);
    }

    private WinnerSceneController getWinnerSceneController(FXMLLoader winnerSceneFxmlLoader) {
        WinnerSceneController controller = (WinnerSceneController) winnerSceneFxmlLoader.getController();
        return controller;
    }
    
    private FXMLLoader getFXMLLoaderByRelativePath(String path) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(path);
        fxmlLoader.setLocation(url);
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return fxmlLoader;
    }
}
