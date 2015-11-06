package controllers;

import logic.GameDetails;

public interface IController {    
    public GameDetails getNewGameInput();
    
    public void showWrongNewGameInput();
    
    public boolean isGameFromFile();
    
    public String getGameFilePath();
    
    public boolean isPlayerResign();
}
