package javafxrummikub.scenes.gamelist;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ws.rummikub.GameDetails;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.RummikubWebService;

public class GamesListSceneController implements Initializable {

    @FXML
    private Button joinGameButton;
    @FXML
    private Button createGameButton;
    @FXML
    private Button exitGameButton;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private TableView<GameDetails> gamesTable;
    @FXML
    private TableColumn<GameDetails, String> gameNameColumn;
    @FXML
    private TableColumn<GameDetails, Integer> humanPlayersNumColumn;
    @FXML
    private TableColumn<GameDetails, Integer> compPlayersNumColumn;
    @FXML
    private TableColumn<GameDetails, Integer> joinedHumanNumColumn;
    @FXML
    private TextField playerNameTextBox;

    private SimpleBooleanProperty isGameSelectedFromList = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCreateGameButtonPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isPlayerJoinedGame = new SimpleBooleanProperty(false);
    private ObservableList<GameDetails> gamesList;
    private RummikubWebService server;
    private String joinedGameName;
    private int joinedPlayerID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        joinGameButton.disableProperty().bind(Bindings.not(isGameSelectedFromList));
        playerNameTextBox.disableProperty().bind(Bindings.not(isGameSelectedFromList));
        initGamesTable();
    }

    private void initGamesTable() {
        gamesList = FXCollections.observableArrayList();
        gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        gameNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        humanPlayersNumColumn.setCellValueFactory(new PropertyValueFactory<>("humanPlayers"));
        compPlayersNumColumn.setCellValueFactory(new PropertyValueFactory<>("computerizedPlayers"));
        joinedHumanNumColumn.setCellValueFactory(new PropertyValueFactory<>("joinedHumanPlayers"));
        gamesTable.setItems(gamesList);
        gamesTable.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener.Change<? extends GameDetails> c) -> {
                    isGameSelectedFromList.set(true);
                });

        Platform.runLater(this::fillGamesTable);
    }

    private void fillGamesTable() {
        List<String> gameNames = server.getWaitingGames();
        gameNames.forEach((gameName) -> {
            try {
                gamesList.add(server.getGameDetails(gameName));
            } catch (GameDoesNotExists_Exception ex) {
                System.err.println("Game " + gameName + "Does not exist in the server");
            }
        });
    }

    private void addGameToGamesTable(String name) {
    }

    @FXML
    private void onJoinGameButtonPressed(ActionEvent event) {
        String gameName = gamesTable.getSelectionModel().getSelectedItem().getName();
        String playerName = playerNameTextBox.getText();
        try {
            joinedPlayerID = server.joinGame(gameName, playerName);
            joinedGameName = gameName;
            isPlayerJoinedGame.set(true);
        } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    @FXML
    private void onCreateGameButtonPressed(ActionEvent event) {
        isCreateGameButtonPressed.set(true);
    }

    @FXML
    private void onExitGameButtonPressed(ActionEvent event) {
        Platform.exit();
    }

    public SimpleBooleanProperty getIsCreateGamePressed() {
        return isCreateGameButtonPressed;
    }

    public SimpleBooleanProperty getIsPlayerJoinedGame() {
        return isPlayerJoinedGame;
    }

    public void setServer(RummikubWebService rummikubGameWS) {
        server = rummikubGameWS;
    }

    public String getJoinedGameName() {
        return joinedGameName;
    }

    public int getJoinedPlayerID() {
        return joinedPlayerID;
    }
    
     private void showErrorMessage(String msg)
     {
         errorMsgLabel.setText(msg);
     }
}