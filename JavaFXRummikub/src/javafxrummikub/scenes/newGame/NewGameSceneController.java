package javafxrummikub.scenes.newGame;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import logic.Game;
import logic.GameDetails;
import logic.PlayerDetails;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;

public class NewGameSceneController implements Initializable {

    @FXML
    private Label filePath;
    @FXML
    private TextField filePathInput;
    @FXML
    private Button startPlayButton;
    @FXML
    private RadioButton loadGameFromFileBtn;
    @FXML
    private RadioButton createNewGameBtn;
    @FXML
    private AnchorPane newGameFieldsPane;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private TableView<PlayerDetails> playersTable;
    @FXML
    private TableColumn<PlayerDetails, String> playerNameColumn;
    @FXML
    private TableColumn<PlayerDetails, Boolean> isHumanColumn;
    @FXML
    private Button addNewPlayerButton;
    @FXML
    private TextField newPlayerName;
    @FXML
    private CheckBox newPlayerIsHuman;
    @FXML
    private TextField newGameName;

    private ObservableList<PlayerDetails> playersInputData;
    private static int CURRENT_PLAYER_ID = 1;
    private static final int MAX_PLAYERS_NUMBER = 4;
    private static final int MIN_PLAYERS_NUMBER = 2;
    private Game game;
    private ToggleGroup newGameOptions;
    private SimpleBooleanProperty isStartPlayPressed;
    private boolean isPlayersFormInitialized;
    private SimpleBooleanProperty gameLoadedSuccessfully;
    private SimpleBooleanProperty newGameFormValid;
    private SimpleIntegerProperty currNewPlayersNum;

    //Initializes the controller class.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNewGameOptions();
        initStartGameButton();
    }

    @FXML
    private void startPlayPressed(ActionEvent event) {
        if (newGameOptions.getSelectedToggle() == createNewGameBtn) {
            GameDetails gameDetails = new GameDetails(newGameName.getText(), playersInputData, null);
            game = new Game(gameDetails);
        }

        isStartPlayPressed.set(true);
    }

    public SimpleBooleanProperty isStartPlayPressed() {
        return isStartPlayPressed;
    }

    private void initializeNewGameOptions() {
        newGameOptions = new ToggleGroup();
        loadGameFromFileBtn.setToggleGroup(newGameOptions);
        createNewGameBtn.setToggleGroup(newGameOptions);
        gameLoadedSuccessfully = new SimpleBooleanProperty(false);
        newGameFormValid = new SimpleBooleanProperty(false);
        currNewPlayersNum = new SimpleIntegerProperty(0);
        newGameFormValid.bind(Bindings.and(currNewPlayersNum.greaterThanOrEqualTo(MIN_PLAYERS_NUMBER), newGameName.textProperty().isEqualTo("").not()));
        newGameOptions.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            clearErrorMsg();
            gameLoadedSuccessfully.set(false);
            currNewPlayersNum.set(0);
            if (newGameOptions.getSelectedToggle() == loadGameFromFileBtn) {
                handleLoadGameFromFile();
            } else if (newGameOptions.getSelectedToggle() == createNewGameBtn) {
                showNewGameFields();
            }
        });
        isStartPlayPressed = new SimpleBooleanProperty(false);
    }

    private void updateSelectedFile(String fullFilePath) {
        filePathInput.setText(fullFilePath);
        filePath.setVisible(true);
        filePathInput.setVisible(true);
    }

    private FileDetails openFileChooser() {
        FileDetails fileDetails = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open rummikub game file");
        File file = fileChooser.showOpenDialog(loadGameFromFileBtn.getScene().getWindow());

        if (file != null) {
            fileDetails = new FileDetails(file.getParent(), file.getName(), false);
            updateSelectedFile(file.getAbsolutePath());
        }

        return fileDetails;
    }

    private void showNewGameFields() {
        newGameFieldsPane.setVisible(true);
        if (isPlayersFormInitialized) {
            clearNewGameForm();
        } else {
            initNewGameForm();
        }
    }

    @FXML
    private void addNewPlayer(ActionEvent event) {
        boolean isHuman = newPlayerIsHuman.isSelected();
        String playerName = newPlayerName.getText();
        if (!isPlayerNameExist(playerName)) {
            playersInputData.add(new PlayerDetails(CURRENT_PLAYER_ID, playerName, isHuman));
            CURRENT_PLAYER_ID++;
            currNewPlayersNum.setValue(currNewPlayersNum.getValue() + 1);

            if (CURRENT_PLAYER_ID > MAX_PLAYERS_NUMBER) {
                addNewPlayerButton.disableProperty().set(true);
            }
            newPlayerName.clear();
        } else {
            errorMsgLabel.setText("Player name \"" + playerName + "\" is already exist");
        }
    }

    private void initNewGameForm() {
        playersInputData = FXCollections.observableArrayList();
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        isHumanColumn.setCellValueFactory(new PropertyValueFactory<>("isHuman"));
        playersTable.setItems(playersInputData);
        isPlayersFormInitialized = true;
    }

    private void handleLoadGameFromFile() {
        newGameFieldsPane.setVisible(false);
        FileDetails fileDetails = openFileChooser();
        Thread thread = new Thread(()->loadGameFile(fileDetails));
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadGameFile(FileDetails fileDetails) {
        if (fileDetails != null) {
            try {
                game = GamePersistency.load(fileDetails);
                Platform.runLater(this::clearErrorMsg);
                gameLoadedSuccessfully.set(true);
            } catch (Exception err) {
                Platform.runLater(this::openFileFailure);
            }
        } else {
            Platform.runLater(this::openFileFailure);
        }
    }

    private void openFileFailure() {
        newGameOptions.selectToggle(null);
        filePath.setVisible(false);
        filePathInput.setVisible(false);

        errorMsgLabel.setText("Failed to load file, please try again");
    }

    @FXML
    private void clearErrorMsg() {
        errorMsgLabel.setText("");
    }

    public Game getGame() {
        return game;
    }

    private boolean isPlayerNameExist(String playerName) {
        return playersInputData.stream().anyMatch((player) -> (player.getName().toLowerCase().equals(playerName.toLowerCase())));
    }

    private void clearNewGameForm() {
        playersInputData.clear();
        newPlayerName.clear();
        newGameName.clear();
        CURRENT_PLAYER_ID = 1;
        addNewPlayerButton.disableProperty().set(false);
    }

    private void initStartGameButton() {
        //start play button is enabled on the below cases:
        // 1. Game loaded successfully from file
        // 2. New game form is valid
        startPlayButton.disableProperty().bind(Bindings.and(newGameFormValid.not(), gameLoadedSuccessfully.not()));
    }
}
