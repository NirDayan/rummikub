package javafxrummikub.scenes.winner;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WinnerSceneController implements Initializable {

    @FXML
    private Button backToMainMenu;
    @FXML
    private Label winnerMsg;

    private SimpleBooleanProperty isBackToMainMenu;
    private String winnerName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isBackToMainMenu = new SimpleBooleanProperty(false);
    }

    @FXML
    private void onBackToMainMenu(ActionEvent event) {
        isBackToMainMenu.set(true);
    }

    public SimpleBooleanProperty isMainMenuButtonPressed() {
        return isBackToMainMenu;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
        if (winnerName == null) {
            winnerMsg.setText("Game Over! There is no winner.");
        } else {
            winnerMsg.setText("The Winner is " + winnerName + "!!!");
        }
    }
}
