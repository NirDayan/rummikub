package javafxrummikub.scenes.gameplay;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafxrummikub.components.TileView;
import javafxrummikub.utils.CustomizablePromptDialog;
import logic.ComputerAI;
import logic.Game;
import logic.MoveTileData;
import logic.Player;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;
import logic.tile.Color;
import logic.tile.Sequence;
import logic.tile.Tile;

public class GamePlaySceneController implements Initializable {

    private static final int TILES_LIST_VIEW_WIDTH = 1050;
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
    private Label msgLabel;
    @FXML
    private Button mainMenuButton;
    @FXML
    private Button saveGameButton;
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
    private boolean isPlayerPerformAnyChange = false;
    private boolean isBackupNeeded = true;
    private MoveTileData dragTileData;
    private Tile draggedTile;
    private final String ERROR_MSG_TYPE = "error";
    private final String REGULAR_MSG_TYPE = "massage";
    private final int COMPUTER_THINK_TIME_MSEC = 800;
    ScheduledFuture<?> clearMsgTask = null;
    private final ComputerAI ai = new ComputerAI();
    boolean isPlayerPutNewSequence;

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

        if (!isPlayerPerformAnyChange) {
            game.pullTileFromDeck(player.getID());
            updateCurrentPlayerTilesView();
            isCurrPlayerFinished.set(true);
        } else {
            showMessage("Pull tile from deck is not possible since you performed board changes", ERROR_MSG_TYPE);
        }
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
        showMessage(currentPlayer.getName() + " Has Resigned", REGULAR_MSG_TYPE);

        isCurrPlayerFinished.set(true);
    }

    @FXML
    private void onFinishTurnButton(ActionEvent event) {
        Player currentPlayer = game.getCurrentPlayer();
        if (isPlayerPerformAnyChange == false) {
            showMessage("No Changes have been made to the board", ERROR_MSG_TYPE);
            return;
        }
        if (game.getBoard().isValid() == false) {
            punishPlayer();
            isCurrPlayerFinished.set(true);
            return;
        }

        if (game.isPlayerFirstStep(currentPlayer.getID())) {
            if (isFirstStepCompleted()) {
                game.setPlayerCompletedFirstStep(currentPlayer.getID());
                showMessage("Player " + currentPlayer.getName() + " put his first sequence successfully!", REGULAR_MSG_TYPE);
            } else {
                punishPlayer();
            }
        }
        
        isCurrPlayerFinished.set(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
    }

    private void initializeGamePlay() {
        isMainMenuButtonPressed = new SimpleBooleanProperty(false);
        isCurrPlayerFinished = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);

        groupPlayerNamesToList();
        initBoard();
        initCurrentPlayerTilesView();
        registerFinishTurnProperty();
        clearMsgLabel();
    }

    private void groupPlayerNamesToList() {
        playersNames = new ArrayList<>(4);
        playersNames.add(player1Name);
        playersNames.add(player2Name);
        playersNames.add(player3Name);
        playersNames.add(player4Name);
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
                playerFinishedTurn();
            }
        });
    }

    private void playerFinishedTurn() {
        if (game.checkIsGameOver()) {
            isGameOver.set(true);
            return;
        }
        getNextPlayingPlayer();
        if (game.getCurrentPlayer().isHuman() == false) {
            Platform.runLater(this::playComputerTurn);
        }
        updateSceneWithCurrentPlayer();
        isCurrPlayerFinished.set(false);
    }

    public void playComputerTurn() {
        Player compPlayer = game.getCurrentPlayer();
        List<Tile> sequence = ai.getRelevantTiles(compPlayer.getTiles());

        try {
            // Simulate Computer "Thinking..."
            Thread.sleep(COMPUTER_THINK_TIME_MSEC);
        } catch (InterruptedException e) {
        }

        if (sequence != null) {
            game.getBoard().addSequence(new Sequence(sequence));
            compPlayer.removeTiles(sequence);

            showMessage(compPlayer.getName() + " Added a Sequece to the borad.", REGULAR_MSG_TYPE);
            playerActionOnBoardDone();

            sequence = ai.getRelevantTiles(game.getCurrentPlayer().getTiles());
            if (sequence != null) {
                Platform.runLater(this::playComputerTurn);
                return;
            }
        }

        if (isPlayerPerformAnyChange == false) {
            showMessage(compPlayer.getName() + " Pulled a tile from the deck.", REGULAR_MSG_TYPE);
            game.pullTileFromDeck(compPlayer.getID());
            updateCurrentPlayerTilesView();
        }

        isCurrPlayerFinished.set(true);
    }

    private void getNextPlayingPlayer() {
        game.moveToNextPlayer();
        while (game.getCurrentPlayer().isResign()) {
            game.moveToNextPlayer();
        }
        isPlayerPerformAnyChange = false;
        isBackupNeeded = true;
        isPlayerPutNewSequence = false;
        updateSceneWithCurrentPlayer();
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
        ArrayList<Tile> playerTiles = game.getCurrentPlayer().getTiles();
        //Don't change to forEach, it won't work since the "equals" function of tile which ignores duplicate tiles with the same coloe and value
        for (int i = 0; i < playerTiles.size(); i++) {
            currentPlayerTilesData.add(playerTiles.get(i));
        }
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

    private void showMessage(String msg, String msgType) {
        if (clearMsgTask != null) {
            clearMsgTask.cancel(true);
        }
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
        clearMsgTask = threadPool.schedule(() -> Platform.runLater(this::clearMsgLabel),
                3, TimeUnit.SECONDS);
        threadPool.shutdown();
        msgLabel.getStyleClass().clear();
        msgLabel.getStyleClass().add(msgType);
        msgLabel.setText(msg);
    }

    private void clearMsgLabel() {
        msgLabel.setText("");
    }

    private void onSaveToLastFile(ActionEvent event) {
        FileDetails fileDetails = game.getSavedFileDetails();
        saveToFile(fileDetails);
    }

    private void onSaveAs(ActionEvent event) {
        FileDetails fileDetails = openFileChooserToSave();
        saveToFile(fileDetails);
    }

    private void saveToFile(FileDetails fileDetails) {
        Thread thread = new Thread(() -> {
            try {
                GamePersistency.save(fileDetails, game);
                game.setSavedFileDetails(fileDetails);
                Platform.runLater(this::saveFileSeccess);
            } catch (Exception ex) {
                Platform.runLater(this::saveFileFailure);
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    private void saveFileFailure() {
        showMessage("Game saving was failed.", ERROR_MSG_TYPE);
    }

    private void saveFileSeccess() {
        showMessage("Game Was saved.", REGULAR_MSG_TYPE);
    }

    private FileDetails openFileChooserToSave() {
        FileDetails fileDetails = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        fileChooser.setTitle("Select File to save the game");
        File file = fileChooser.showSaveDialog(mainMenuButton.getScene().getWindow());

        if (file != null) {
            fileDetails = new FileDetails(file.getParent(), file.getName(), true);
        }

        return fileDetails;
    }

    private void manageDragAndDrop(ListView<Tile> listView) {
        listView.setOnDragDetected((MouseEvent event) -> {
            performDragDetected(listView);
        });

        listView.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });

        listView.setOnDragDone(event -> {
            updateBoard();
        });

        listView.setOnDragDropped(event -> {
            performDropDetected(listView, event);
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

        //Find the position in the sequence
        for (int i = 0; i < listView.getItems().size(); i++) {
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
        return INDEX_NOT_FOUND;
    }

    private void addPlusTilesToBoard() {
        if (isPlayerFirstStepAndNoNewSequence()) {
            addNewSequencePlaceholder();
        } else if (isPlayerFirstStepWithNewSequence()) {
            addPlusTilesToLastSequence();
        } else { //Add plus tile to existing sequences if this is not the first step of the player
            addPlusTilesToAllSequences();
            //If the source tile is dragged from player's tiles we woule like to present a new sequence placeholder
            if (dragTileData.getSourceSequenceIndex() == INDEX_NOT_FOUND) {
                addNewSequencePlaceholder();
            }
        }
    }

    /**
     * This function checks if the selected tile could be dragged out of its
     * position [for example, tile from the middle of a board sequence could not
     * be dragged out of the sequence]
     *
     * @return boolean
     */
    private boolean checkIsDragTileValid(ListView<Tile> listView) {
        if (draggedTile == null || dragTileData == null) {
            return false;
        }

        if (isBoardSequence(listView)) {
            int tilePoition = dragTileData.getSourceSequencePosition();
            //Check if this is the first or the last tile in the sequence
            if (!(tilePoition == 0 || (tilePoition == listView.getItems().size() - 1))) {
                return false;
            }

            //if this is the first step and player didnt put a new sequence yet, drag from board is not valid
            if (isPlayerFirstStepAndNoNewSequence()) {
                return false;
            }

            //if this is the first step and the player already put a new sequence,
            //make sure the drag is performed from the last sequence!
            if (isPlayerFirstStepWithNewSequence()) {
                boolean isLastSequence = (dragTileData.getSourceSequenceIndex() == (boardData.size() - 1));

                return isLastSequence;
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
            playerActionOnBoardDone();
        } else {
            showMessage("Invalid add tile action", ERROR_MSG_TYPE);
        }
    }

    private void performMoveTileInBoard(MoveTileData moveTileData) {
        if (game.moveTile(moveTileData)) {
            playerActionOnBoardDone();
        } else {
            showMessage("Invalid move tile action", ERROR_MSG_TYPE);
        }
    }

    private void playerActionOnBoardDone() {
        isPlayerPerformAnyChange = true;
        updateBoard();
        updateCurrentPlayerTilesView();
    }

    private void backupTurn() {
        if (isBackupNeeded) {
            game.storeBackup();
            isBackupNeeded = false;
        }
    }

    private void performDragDetected(ListView<Tile> listView) {
        if (listView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        if (isBackupNeeded) {
            backupTurn();
        }

        dragTileData = getDraggedTileData(listView);
        if (checkIsDragTileValid(listView)) {
            addPlusTilesToBoard();
            Dragboard dragBoard = listView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("");
            dragBoard.setContent(content);
        }
    }

    private void performDropDetected(ListView<Tile> listView, DragEvent event) {
        if (draggedTile != null && dragTileData != null) {
            int targetSequenceBoardIndex = getListTilesViewBoardIndex(listView);
            if (targetSequenceBoardIndex != INDEX_NOT_FOUND) {
                dragTileData.setTargetSequenceIndex(targetSequenceBoardIndex);
                dragTileData.setTargetSequencePosition(getTargetBoardSequencePosition(listView));
                if (dragTileData.getSourceSequenceIndex() == INDEX_NOT_FOUND) {//tile dragged from player's tiles
                    handleDropTileFromPlayerTiles();
                } else {
                    performMoveTileInBoard(dragTileData);
                }
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
        } else {
            event.setDropCompleted(false);
        }

        dragTileData = null;
        draggedTile = null;
    }

    private void addNewSequencePlaceholder() {
        int playerID = game.getCurrentPlayer().getID();

        ObservableList<Tile> seqBinding = FXCollections.observableArrayList();
        seqBinding.add(new Tile(Color.Red, Tile.PLUS_TILE));
        ListView<Tile> seqView = getTilesListView(seqBinding);
        seqView.getStyleClass().add("sequenceView");
        boardData.add(seqView);
    }

    private void performCreateNewSequence(MoveTileData dragTileData) {
        int tilePosition = dragTileData.getSourceSequencePosition();
        game.createSequenceByPlayerTile(game.getCurrentPlayer().getID(), tilePosition);
        playerActionOnBoardDone();
    }

    private boolean isDropIntoLastSequence() {
        //The player drop tile into a new sequence if the position is the last sequence        
        return dragTileData.getTargetSequenceIndex() == boardData.size() - 1;
    }

    private boolean isPlayerFirstStepAndNoNewSequence() {
        int playerID = game.getCurrentPlayer().getID();
        boolean isFirstStep = game.isPlayerFirstStep(playerID);

        return (isFirstStep && !isPlayerPutNewSequence);
    }

    private boolean isPlayerFirstStepWithNewSequence() {
        int playerID = game.getCurrentPlayer().getID();
        boolean isFirstStep = game.isPlayerFirstStep(playerID);

        return (isFirstStep && isPlayerPutNewSequence);
    }

    private void addPlusTilesToAllSequences() {
        ObservableList<Tile> sequence;
        for (ListView<Tile> boardSequences : boardData) {
            sequence = boardSequences.getItems();
            for (int j = 0; j < sequence.size() + 1; j = j + 2) {
                sequence.add(j, new Tile(Color.Red, Tile.PLUS_TILE));
            }
        }
    }

    private void addPlusTilesToLastSequence() {
        ListView<Tile> lastSequence = boardData.get(boardData.size() - 1);
        if (lastSequence != null) {
            for (int i = 0; i < lastSequence.getItems().size() + 1; i = i + 2) {
                lastSequence.getItems().add(i, new Tile(Color.Red, Tile.PLUS_TILE));
            }
        }
    }

    private void handleDropTileFromPlayerTiles() {
        if (isPlayerFirstStepAndNoNewSequence()) {
            if (isDropIntoLastSequence()) {
                performCreateNewSequence(dragTileData);
                isPlayerPutNewSequence = true;
            } else {
                showMessage("You must drop the tile into the last new sequence", ERROR_MSG_TYPE);
            }
        } else if (isPlayerFirstStepWithNewSequence()) {
            if (isDropIntoLastSequence()) {
                performAddTileToBoard(dragTileData);
            } else {
                showMessage("You must drop the tile into the last new sequence", ERROR_MSG_TYPE);
            }
        } else {
            if (isDropIntoLastSequence()) {
                performCreateNewSequence(dragTileData);
            } else {
                performAddTileToBoard(dragTileData);
            }
        }
    }

    private boolean isFirstStepCompleted() {
        int playerID = game.getCurrentPlayer().getID();
        ListView<Tile> lastSequence;

        if (boardData.size() > 0) {
            lastSequence = boardData.get(boardData.size() - 1);
            return game.checkSequenceValidity(playerID, lastSequence.getItems());
        }

        return false;
    }

    private void punishPlayer() {
        Player currentPlayer = game.getCurrentPlayer();

        showMessage("Board is invalid. " + currentPlayer.getName() + " is punished.", ERROR_MSG_TYPE);
        game.restoreFromBackup();
        updateBoard();
        //Punish player MUST be after restore from backup, otherwise the restore operation will remove additional tiles
        game.punishPlayer(currentPlayer.getID());
    }
}
