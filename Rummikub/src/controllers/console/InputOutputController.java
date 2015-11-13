package controllers.console;

import controllers.IControllerInputOutput;
import java.util.ArrayList;
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
    public String getGameFilePath() {
       //TODO: implement
        return "path";
    }

    @Override
    public GameDetails getNewGameInput() {
        return view.getNewGameInput();
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
    public FileDetails askUserToSaveGame(boolean isAlreadySaved) {
        return view.askUserToSaveGame(isAlreadySaved);
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
}