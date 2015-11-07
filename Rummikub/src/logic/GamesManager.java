package logic;

import controllers.IController;
import controllers.IController.UserOptions;
import java.util.ArrayList;

public class GamesManager {

    private final IController controller;
    private Game game;
    
    public GamesManager (IController controller) {
        this.controller = controller;
    }
    
    public void start() {
        boolean exitGame = false;
        
        createNewGame();        
        while (!exitGame) {
            game.play();
            exitGame = startNewGameOrExit();            
        }
    }
      
    public Game createGameFromXML (String xmlData, IController controller) {
        //TODO: implement
        return new Game(controller);
    }
    
    private void createNewGame() {
        if (controller.isGameFromFile()) {
            game = createGameFromXML(controller.getGameFilePath(), controller);
        }
        else {
            game = new Game(controller);
            game.initNewGame();
        }
    }
    
    /**
     * Ask the user to choose between:
     * 1) Replay last game
     * 2) Create a new game
     * 3) Exit Game
     * @return true if Exit Game, otherwise false
     */
    private boolean startNewGameOrExit() {
        ArrayList<Integer> options = new ArrayList<>();
        options.add(UserOptions.ONE.getOption());
        options.add(UserOptions.TWO.getOption());
        options.add(UserOptions.THREE.getOption());
        
        controller.showEndOfGameMenu();
        UserOptions option = controller.askUserChooseOption(options);
        
        if (option == UserOptions.ONE) {
            replayGame();
            return false;
        }
        else if (option == UserOptions.TWO) {
            createNewGame();
            return false;
        }
        
        return true;
    }
    
    private void replayGame() {
        //TODO: implement
    }
}