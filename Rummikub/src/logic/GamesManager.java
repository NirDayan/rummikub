package logic;

import controllers.IController;

public class GamesManager {

    private final IController controller;
    private Game game;
    
    public GamesManager (IController controller) {
        this.controller = controller;
    }
    
    public void start() {
        if (controller.isGameFromFile()) {
            game = createGameFromXML(controller.getGameFilePath(), controller);
        }
        else {
            game = new Game(controller);
            game.initNewGame();
        }
        
        game.play();
    }
    
    public Game createGameFromXML (String xmlData, IController controller) {
        return new Game(controller);
    }
}
