package controllers.console;

import controllers.IControllerInputOutput;
import java.util.ArrayList;
import java.util.List;
import logic.Board;
import logic.GameDetails;
import logic.MoveTileData;
import logic.Player;
import logic.persistency.FileDetails;
import views.console.GameView;

public class InputOutputController implements IControllerInputOutput {
    private final GameView view;  
    
    public InputOutputController() {
        this.view = new GameView();
    }

    @Override
    public FileDetails getFileDetails() {
        return view.getNewFileDetails();
    }

    @Override
    public GameDetails getNewGameInput(int minPlayersNum, int maxPlayersNum) {
        return view.getNewGameInput(minPlayersNum, maxPlayersNum);
    }
    
    @Override
    public boolean isGameFromFile() {
        return view.isGameFromFile();
    }
    
    @Override
    public void showWrongInputMessage() {
        view.showWrongInputMessage();
    }
    
    @Override
    public void showMessage(String msg){
        view.showMessage(msg);
    }

    @Override
    public UserOptions askUserChooseOption(ArrayList<Integer> availableOptions) {
        return view.askUserChooseOption(availableOptions);
    }

    @Override
    public void showEndOfGameMenu() {
        view.showEndOfGameMenu();
    }

    @Override
    public void showEndOfGame(Player winner) {
        view.showEndOfGame(winner);
    }

    @Override
    public FileDetails askUserToSaveGame(boolean isAlreadySaved, Player player) {
        return view.askUserToSaveGame(isAlreadySaved, player);
    }

    @Override
    public void showGameStatus(Board board, Player player) {
        view.showGameStatus(board, player);
    }

    @Override
    public void showUserActionsMenu(Player player) {
        view.showUserActionsMenu(player);
    }

    @Override
    public void showFinishTurnWithoutAction() {
        view.showFinishTurnWithoutAction();
    }
    
    @Override
    public MoveTileData getAddTileData() {
        return view.getAddTileData();
    }

    @Override
    public MoveTileData getMoveTileData() {
        return view.getMoveTileData();
    }

    @Override
    public void askUserFirstSequenceAvailable(Player player) {
        view.askUserFirstSequenceAvailable(player);
    }

    @Override
    public List<Integer> getOrderedTileIndicesForSequence(Player player) {
        return view.getOrderedTileIndicesForSequence(player);
    }

    @Override
    public void punishPlayerMessage(Player player) {
        view.punishPlayerMessage(player);
    }
    
    @Override
    public void announcePlayerResigned(Player player) {
        view.announcePlayerResigned(player);
    }

    @Override
    public void playerTryToAddTileToBoard(Player player, MoveTileData addTileData) {
        view.playerTryToAddTileToBoard(player, addTileData);
    }

    @Override
    public void playerTryToMoveTile(Player player, MoveTileData moveTileData) {
        view.playerTryToMoveTile(player, moveTileData);
    }

    @Override
    public void announcePlayerTakeTileFromDeck(Player player) {
        view.announcePlayerTakeTileFromDeck(player);
    }

    @Override
    public void announcePlayerFinishTurn(Player player) {
        view.announcePlayerFinishTurn(player);
    }

    @Override
    public void showPlayerCantTakeTileAfterChange(Player player) {
        view.showPlayerCantTakeTileAfterChange(player);
    }

    @Override
    public void announceWrongBoard(Player player) {
        view.announceWrongBoard(player);
    }
}