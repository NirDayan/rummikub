package javafxrummikub.scenes.gameplay;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafxrummikub.components.TileView;
import javafxrummikub.utils.CustomizablePromptDialog;
import logic.Game;
import logic.Player;
import logic.tile.Tile;

public class GamePlaySceneController implements Initializable {

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

    private Game game;
    private List<Label> playersNames;
    private ObservableList<Tile> currentPlayerTilesData;
    private SimpleBooleanProperty isMainMenuButtonPressed;
    private ListView<Tile> currentPlayerTilesView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
    }

    private void initCurrentPlayerTilesView() {
        currentPlayerTilesView = new ListView<>();
        currentPlayerTilesView.setOrientation(Orientation.HORIZONTAL);
        currentPlayerTilesView.setPrefWidth(780);
        currentPlayerTilesView.setCellFactory((ListView<Tile> param) -> new TileView());
        currentPlayerTilesView.setItems(currentPlayerTilesData);
        currentPlayerTilesView.getStyleClass().add("currentPlayerTilesView");
        tilesContainer.getChildren().add(currentPlayerTilesView);
    }

    private void initializeGamePlay() {
        currentPlayerTilesData = FXCollections.observableArrayList();
        playersNames = new ArrayList<>(4);
        playersNames.add(player1Name);
        playersNames.add(player2Name);
        playersNames.add(player3Name);
        playersNames.add(player4Name);
        initCurrentPlayerTilesView();
        isMainMenuButtonPressed = new SimpleBooleanProperty(false);
    }

    public void setGame(Game game) {
        this.game = game;
        initSceneByCurrentGame();
    }

    public void initSceneByCurrentGame() {
        fillPlayersNames();
        updateSceneWithCurrentPlayer();
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

        game.getCurrentPlayer().getTiles().stream().forEach((tile) -> {
            currentPlayerTilesData.add(tile);
        });
    }

    @FXML
    private void handlePlayerTakeTileFromDeck(ActionEvent event) {
        Player player = game.getCurrentPlayer();
        game.pullTileFromDeck(player.getID());
        //TODO: we need to think how to present it..
        //TODO: continue..
    }

    @FXML
    private void mainMenuButtonPressed(ActionEvent event) {
        Stage stage = (Stage) mainMenuButton.getScene().getWindow();
        String answer = CustomizablePromptDialog.show(
                stage, "Are you sure you want to exit? All unsaved data will be lost.", "Exit", "Stay");
        if (answer.equals("Exit")) {
               isMainMenuButtonPressed.set(true);
        }
    }
    
    public SimpleBooleanProperty IsMainMenuButtonPressed(){
        return isMainMenuButtonPressed;
    }
}
