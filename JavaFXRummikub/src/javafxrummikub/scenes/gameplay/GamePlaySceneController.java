package javafxrummikub.scenes.gameplay;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafxrummikub.components.TileView;
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
    private ObservableList<TileView> currentPlayerTilesData;
    private ListView<TileView> currentPlayerTilesView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
    }

    private void initCurrentPlayerTilesView() {
        currentPlayerTilesView = new ListView<>();
        currentPlayerTilesView.setOrientation(Orientation.HORIZONTAL);
        currentPlayerTilesView.setPrefWidth(780);
        currentPlayerTilesView.setCellFactory((ListView<TileView> param) -> new TileViewCell());
        currentPlayerTilesView.setItems(currentPlayerTilesData);
        currentPlayerTilesView.getStyleClass().add("currentPlayerTilesView");
        tilesContainer.getChildren().add(currentPlayerTilesView);
    }

    public class TileViewCell extends ListCell<TileView> {
        @Override
        public void updateItem(TileView tile, boolean empty) {
            super.updateItem(tile, empty);
            if (tile != null) {
                setTextAlignment(TextAlignment.CENTER);
                setGraphic(tile.createImage());
                setContentDisplay(ContentDisplay.TOP);
                if (tile.isJoker()) {
                    setText("J");
                } else {
                    setText(String.format("%d", tile.getValue()));
//            Paint paint = tilegetColorByTileData();
//            setTextFill(paint);
                }
                getStyleClass().add("TileCellView");
            }
        }
    }

    private void initializeGamePlay() {
        currentPlayerTilesData = FXCollections.observableArrayList();
        playersNames = new ArrayList<>(4);
        playersNames.add(player1Name);
        playersNames.add(player2Name);
        playersNames.add(player3Name);
        playersNames.add(player4Name);
        initCurrentPlayerTilesView();
    }

    public void setGame(Game game) {
        this.game = game;
        initSceneByCurrentGame();
    }

    public void initSceneByCurrentGame() {
        fillPlayersNames();
        updateSceneWithCurrentPlayer();
        TileView tileView = new TileView(game.getTilesDeck().pullTile());
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
            currentPlayerTilesData.add(new TileView(tile));
        });
    }

    @FXML
    private void handlePlayerTakeTileFromDeck(ActionEvent event) {
        Player player = game.getCurrentPlayer();
        game.pullTileFromDeck(player.getID());
        //TODO: we need to think how to present it..
        //TODO: continue..
    }
}
