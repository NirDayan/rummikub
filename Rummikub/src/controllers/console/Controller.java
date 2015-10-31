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
    public GameDetails getInitialGameInput() {
        return view.getUserInput();
    }
}
