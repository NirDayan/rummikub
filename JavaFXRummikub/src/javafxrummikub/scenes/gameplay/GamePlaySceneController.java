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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
    }

    private void initializeGamePlay() {
        playersNames = new ArrayList<>(4);
        playersNames.add(player1Name);
        playersNames.add(player2Name);
        playersNames.add(player3Name);
        playersNames.add(player4Name);        
        currentPlayerTilesData = FXCollections.observableArrayList();
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
    }

    @FXML
    private void handlePlayerTakeTileFromDeck(ActionEvent event) {
        Player player = game.getCurrentPlayer();        
        game.pullTileFromDeck(player.getID());
        //TODO: we need to think how to present it..
        //TODO: continue..
    }
}