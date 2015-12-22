package javafxrummikub.scenes.gameplay;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafxrummikub.components.TileView;
import javafxrummikub.utils.CustomizablePromptDialog;
import logic.Game;
import logic.MoveTileData;
import logic.Player;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;
import logic.tile.Color;
import logic.tile.Sequence;
import logic.tile.Tile;

public class GamePlaySceneController implements Initializable {

    private static final int TILES_LIST_VIEW_WIDTH = 780;
    private static final int INDEX_NOT_FOUND = -1;
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
    private ObservableList<ListView<Tile>> boardData;
    private ListView<ListView<Tile>> boardView;
    private boolean isBoardChanged = false;
    private boolean isPlayerPerformAnyChange = false;
    private MoveTileData dragTileData;
    private Tile draggedTile;

    @FXML
    private void onMainMenuButton(ActionEvent event) {
        Stage stage = (Stage) mainMenuButton.getScene().getWindow();
        String answer = CustomizablePromptDialog.show(
                stage, "Are you sure you want to exit? All unsaved data will be lost.", "Exit", "Stay");
        if (answer.equals("Exit")) {
            isMainMenuButtonPressed.set(true);
        }
    }

    @FXML
    private void onSaveGameButton(ActionEvent event) {
        ContextMenu saveMenu = new ContextMenu();
        MenuItem saveOption = new MenuItem("Save");
        MenuItem saveAsOption = new MenuItem("Save As...");
        saveOption.setOnAction(this::onSaveToLastFile);
        saveAsOption.setOnAction(this::onSaveAs);
        if (game.getSavedFileDetails() == null) {
            saveOption.setDisable(true);
        } else {
            saveOption.setDisable(false);
        }

        saveMenu.getItems().addAll(saveOption, saveAsOption);
        saveMenu.show(saveGameButton, Side.LEFT, 10, 10);
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
    private void onResignButton(ActionEvent event) {
        Player currentPlayer = game.getCurrentPlayer();
        game.playerResign(currentPlayer.getID());

        for (Label playerNameLabel : playersNames) {
            if (playerNameLabel.getText().toLowerCase().equals(currentPlayer.getName().toLowerCase())) {
                playerNameLabel.setText("");
            }
        }

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
        isMainMenuButtonPressed = new SimpleBooleanProperty(false);
        initBoard();
        initCurrentPlayerTilesView();
        isCurrPlayerFinished = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);
        registerFinishTurnProperty();
    }

    private void initBoard() {
        boardData = FXCollections.observableArrayList();
        boardView = new ListView<>();
        boardView.setItems(boardData);
    }

    private void initCurrentPlayerTilesView() {
        currentPlayerTilesData = FXCollections.observableArrayList();
        currentPlayerTilesView = getTilesListView(currentPlayerTilesData);
        currentPlayerTilesView.setPrefWidth(TILES_LIST_VIEW_WIDTH);
        tilesContainer.getChildren().add(currentPlayerTilesView);
    }

    private void registerFinishTurnProperty() {
        isCurrPlayerFinished.addListener((source, oldValue, isPlayerFinished) -> {
            if (isPlayerFinished == true) {
                if (game.checkIsGameOver()) {
                    isGameOver.set(true);
                }
                game.moveToNextPlayer();
                while (game.getCurrentPlayer().isResign()) {
                    game.moveToNextPlayer();
                }
                updateSceneWithCurrentPlayer();
                isPlayerPerformAnyChange = false;
                isCurrPlayerFinished.set(false);
            }
        });
    }

    public void setGame(Game game) {
        this.game = game;
        initSceneByCurrentGame();
    }

    private void initSceneByCurrentGame() {
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

    private void updateBoard() {
        ObservableList<Tile> seqBinding;
        ListView<Tile> seqView;
        boardData.clear();

        for (Sequence sequence : game.getBoard().getSequences()) {
            seqBinding = FXCollections.observableArrayList(sequence.toList());
            seqView = getTilesListView(seqBinding);
            seqView.getStyleClass().add("sequenceView");
            boardData.add(seqView);
        }
        boardView.getStyleClass().add("boardView");
        if (!boardContainer.getChildren().contains(boardView)) {
            boardContainer.getChildren().add(boardView);
        }        
    }

    private void updateCurrentPlayerTilesView() {
        currentPlayerTilesData.clear();
        game.getCurrentPlayer().getTiles().stream().forEach((tile) -> {
            currentPlayerTilesData.add(tile);
        });
    }

    private ListView<Tile> getTilesListView(ObservableList<Tile> tiles) {
        ListView<Tile> tilesListView = new ListView<>();
        tilesListView.setOrientation(Orientation.HORIZONTAL);
        tilesListView.setCellFactory((ListView<Tile> param) -> new TileView());
        tilesListView.setItems(tiles);
        tilesListView.getStyleClass().add("TilesView");
        manageDragAndDrop(tilesListView);
        return tilesListView;
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

    public SimpleBooleanProperty IsMainMenuButtonPressed() {
        return isMainMenuButtonPressed;
    }

    private void showErrorMsg(String msg) {
        Thread clearMsgThread = new Thread(() -> {
            final int timeToShowErrorInMsec = 3000;
            try {
                Thread.sleep(timeToShowErrorInMsec);
            } catch (InterruptedException ex) {
            } finally {
                Platform.runLater(this::clearErrorMsg);
            }
        });
        clearMsgThread.setDaemon(true);
        clearMsgThread.start();

        errorMsgLabel.setText(msg);
    }

    private void clearErrorMsg() {
        errorMsgLabel.setText("");
    }

    private void onSaveToLastFile(ActionEvent event) {
        try {
            GamePersistency.save(game.getSavedFileDetails(), game);
        } catch (Exception ex) {
            showErrorMsg("Game saving was failed.");
        }
    }

    private void onSaveAs(ActionEvent event) {
        FileDetails fileDetails = openFileChooserToSave();
        try {
            GamePersistency.save(fileDetails, game);
        } catch (Exception ex) {
            showErrorMsg("Game saving was failed.");
        }
    }

    private FileDetails openFileChooserToSave() {
        FileDetails fileDetails = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to save the game");
        File file = fileChooser.showSaveDialog(mainMenuButton.getScene().getWindow());

        if (file != null) {
            fileDetails = new FileDetails(file.getParent(), file.getName(), true);
        }

        return fileDetails;
    }

    private void manageDragAndDrop(ListView<Tile> listView) {
        listView.setOnDragDetected((MouseEvent event) -> {
            if (listView.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            
            dragTileData = getDraggedTileData(listView);
            if (checkIsDragTileValid(listView)) {
                Dragboard dragBoard = listView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("sdf");//TODO: change to tile data
                dragBoard.setContent(content);
                listView.startDragAndDrop(TransferMode.MOVE);
                addPlusTilesToBoard();
            }            
        });

        listView.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });

        listView.setOnDragDone(event -> {
//            listView.getItems().remove(cell.getItem())
        });

        listView.setOnDragDropped(event -> {
            if (draggedTile != null && dragTileData != null) {
                dragTileData.setTargetSequenceIndex(getListTilesViewBoardIndex(listView));
                dragTileData.setTargetSequencePosition(getTargetBoardSequencePosition(listView));
                if (dragTileData.getSourceSequenceIndex() == INDEX_NOT_FOUND) {
                    performAddTileToBoard(dragTileData);
                } else {
                    //TODO: performMoveTileInBoard(dragTileData);
                }
                event.setDropCompleted(true);
                removePlusTilesFromBoard();
                dragTileData = null;
                draggedTile = null;                
            } else {
                event.setDropCompleted(false);
            }
        });
    }

    private MoveTileData getDraggedTileData(ListView<Tile> listView) {
        Tile selectedTile = listView.getSelectionModel().getSelectedItem();
        MoveTileData moveTileData = new MoveTileData();
        if (isBoardSequence(listView)) {
            //Find the sequence index
            moveTileData.setSourceSequenceIndex(getListTilesViewBoardIndex(listView));
        } else {
            moveTileData.setSourceSequenceIndex(INDEX_NOT_FOUND);
        }
        
        //Find the posdfsition in the sequence
        for (int i = 0; i <listView.getItems().size(); i++) {
            if (listView.getItems().get(i) == selectedTile) {
                moveTileData.setSourceSequencePosition(i);
            }
        }
        
        draggedTile = listView.getSelectionModel().getSelectedItem();
        moveTileData.setPlayerID(game.getCurrentPlayer().getID());
        return moveTileData;
    }

    private int getListTilesViewBoardIndex(ListView<Tile> listView) {
        for (int i = 0; i < boardData.size(); i++) {
            if (boardData.get(i).equals(listView)) {
                return i;
            }
        }
        return 0;
    }

    private void addPlusTilesToBoard() {
        ObservableList<Tile> sequence;
        for (ListView<Tile> boardData1 : boardData) {
            sequence = boardData1.getItems();
            for (int j = 0; j < sequence.size() + 1; j = j + 2) {
                sequence.add(j, new Tile(Color.Red, Tile.PLUS_TILE));
            }
        }
    }
    
    private void removePlusTilesFromBoard() {
       ObservableList<Tile> sequence;
       List<Tile> tilesForRemove = new ArrayList<>();
        for (ListView<Tile> boardData1 : boardData) {
            sequence = boardData1.getItems();
            for (int j = 0; j < sequence.size() + 1; j = j + 2) {
                if (sequence.get(j).isPlusTile()) {
                    tilesForRemove.add(sequence.get(j));
                }
            }
            sequence.removeAll(tilesForRemove);
        } 
    }

    /**
     * This function checks if the selected tile could be dragged out of its position
     * [for example, tile from the middle of a board sequence could not be dragged out of the sequence]
     * @return boolean
     */
    private boolean checkIsDragTileValid(ListView<Tile> listView) {
        if (draggedTile == null || dragTileData == null) {
            return false;
        }
        
        if (isBoardSequence(listView)) {
            int tilePoition = dragTileData.getSourceSequencePosition();
            //Check if this is the first or the list tile in the sequence
            if (!(tilePoition == 0 || tilePoition == listView.getItems().size() - 1)) {
                return false;                                
            }
        }
        
        return true;
    }

    private boolean isBoardSequence(ListView<Tile> listView) {
        for (ListView<Tile> sequenceView : boardView.getItems()) {
            if (sequenceView.equals(listView)) {
                return true;
            }
        }
        
        return false;
    }

    private int getTargetBoardSequencePosition(ListView<Tile> listView) {
        Tile currentTile;
        int plusTilesCounter = 0;
        for (int i = 0; i < listView.getItems().size(); i++) {
            currentTile = listView.getItems().get(i);
            if (currentTile.isPlusTile()) {
                if (currentTile.isHovered()) {
                    return i - plusTilesCounter;
                }
                plusTilesCounter++;
            }
        }
        
        return INDEX_NOT_FOUND;
    }

    private void performAddTileToBoard(MoveTileData addTileData) {
        boolean isValid = game.addTile(game.getCurrentPlayer().getID(), addTileData);
        
        if (isValid) {
            isPlayerPerformAnyChange = true;
            updateBoard();
            updateCurrentPlayerTilesView();
        } else {
            showErrorMsg("Invalid add tile action");
        }
    }
}
