package controllers;

import logic.GameDetails;
import logic.Event;
import logic.Game;

public interface IController {    
    public GameDetails getInitialGameInput();
    
    public void showWrongInitialGameInput();
    
    public Event getNextEvent();
    
    public void renderGame(Game game);
}
