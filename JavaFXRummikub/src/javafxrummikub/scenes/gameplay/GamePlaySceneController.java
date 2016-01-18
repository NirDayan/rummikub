package javafxrummikub.scenes.gameplay;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import logic.MoveTileData;
import logic.WSObjToGameObjConverter;
import static logic.WSObjToGameObjConverter.*;
import logic.tile.Color;
import logic.tile.Sequence;
import logic.tile.Tile;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.RummikubWebService;

public class GamePlaySceneController implements Initializable, IGamePlayEventHandler {
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

    private SimpleBooleanProperty isMainMenuButtonPressed;
    private SimpleBooleanProperty isGameOver;
    private String clientPlayerName;
    private List<Tile> playerTiles = new ArrayList<>();
    private final List<Sequence> boardSequences = new ArrayList<>();
    private String gameWinnerName = "";
    private List<Label> playersNames;
    private ObservableList<Tile> clientPlayerTilesData;
    private ListView<Tile> clientPlayerTilesView;
    private ObservableList<ListView<Tile>> boardData;
    private ListView<ListView<Tile>> boardView;
    private boolean isPlayerPerformAnyChange = false;
    private MoveTileData dragTileData;
    private Tile draggedTile;
    private ScheduledFuture<?> clearMsgTask = null;
    private RummikubWebService server;
    private int playerID;
    private String gameName;
    private static final int TILES_LIST_VIEW_WIDTH = 1050;
    private static final int INDEX_NOT_FOUND = -1;
    private static final int FROM_PLAYER = -1;
    private static final String GAME_START_SOUND_PATH = "./src/resources/gameStart.wav";
    private static final String PLAYER_TURN_SOUND_PATH = "./src/resources/notifyTurn.wav";
    private static final String ERROR_MSG_TYPE = "error";
    private static final String REGULAR_MSG_TYPE = "massage";

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
        if (isPlayerPerformAnyChange) {
            showMessage("Pull tile from deck is not possible since you performed board changes", ERROR_MSG_TYPE);
            return;
        }

        try {
            server.finishTurn(playerID);
            disableAllControls(true);
        } catch (InvalidParameters_Exception ex) {
            showMessage(ex.getMessage(), ERROR_MSG_TYPE);
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
    private void onFinishTurnButton(ActionEvent event) {
        if (isPlayerPerformAnyChange == false) {
            showMessage("No Changes have been made to the board", ERROR_MSG_TYPE);
            return;
        }
        try {
            server.finishTurn(playerID);
        } catch (InvalidParameters_Exception ex) {
            System.err.println(ex.getMessage());
        }
        disableAllControls(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGamePlay();
        disableAllControls(true);
    }

    private void initializeGamePlay() {
        isMainMenuButtonPressed = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);

        groupPlayerNamesToList();
        initBoard();
        initCurrentPlayerTilesView();
        clearMsgLabel();
        //Show waiting for players message
        msgLabel.getStyleClass().add(REGULAR_MSG_TYPE);
        msgLabel.setText("Waiting for other players to join...");
    }

    private void groupPlayerNamesToList() {
        playersNames = new ArrayList<>(4);
        playersNames.addAll(Arrays.asList(
                player1Name,
                player2Name,
                player3Name,
                player4Name
        ));
    }

    private void initBoard() {
        boardData = FXCollections.observableArrayList();
        boardView = new ListView<>();
        boardView.setItems(boardData);
    }

    private void initCurrentPlayerTilesView() {
        clientPlayerTilesData = FXCollections.observableArrayList();
        clientPlayerTilesView = getTilesListView(clientPlayerTilesData);
        clientPlayerTilesView.setPrefWidth(TILES_LIST_VIEW_WIDTH);
        tilesContainer.getChildren().add(clientPlayerTilesView);
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

    private void updatePlayerTilesView() {
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
        if (gameWinnerName.isEmpty()) {
            return null;
        } else {
            return gameWinnerName;
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
            updatePlayerTilesView();
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
            moveTileData.setSourceSequenceIndex(FROM_PLAYER);
        }

        //Find the position in the sequence
        for (int i = 0; i < listView.getItems().size(); i++) {
            if (listView.getItems().get(i) == selectedTile) {
                moveTileData.setSourceSequencePosition(i);
            }
        }

        draggedTile = listView.getSelectionModel().getSelectedItem();
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
        //If the source tile is dragged from player's tiles we woule like to present a new sequence placeholder
        if (dragTileData.getSourceSequenceIndex() == INDEX_NOT_FOUND) {
            addPlusTilesToAllSequences();
            addNewSequencePlaceholder();
        } else {
            addNewPlaceholderToPlayerTiles();
            addPlusTilesToFrontAndEnd();
        }
    }

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
        //Send addTile event to the server
        try {
            server.addTile(playerID,
                    WSObjToGameObjConverter.convertGameTileIntoGeneratedTile(draggedTile),
                    addTileData.getTargetSequenceIndex(),
                    addTileData.getTargetSequencePosition());
            disableAllControls(true);
        } catch (InvalidParameters_Exception ex) {
            showMessage(ex.getMessage(), ERROR_MSG_TYPE);
        }
    }

    private void performMoveTileInBoard(MoveTileData moveTileData) {
        if (isMoveTileValid(moveTileData)) {
            showMessage("Invalid move tile action", ERROR_MSG_TYPE);
            return;
        }

        try {
            server.moveTile(playerID,
                    moveTileData.getSourceSequenceIndex(),
                    moveTileData.getSourceSequencePosition(),
                    moveTileData.getTargetSequenceIndex(),
                    moveTileData.getTargetSequencePosition()
            );
        } catch (InvalidParameters_Exception invalidParameters_Exception) {
            showMessage("Invalid move tile action", ERROR_MSG_TYPE);
        }
    }

    private boolean isMoveTileValid(MoveTileData moveTileData) {
        if (moveTileData.getSourceSequenceIndex() == moveTileData.getTargetSequenceIndex()) {
            int seqSize = boardSequences.get(moveTileData.getTargetSequencePosition()).getSize();
            
            if (moveTileData.getTargetSequencePosition() == seqSize) {
                return false;
            }
        }

        return true;
    }

    private void performDragDetected(ListView<Tile> listView) {
        if (listView.getSelectionModel().getSelectedItem() == null) {
            return;
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
            } else if (isTileDraggedFromBoardToPlayer(listView)) {
                performTakeBackTile(dragTileData);
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(true);
            }
        } else {
            event.setDropCompleted(false);
        }

        dragTileData = null;
        draggedTile = null;
    }

    private boolean isTileDraggedFromBoardToPlayer(ListView<Tile> listView) {
        return listView.equals(clientPlayerTilesView)
                && dragTileData.getSourceSequenceIndex() != INDEX_NOT_FOUND;
    }

    private void addNewSequencePlaceholder() {
        ObservableList<Tile> seqBinding = FXCollections.observableArrayList();
        seqBinding.add(new Tile(Color.Red, Tile.PLUS_TILE));
        ListView<Tile> seqView = getTilesListView(seqBinding);
        seqView.getStyleClass().add("sequenceView");
        boardData.add(seqView);
    }

    private void addNewPlaceholderToPlayerTiles() {
        clientPlayerTilesData.add(new Tile(Color.Red, Tile.PLUS_TILE));
    }

    private void performCreateNewSequenceFromPlayer(MoveTileData dragTileData) {
        ArrayList<Tile> tileListToServer = new ArrayList<>();
        tileListToServer.add(draggedTile);
        try {
            server.createSequence(playerID,
                    convertGameTilesListIntoGeneratedTilesList(tileListToServer));
            disableAllControls(true);
        } catch (InvalidParameters_Exception ex) {
            showMessage(ex.getMessage(), ERROR_MSG_TYPE);
        }
    }

    private void performTakeBackTile(MoveTileData dragTileData) {
        try {
            server.takeBackTile(playerID,
                    dragTileData.getSourceSequenceIndex(),
                    dragTileData.getSourceSequencePosition());
            disableAllControls(true);
        } catch (InvalidParameters_Exception ex) {
            showMessage(ex.getMessage(), ERROR_MSG_TYPE);
        }
    }

    private boolean isDropIntoLastSequence() {
        //The player drop tile into a new sequence if the position is the last sequence        
        return dragTileData.getTargetSequenceIndex() == boardData.size() - 1;
    }

    private void addPlusTilesToAllSequences() {
        ObservableList<Tile> sequence;
        for (ListView<Tile> boardSequence : boardData) {
            sequence = boardSequence.getItems();
            for (int j = 0; j < sequence.size() + 1; j = j + 2) {
                sequence.add(j, new Tile(Color.Red, Tile.PLUS_TILE));
            }
        }
    }

    private void handleDropTileFromPlayerTiles() {
        if (isDropIntoLastSequence()) {
            performCreateNewSequenceFromPlayer(dragTileData);
        } else {
            performAddTileToBoard(dragTileData);
        }
    }

    private void disableAllControls(boolean disabled) {
        pullTileButton.setDisable(disabled);
        resignButton.setDisable(disabled);
        finishTurnButton.setDisable(disabled);
        clientPlayerTilesView.setDisable(disabled);
        boardView.setDisable(disabled);
    }

    @Override
    public void gameStart(String playerName, List<String> allPlayerNames, List<Tile> currPlayerTiles) {
        this.clientPlayerName = playerName;
        fillPlayersNames(allPlayerNames);
        markCurrClientPlayerName(playerName);

        // Add tiles to stand
        playerTiles.addAll(currPlayerTiles);
        updatePlayerTilesView();

        playSound(GAME_START_SOUND_PATH);
        showMessage("Game Started!", REGULAR_MSG_TYPE);
    }

    @Override
    public void gameOver() {
        isGameOver.set(true);
    }

    @Override
    public void gameWinner(String winnerName) {
        gameWinnerName = winnerName;
        isGameOver.set(true);
    }

    @Override
    public void playerFinishTurn(List<Tile> tiles, String playerName) {
        if (tiles.size() == 1) {
            if (playerName.equalsIgnoreCase(clientPlayerName)) {
                //handle pullTile
                playerTiles.add(tiles.get(0));
                updatePlayerTilesView();
            } else {
                showMessage(playerName + " has pulled a tile from the deck", REGULAR_MSG_TYPE);
            }
        } else if (!tiles.isEmpty()) {
            System.err.println("playerFinishTurn(): Unknown finish turn event recieved.");
        }
    }

    @Override
    public void playerResigned(String playerName) {
        playersNames.forEach((label) -> {
            if (label.getText().equalsIgnoreCase(playerName))
                label.setText("");
        });
        showMessage(playerName + " Has Resigned.", REGULAR_MSG_TYPE);
    }

    @Override
    public void sequenceCreated(List<Tile> tiles, String playerName) {
        if (playerName.equalsIgnoreCase(clientPlayerName)) {
            playerTiles.remove(tiles.get(0));
            disableAllControls(false);
            updatePlayerTilesView();
            isPlayerPerformAnyChange = true;
        } else if (!playerName.isEmpty()) {
            showMessage(playerName + " has added a new sequence.", REGULAR_MSG_TYPE);
        }
        boardSequences.add(new Sequence(tiles));
        updateBoard();
    }

    @Override
    public void PlayerTurn(String playerName) {
        if (playerName.equalsIgnoreCase(clientPlayerName)) {
            playSound(PLAYER_TURN_SOUND_PATH);
            disableAllControls(false);
            isPlayerPerformAnyChange = false;
        }

        updatePlayerNamesWithCurrentPlayer(playerName);
    }

    @Override
    public void addTile(String playerName, int targetSequenceIndex, int targetSequencePosition, logic.tile.Tile tile) {
        if (playerName.equalsIgnoreCase(clientPlayerName)) {
            // Remove tile from player stand
            playerTiles.remove(tile);
            disableAllControls(false);
            isPlayerPerformAnyChange = true;
            updatePlayerTilesView();
        } else {
            showMessage(playerName + " has added tile to board", REGULAR_MSG_TYPE);
        }
        Sequence targetSeq = boardSequences.get(targetSequenceIndex);
        targetSeq.addTile(targetSequencePosition, tile);

        updateBoard();
    }

    @Override
    public void moveTile(int sourceSeqIndex, int sourceSeqPos, int targetSeqIndex, int targetSeqPos) {
        Tile tile = boardSequences.get(sourceSeqIndex).removeTile(sourceSeqPos);
        Sequence targetSeq = boardSequences.get(targetSeqIndex);
        targetSeq.addTile(targetSeqPos, tile);

        updateBoard();
    }

    @Override
    public void revert(String playerName) {
        if (playerName.equalsIgnoreCase(clientPlayerName)) {
            try {
                playerTiles = convertGeneratedTilesListIntoGameTiles(server.getPlayerDetails(playerID).getTiles());
            } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
                System.err.println(ex.getMessage());
            }
            updatePlayerTilesView();
        } else {
            showMessage(playerName + "was punished with 3 tiles", REGULAR_MSG_TYPE);
        }
        boardSequences.clear();
        updateBoard();
    }

    @Override
    public void tileReturned(String playerName, int sequenceIndex, int SequencePosition, Tile tile) {
        if (playerName.equalsIgnoreCase(clientPlayerName)) {
            // Remove tile from player stand
            playerTiles.add(tile);
            disableAllControls(false);
            updatePlayerTilesView();
        } else {
            showMessage(playerName + "has taken a tile back", gameName);
        }
        Sequence srcSeq = boardSequences.get(sequenceIndex);
        srcSeq.removeTile(SequencePosition);
        if (srcSeq.getSize() == 0) {
            boardSequences.remove(sequenceIndex);
        }

        updateBoard();
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

    private void addPlusTilesToFrontAndEnd() {
        ObservableList<Tile> sequence;
        for (ListView<Tile> boardSequence : boardData) {
            sequence = boardSequence.getItems();
            sequence.add(0, new Tile(Color.Red, Tile.PLUS_TILE));
            sequence.add(sequence.size(), new Tile(Color.Red, Tile.PLUS_TILE));
        }
    }
}
