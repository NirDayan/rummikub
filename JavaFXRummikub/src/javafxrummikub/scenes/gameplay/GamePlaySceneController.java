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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafxrummikub.components.TileView;
import javafxrummikub.utils.CustomizablePromptDialog;
import logic.ComputerAI;
import logic.Game;
import logic.MoveTileData;
import logic.Player;
import logic.tile.Color;
import logic.tile.Sequence;
import logic.tile.Tile;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.RummikubWebService;

public class GamePlaySceneController implements Initializable, IGamePlayEventHandler {
    private String clientPlayerName;
    private List<Tile> playerTiles;
    private List<Sequence> boardSequences = new ArrayList<>();
    private static final int TILES_LIST_VIEW_WIDTH = 1050;
    private static final int INDEX_NOT_FOUND = -1;
    private static final String GAME_START_SOUND_PATH = "./src/resources/gameStart.wav";
    private static final String PLAYER_TURN_SOUND_PATH = "./src/resources/notifyTurn.wav";
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
    private ObservableList<Tile> clientPlayerTilesData;
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
    private ScheduledFuture<?> clearMsgTask = null;
    private final ComputerAI ai = new ComputerAI();
    private boolean isPlayerPutNewSequence;
    private RummikubWebService server;
    private int playerID;
    private String gameName;

    @FXML
    private void onMainMenuButton(ActionEvent event) {
        Stage stage = (Stage) mainMenuButton.getScene().getWindow();
        String answer = CustomizablePromptDialog.show(
                stage, "Are you sure you want to exit? All unsaved data will be lost.", "Exit", "Stay");
        if (answer.equals("Exit")) {
            onResignButton(event);
        }
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
        try {
            server.resign(playerID);
        } catch (InvalidParameters_Exception ex) {
            System.err.println(ex.getMessage());
        }
        isMainMenuButtonPressed.set(true);
    }

    @FXML
    private void onFinishTurnButton(ActionEvent event
    ) {
        Player currentPlayer = game.getCurrentPlayer();
        if (isPlayerPerformAnyChange == false) {
            showMessage("No Changes have been made to the board", ERROR_MSG_TYPE);
            return;
        }
        try {
            server.finishTurn(playerID);
        } catch (InvalidParameters_Exception ex) {
            System.err.println(ex.getMessage());
        }
        isCurrPlayerFinished.set(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
        disableAllControls(true);

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
        clientPlayerTilesData = FXCollections.observableArrayList();
        currentPlayerTilesView = getTilesListView(clientPlayerTilesData);
        currentPlayerTilesView.setPrefWidth(TILES_LIST_VIEW_WIDTH);
        tilesContainer.getChildren().add(currentPlayerTilesView);
    }

    private void registerFinishTurnProperty() {
        isCurrPlayerFinished.addListener((source, oldValue, isPlayerFinished) -> {
            if (isPlayerFinished == true) {
                disableAllControls(true);
            }
        });
    }

    public void initGameParmeters(RummikubWebService rummikubGameWS, String gameName, int playerID) {
        this.server = rummikubGameWS;
        this.playerID = playerID;
        this.gameName = gameName;
    }

    private void fillPlayersNames(List<String> names) {
        for (int i = 0; i < names.size(); i++) {
            playersNames.get(i).setText(names.get(i));
        }
    }

    private void updatePlayerNamesWithCurrentPlayer(String currPlayer) {
        for (Label playerNameLabel : playersNames) {
            if (playerNameLabel.getText().toLowerCase().equals(currPlayer.toLowerCase())) {
                playerNameLabel.getStyleClass().add("currentPlayer");
            } else {
                playerNameLabel.getStyleClass().remove("currentPlayer");
            }
        }
    }

    private void updateBoard() {
        ObservableList<Tile> seqBinding;
        ListView<Tile> seqView;
        boardData.clear();

        for (Sequence sequence : boardSequences) {
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
        clientPlayerTilesData.clear();
        //Don't change to forEach, it won't work since the "equals" function of tile which ignores duplicate tiles with the same coloe and value
        for (int i = 0; i < playerTiles.size(); i++) {
            clientPlayerTilesData.add(playerTiles.get(i));
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

    private void disableAllControls(boolean disabled) {
        pullTileButton.setDisable(disabled);
        resignButton.setDisable(disabled);
        finishTurnButton.setDisable(disabled);
        currentPlayerTilesView.setDisable(disabled);
    }

    @Override
    public void gameStart(String playerName, List<String> allPlayerNames, List<Tile> currPlayerTiles) {
        this.clientPlayerName = playerName;
        fillPlayersNames(allPlayerNames);
        markCurrClientPlayerName(playerName);

        // Add tiles to stand
        clientPlayerTilesData.addAll(currPlayerTiles);

        playSound(GAME_START_SOUND_PATH);
    }

    private void playSound(String soundPath) {
        Media sound = new Media(new File(soundPath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void markCurrClientPlayerName(String playerName) {
        for (Label playerNameLabel : playersNames) {
            if (playerNameLabel.getText().toLowerCase().equals(playerName.toLowerCase())) {
                playerNameLabel.getStyleClass().add("clientPlayer");
            }
        }
    }

    @Override
    public void sequenceCreated(List<Tile> tiles, String playerName) {
        boardSequences.add(new Sequence(tiles));
        updateBoard();
        if (!playerName.isEmpty()) {
            showMessage(playerName + " has added a new sequence.", REGULAR_MSG_TYPE);
        }
    }

    @Override
    public void PlayerTurn(String playerName) {
        if (playerName.equals(clientPlayerName)) {
            playSound(PLAYER_TURN_SOUND_PATH);
            disableAllControls(false);
        }
        updatePlayerNamesWithCurrentPlayer(playerName);
    }
}
