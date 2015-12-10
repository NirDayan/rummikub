package javafxrummikub.scenes.newGame;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
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
import logic.PlayerDetails;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;

public class NewGameSceneController implements Initializable {
    private static int CURRENT_PLAYER_ID = 1;
    private static final int MAX_PLAYERS_NUMBER = 4;
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
    private ObservableList<PlayerDetails> playersInputData;
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
    private Game game;
    private ToggleGroup newGameOptions;
    private SimpleBooleanProperty isStartPlayPressed;


    //Initializes the controller class.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNewGameOptions();
    }    

    @FXML
    private void startPlayPressed(ActionEvent event) {
        isStartPlayPressed.set(true);
    }
    
        public SimpleBooleanProperty getFinishedInit() {
        return isStartPlayPressed;
    }

    private void initializeNewGameOptions() {
        newGameOptions = new ToggleGroup();
        loadGameFromFileBtn.setToggleGroup(newGameOptions);
        createNewGameBtn.setToggleGroup(newGameOptions);      
        newGameOptions.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (newGameOptions.getSelectedToggle() == loadGameFromFileBtn) {
                handleLoadGameFromFile();
            }
            else if (newGameOptions.getSelectedToggle() == createNewGameBtn) {
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
        clearErrorMsg();
        newGameFieldsPane.setVisible(true);
        playersInputData = FXCollections.observableArrayList();
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        isHumanColumn.setCellValueFactory(new PropertyValueFactory<>("isHuman"));
        playersTable.setItems(playersInputData);
    }

    @FXML
    private void addNewPlayer(ActionEvent event) {
        boolean isHuman = newPlayerIsHuman.isSelected();
        String playerName = newPlayerName.getText();
        playersInputData.add(new PlayerDetails(CURRENT_PLAYER_ID, playerName, isHuman));
        CURRENT_PLAYER_ID++;
        if (CURRENT_PLAYER_ID > MAX_PLAYERS_NUMBER) {
            addNewPlayerButton.setDisable(true);
        }
        newPlayerName.clear();
    }
   
    private void handleLoadGameFromFile() {
        newGameFieldsPane.setVisible(false);
        FileDetails fileDetails = openFileChooser();
        if (fileDetails != null) {
            try {
                game = GamePersistency.load(fileDetails);
                clearErrorMsg();
            }
            catch (Exception err) {
                openFileFailure();
            }                    
        }
        else {
            openFileFailure();            
        }
    }
    
    private void openFileFailure() {
        newGameOptions.selectToggle(null);
        filePath.setVisible(false);
        filePathInput.setVisible(false);
        
        errorMsgLabel.setText("Failed to load file, please try again");
    }
    
    private void clearErrorMsg() {
        errorMsgLabel.setText("");
    }
}
