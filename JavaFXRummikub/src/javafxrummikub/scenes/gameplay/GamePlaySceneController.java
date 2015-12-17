package javafxrummikub.scenes.gameplay;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafxrummikub.components.TileView;
import javafxrummikub.utils.CustomizablePromptDialog;
import logic.Game;
import logic.Player;
import logic.tile.Sequence;
import logic.tile.Tile;

public class GamePlaySceneController implements Initializable {
    private static final int TILES_LIST_VIEW_WIDTH = 780;
    @FXML
    private Label player1Name;
    @FXML
    private Label player2Name;
    @FXML
    private Label player3Name;
    @FXML
    private Label player4Name;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private Font x1;
    @FXML
    private Button mainMenuButton;
    @FXML
    private Button saveGameButton;
    @FXML
    private Button pullTileButton;
    @FXML
    private Button resignButton;
    @FXML
    private Button finishTurnButton;
    @FXML
    private HBox tilesContainer;
    @FXML
    private VBox boardContainer;

    private Game game;
    private List<Label> playersNames;
    private ObservableList<Tile> currentPlayerTilesData;
    private SimpleBooleanProperty isMainMenuButtonPressed;
    private ListView<Tile> currentPlayerTilesView;
    private SimpleBooleanProperty isCurrPlayerFinished;
    private SimpleBooleanProperty isGameOver;
    private List<ObservableList<Tile>> boardData;
    private List<ListView<Tile>> boardView;
    private boolean isBoardChanged = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
    }

    private void initCurrentPlayerTilesView() {
        currentPlayerTilesData = FXCollections.observableArrayList();
        ListView<Tile> playerTilesView = getTilesListView(currentPlayerTilesData);
        tilesContainer.getChildren().add(playerTilesView);
    }

    private void initializeGamePlay() {
        playersNames = new ArrayList<>(4);
        playersNames.add(player1Name);
        playersNames.add(player2Name);
        playersNames.add(player3Name);
        playersNames.add(player4Name);
        isMainMenuButtonPressed = new SimpleBooleanProperty(false);
        initBoard();
        initCurrentPlayerTilesView();
        isCurrPlayerFinished = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);
        registerFinishTurnProperty();
    }

    public void setGame(Game game) {
        this.game = game;
        initSceneByCurrentGame();
    }

    public void initSceneByCurrentGame() {
        fillPlayersNames();
        updateSceneWithCurrentPlayer();
        updateBoard();
    }

    private void fillPlayersNames() {
        List<Player> gamePlayers = game.getPlayers();
        for (int i = 0; i < gamePlayers.size(); i++) {
            playersNames.get(i).setText(gamePlayers.get(i).getName());
        }
    }

    private void updateSceneWithCurrentPlayer() {
        Player currentPlayer = game.getCurrentPlayer();

        for (Label playerNameLabel : playersNames) {
            if (playerNameLabel.getText().toLowerCase().equals(currentPlayer.getName().toLowerCase())) {
                playerNameLabel.getStyleClass().add("currentPlayer");
            } else {
                playerNameLabel.getStyleClass().remove("currentPlayer");
            }
        }

        updateCurrentPlayerTilesView();
    }

    private void updateCurrentPlayerTilesView() {
        currentPlayerTilesData.clear();
        game.getCurrentPlayer().getTiles().stream().forEach((tile) -> {
            currentPlayerTilesData.add(tile);
        });
    }

    @FXML
    private void onPullTileButton(ActionEvent event) {
        Player player = game.getCurrentPlayer();
        game.pullTileFromDeck(player.getID());
        updateCurrentPlayerTilesView();
        if (isBoardChanged) {
            game.getBoard().restoreFromBackup();
            updateBoard();
        }
        isCurrPlayerFinished.set(true);
    }

    @FXML
    private void onSaveGameButton(ActionEvent event) {
    }

    @FXML
    private void onResignButton(ActionEvent event) {
        game.playerResign(game.getCurrentPlayer().getID());
        
        isCurrPlayerFinished.set(true);
    }

    @FXML
    private void onFinishTurnButton(ActionEvent event) {
        if (isBoardChanged == false) {
            showErrorMsg("No Changes have been made to the board");
            return;
        }
        if (game.getBoard().isValid() == false) {
            Player currentPlayer = game.getCurrentPlayer();
            showErrorMsg("Board is invalid. " + currentPlayer.getName() + " is punished.");
            game.punishPlayer(currentPlayer.getID());
            game.getBoard().restoreFromBackup();
            updateBoard();
        }
        isCurrPlayerFinished.set(true);
    }

    @FXML
    private void onMainMenuButton(ActionEvent event) {
        Stage stage = (Stage) mainMenuButton.getScene().getWindow();
        String answer = CustomizablePromptDialog.show(
                stage, "Are you sure you want to exit? All unsaved data will be lost.", "Exit", "Stay");
        if (answer.equals("Exit")) {
            isMainMenuButtonPressed.set(true);
        }
    }

    public SimpleBooleanProperty IsMainMenuButtonPressed() {
        return isMainMenuButtonPressed;
    }

    private void updateBoard() {
        ObservableList<Tile> seqBinding;
        ListView<Tile> seqView;
        boardData.clear();
        boardView.clear();

        for (Sequence sequence : game.getBoard().getSequences()) {
            seqBinding = FXCollections.observableArrayList(sequence.toList());
            boardData.add(seqBinding);
            seqView = getTilesListView(seqBinding);
            boardView.add(seqView);
        }
        boardContainer.getChildren().addAll(boardView);
    }

    private void initBoard() {
        boardData = new ArrayList<>();
        boardView = new ArrayList<>();
    }

    private ListView<Tile> getTilesListView(ObservableList<Tile> tiles) {
        ListView<Tile> tilesListView = new ListView<>();
        tilesListView.setOrientation(Orientation.HORIZONTAL);
        tilesListView.setPrefWidth(TILES_LIST_VIEW_WIDTH);
        tilesListView.setCellFactory((ListView<Tile> param) -> new TileView());
        tilesListView.setItems(tiles);
        tilesListView.getStyleClass().add("TilesView");

        return tilesListView;
    }

    private void registerFinishTurnProperty() {
        isCurrPlayerFinished.addListener((source, oldValue, isPlayerFinished) -> {
            if (isPlayerFinished == true) {
                if (game.checkIsGameOver())
                    isGameOver.set(true);
                game.moveToNextPlayer();
                while (game.getCurrentPlayer().isResign()){
                    game.moveToNextPlayer();
                }
                updateSceneWithCurrentPlayer();
                isCurrPlayerFinished.set(false);
            }
        });
    }

    public SimpleBooleanProperty IsGameOver() {
        return isGameOver;
    }

    public String getWinnerName() {
        if (game.getWinner() != null) {
            return game.getWinner().getName();
        } else {
            return null;
        }
    }

    private void showErrorMsg(String msg) {
        final int timeToShowErrorInMsec = 3000;
        errorMsgLabel.setText(msg);

        Thread clearMsgThread = new Thread(() -> {
            try {
                Thread.sleep(timeToShowErrorInMsec);
            } catch (InterruptedException ex) {}
            finally { Platform.runLater(this::clearErrorMsg); }
        });
        clearMsgThread.setDaemon(true);
        clearMsgThread.start();
    }
    
    private void clearErrorMsg()
    {
        errorMsgLabel.setText("");
    }
}
