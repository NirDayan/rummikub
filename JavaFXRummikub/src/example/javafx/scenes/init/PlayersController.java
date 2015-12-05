/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.javafx.scenes.init;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

/**
 * FXML Controller class
 *
 * @author I071712
 */
public class PlayersController implements Initializable {
    @FXML
    private TextField playerNameTextField;
    @FXML
    private Button addPlayerButton;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Button continueButton;
    @FXML
    private CheckBox isHumanCheckBox;
    @FXML
    private FlowPane playersPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void addPlayer(ActionEvent event) {
    }

    @FXML
    private void onContinue(ActionEvent event) {
    }
    
}
