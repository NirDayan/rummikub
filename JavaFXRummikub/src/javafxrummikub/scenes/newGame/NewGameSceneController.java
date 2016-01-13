package javafxrummikub.scenes.newGame;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.RummikubWebService;

public class NewGameSceneController implements Initializable {

    @FXML
    private Label filePath;
    @FXML
    private TextField filePathInput;
    @FXML
    private Button okButton;
    @FXML
    private RadioButton loadGameFromFileBtn;
    @FXML
    private RadioButton createNewGameBtn;
    @FXML
    private AnchorPane newGameFieldsPane;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private TextField newGameName;
    @FXML
    private TextField humanPlayersNumText;
    @FXML
    private TextField computerPlayersNumText;

    private static int CURRENT_PLAYER_ID = 1;
    private static final int MAX_PLAYERS_NUMBER = 4;
    private static final int MIN_PLAYERS_NUMBER = 2;
    private static final int MIN_HUMAN_PLAYERS_NUMBER = 1;
    private ToggleGroup newGameOptions;
    private SimpleBooleanProperty backToGamesListScene;
    private boolean isPlayersFormInitialized;
    private SimpleBooleanProperty gameLoadedSuccessfully;
    private SimpleBooleanProperty newGameFormValid;
    private SimpleBooleanProperty gameNameValid;
    private SimpleIntegerProperty totalPlayersNum;
    private SimpleIntegerProperty humanPlayersNum;
    private RummikubWebService server;

    //Initializes the controller class.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNewGameOptions();
        initCreateGameButton();
    }

    @FXML
    private void onOkButtonPressed(ActionEvent event) {
        if (newGameOptions.getSelectedToggle() == createNewGameBtn) {
            try {
                server.createGame(
                        newGameName.getText(),
                        humanPlayersNum.get(),
                        totalPlayersNum.get() - humanPlayersNum.get());
            } catch (DuplicateGameName_Exception | InvalidParameters_Exception ex) {
                errorMsgLabel.setText(ex.getMessage());
                return;
            }
        }

        backToGamesListScene.set(true);
    }

    @FXML
    private void onBackButtonPressed(ActionEvent event) {
        backToGamesListScene.set(true);
    }

    @FXML
    private void onNewPlayerNumTyped() {
        clearErrorMsg();
        String humanNum = humanPlayersNumText.getText();
        if (!humanNum.isEmpty()) {
            try {
                humanPlayersNum.set(Integer.parseInt(humanNum));
            } catch (NumberFormatException numberFormatException) {
                humanPlayersNum.set(0);
            }
        }

        String compNumStr = computerPlayersNumText.getText();
        int compNum = 0;
        if (!compNumStr.isEmpty()) {
            try {
                compNum = Integer.parseInt(compNumStr);
            } catch (NumberFormatException numberFormatException) {
                compNum = 0;
            }
        }
        totalPlayersNum.set(compNum + humanPlayersNum.get());
    }

    public SimpleBooleanProperty isBackToGamesListScene() {
        return backToGamesListScene;
    }

    private void initializeNewGameOptions() {
        newGameOptions = new ToggleGroup();
        loadGameFromFileBtn.setToggleGroup(newGameOptions);
        createNewGameBtn.setToggleGroup(newGameOptions);
        gameLoadedSuccessfully = new SimpleBooleanProperty(false);
        newGameFormValid = new SimpleBooleanProperty(false);
        gameNameValid = new SimpleBooleanProperty(false);
        totalPlayersNum = new SimpleIntegerProperty(0);
        humanPlayersNum = new SimpleIntegerProperty(0);
        newGameName.textProperty().addListener((ObservableValue<? extends String> observableValue, String s, String s2) -> {
            boolean isEmpty = newGameName.textProperty().get().isEmpty();
            boolean containsCharsExceptWhitespace = (newGameName.textProperty().get().trim().length() > 0);
            if (!isEmpty && containsCharsExceptWhitespace) {
                gameNameValid.set(true);
            } else {
                gameNameValid.set(false);
            }
        });
        BooleanBinding playersBinding = Bindings.and( Bindings.and(
                totalPlayersNum.greaterThanOrEqualTo(MIN_PLAYERS_NUMBER),
                humanPlayersNum.greaterThanOrEqualTo(MIN_HUMAN_PLAYERS_NUMBER)),
                totalPlayersNum.lessThanOrEqualTo(MAX_PLAYERS_NUMBER));
        
        BooleanBinding newGameFormBindings = Bindings.and(playersBinding, gameNameValid);
        newGameFormValid.bind(newGameFormBindings);
        newGameOptions.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            clearErrorMsg();
            gameLoadedSuccessfully.set(false);
            totalPlayersNum.set(0);
            if (newGameOptions.getSelectedToggle() == loadGameFromFileBtn) {
                handleLoadGameFromFile();
            } else if (newGameOptions.getSelectedToggle() == createNewGameBtn) {
                showNewGameFields();
            }
        });
        backToGamesListScene = new SimpleBooleanProperty(false);
    }

    private void updateSelectedFile(String fullFilePath) {
        filePathInput.setText(fullFilePath);
        filePath.setVisible(true);
        filePathInput.setVisible(true);
    }

    private FileDetails openFileChooser() {
        FileDetails fileDetails = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
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

    private void initNewGameForm() {
        isPlayersFormInitialized = true;
    }

    private void handleLoadGameFromFile() {
        newGameFieldsPane.setVisible(false);
        FileDetails fileDetails = openFileChooser();
        Thread thread = new Thread(() -> loadGameFile(fileDetails));
        thread.setDaemon(true);
        thread.start();
    }

    private void loadGameFile(FileDetails fileDetails) {
        if (fileDetails != null) {
            try {
                GamePersistency.loadGameInServer(fileDetails, server);
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

    private void clearNewGameForm() {
        newGameName.clear();
        humanPlayersNumText.clear();
        computerPlayersNumText.clear();
    }

    private void initCreateGameButton() {
        //start play button is enabled on the below cases:
        // 1. Game loaded successfully from file
        // 2. New game form is valid
        okButton.disableProperty().bind(Bindings.and(newGameFormValid.not(), gameLoadedSuccessfully.not()));
    }

    public void setServer(RummikubWebService rummikubGameWS) {
        server = rummikubGameWS;
    }
}
