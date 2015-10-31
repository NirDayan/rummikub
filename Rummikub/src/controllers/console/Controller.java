package controllers.console;

import controllers.IController;
import logic.GameDetails;
import logic.Event;
import logic.Game;
import views.console.GameView;

public class Controller implements IController {
    
    private final GameView view;  
    
    public Controller() {
        this.view = new GameView();
    }

    @Override
    public GameDetails getInitialGameInput() {
        return view.getUserInput();
    }
    
    @Override
    public void showWrongInitialGameInput() {
        view.showWrongInitialGameInput();
    }
    
    @Override
    public Event getNextEvent() {
        return null;
    }
    
    @Override
    public void renderGame(Game game) {
        view.renderGame(game);
    }
}
