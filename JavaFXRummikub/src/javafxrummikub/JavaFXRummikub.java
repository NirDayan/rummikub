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
import javafxrummikub.scenes.gamelist.GamesListSceneController;
import javafxrummikub.scenes.gameplay.GamePlayEventsMgr;
import javafxrummikub.scenes.gameplay.GamePlaySceneController;
import javafxrummikub.scenes.newGame.NewGameSceneController;
import javafxrummikub.scenes.serverlogin.LoginController;
import javafxrummikub.scenes.winner.WinnerSceneController;
import ws.rummikub.RummikubWebService;

public class JavaFXRummikub extends Application {
    private static final String GAME_PLAY_SCENE_FILE_PATH = "/javafxrummikub/scenes/gameplay/GamePlayScene.fxml";
    private static final String NEW_GAME_SCENE_FILE_PATH = "/javafxrummikub/scenes/newGame/newGameScene.fxml";
    private static final String WINNER_SCENE_FILE_PATH = "/javafxrummikub/scenes/winner/winnerScene.fxml";
    private static final String GAMES_LIST_SCENE_FILE_PATH = "/javafxrummikub/scenes/gamelist/GamesListScene.fxml";
    private static final String LOGIN_SCENE_FILE_PATH = "/javafxrummikub/scenes/serverlogin/LoginScene.fxml";
    private int sceneWidth;
    private int sceneHeight;
    private Stage primaryStage;
    private final double screenFactor = 0.8;
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

        primaryStage.setTitle("Welcome to Rumikub!");
        primaryStage.setResizable(false);
        primaryStage.setScene(getServerLoginScene());
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (eventsMgr != null) {
            eventsMgr.stop();
        }
    }
    
    private Scene getServerLoginScene() {
        Scene loginScene = null;
        FXMLLoader fxmlLoader = getFXMLLoaderByRelativePath(LOGIN_SCENE_FILE_PATH);
        Parent loginRoot = (Parent) fxmlLoader.getRoot();
        LoginController loginController = (LoginController) fxmlLoader.getController();
        registerToLoginScene(loginController);
        if (loginRoot != null) {
            loginScene = new Scene(loginRoot, sceneWidth, sceneHeight);
        }

        return loginScene;
    }
    
    private Scene getGamesListScene() {
        Scene gamesListScene = null;
        FXMLLoader fxmlLoader = getFXMLLoaderByRelativePath(GAMES_LIST_SCENE_FILE_PATH);
        Parent gamesListRoot = (Parent) fxmlLoader.getRoot();
        GamesListSceneController gamesListSceneController = getGamesListSceneController(fxmlLoader);
        registerToGameListSceneProperties(gamesListSceneController);
        gamesListSceneController.setServer(rummikubGameWS);
        if (gamesListRoot != null) {
            gamesListScene = new Scene(gamesListRoot, sceneWidth, sceneHeight);
        }

        return gamesListScene;
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
        newGameSceneController.isBackToGamesListScene().addListener((source, oldValue, goToGamesList) -> {
            if (goToGamesList) {
                primaryStage.setScene(getGamesListScene());
            }
        });
    }

    private Scene getGamePlayScene(String gameName, int playerID) {
        FXMLLoader gamePlayFxmlLoader = getFXMLLoaderByRelativePath(GAME_PLAY_SCENE_FILE_PATH);
        Parent gamePlayRoot = (Parent) gamePlayFxmlLoader.getRoot();
        GamePlaySceneController gamePlayConroller = getGamePlaySceneController(gamePlayFxmlLoader);
        gamePlayConroller.initGameParmeters(rummikubGameWS, gameName, playerID);
        eventsMgr = new GamePlayEventsMgr(gamePlayConroller, rummikubGameWS, playerID, gameName);
        eventsMgr.start();
        registerGameListSceneToMainMenuButton(gamePlayConroller.IsMainMenuButtonPressed());
        registerWinnerSceneToIsGameOver(gamePlayConroller);

        return new Scene(gamePlayRoot, sceneWidth, sceneHeight);
    }
    private GamePlayEventsMgr eventsMgr;

    private GamePlaySceneController getGamePlaySceneController(FXMLLoader fxmlLoader) {
        GamePlaySceneController controller = (GamePlaySceneController) fxmlLoader.getController();
        return controller;
    }

    private void registerGameListSceneToMainMenuButton(SimpleBooleanProperty isMainMenu) {
        isMainMenu.addListener((source, oldValue, isExitToMainMenu) -> {
            if (isExitToMainMenu) {
                eventsMgr.stop();
                primaryStage.setScene(getGamesListScene());
            }
        });
    }

    private void registerWinnerSceneToIsGameOver(GamePlaySceneController gamePlayConroller) {
        gamePlayConroller.IsGameOver().addListener((source, oldValue, isGameOver) -> {
            if (isGameOver) {
                eventsMgr.stop();
                primaryStage.setScene(getWinnerScene(gamePlayConroller.getWinnerName()));
            }
        });
    }

    private Scene getWinnerScene(String winnerName) {
        FXMLLoader winnerSceneFxmlLoader = getFXMLLoaderByRelativePath(WINNER_SCENE_FILE_PATH);
        Parent winnerSceneRoot = (Parent) winnerSceneFxmlLoader.getRoot();
        WinnerSceneController winnerSceneController = getWinnerSceneController(winnerSceneFxmlLoader);
        winnerSceneController.setWinnerName(winnerName);
        registerGameListSceneToMainMenuButton(winnerSceneController.isMainMenuButtonPressed());
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

    private GamesListSceneController getGamesListSceneController(FXMLLoader fxmlLoader) {
        return (GamesListSceneController) fxmlLoader.getController();
    }

    private void registerToGameListSceneProperties(GamesListSceneController controller) {
        //Register new game scene to create game button
        controller.getIsCreateGamePressed().addListener((source, oldValue, isCreateGamePressed) -> {
            if (isCreateGamePressed) {
                primaryStage.setScene(getNewGameScene());
            }
        });

        //Register game play scene to player joined game property
        controller.getIsPlayerJoinedGame().addListener((source, oldValue, isJoined) -> {
            if (isJoined) {
                primaryStage.setScene(getGamePlayScene(controller.getJoinedGameName(), controller.getJoinedPlayerID()));
            }
        });
    }

    private void registerToLoginScene(LoginController controller) {
                controller.getServerConnected().addListener((source, oldValue, isConnected) -> {
            if (isConnected) {
                rummikubGameWS = controller.getRummikubGameWS();
                primaryStage.setScene(getGamesListScene());
            }
        });
    }
}
