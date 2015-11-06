package controllers.console;

import controllers.IController;
import logic.GameDetails;
import views.console.GameView;

public class Controller implements IController {
    private final GameView view;  
    
    public Controller() {
        this.view = new GameView();
    }
    
    @Override
    public boolean isPlayerResign() {
        return view.askPlayerIfResign();
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
}
