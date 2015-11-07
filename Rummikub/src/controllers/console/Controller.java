package controllers.console;

import controllers.IController;
import java.util.ArrayList;
import logic.GameDetails;
import logic.Player;
import logic.persistency.FileDetails;
import views.console.GameView;

public class Controller implements IController {
    private final GameView view;  
    
    public Controller() {
        this.view = new GameView();
    }
    
    @Override
    public boolean isPlayerResign(Player player) {
        return view.askPlayerIfResign(player);
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
    public void showWrongNewGameInput() {
        view.showWrongNewGameInput();
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
}
