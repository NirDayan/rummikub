package javafxrummikub;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
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
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.RummikubWebService;
import ws.rummikub.RummikubWebServiceService;

public class JavaFXRummikub extends Application {
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final String WINNER_SCENE_FILE_PATH = "/javafxrummikub/scenes/winner/winnerScene.fxml";

    private int sceneWidth;
    private int sceneHeight;
    private Stage primaryStage;
    private final double screenFactor = 0.8;
    private final String webserviceRoot = "rummikub";
    private final String webserviceName = "RummikubWS";
    private RummikubWebServiceService service;
    private RummikubWebService rummikubGameWS;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        sceneHeight = (int) (screenBounds.getHeight() * screenFactor);
        sceneWidth = (int) (screenBounds.getWidth() * screenFactor);
        
        try {
            //DOTO: chage it.. currently hard-coded
            createWSClient("127.0.0.1", 8080);
        } catch (MalformedURLException ex) {}
        
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
        newGameSceneController.setServer(rummikubGameWS);
        if (newGameRoot != null) {
            newGameScene = new Scene(newGameRoot, sceneWidth, sceneHeight);
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
        
        // In case that a computer player plays first
        if (game.getCurrentPlayer().isHuman() == false) {
            Platform.runLater(gamePlayConroller::playComputerTurn);
        }
        
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
    
    private void createWSClient(String serverAddress, int serverPort) throws MalformedURLException {
        URL url = new URL("http://" + serverAddress + ":" + serverPort + "/" + webserviceRoot + "/" + webserviceName);
        service = new RummikubWebServiceService(url);
        rummikubGameWS = service.getRummikubWebServicePort();
    }
}
