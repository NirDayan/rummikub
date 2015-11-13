package controllers.console;

import controllers.IControllerInputOutput;
import java.util.ArrayList;
import logic.Game;
import logic.GameDetails;
import logic.HumanPlayer;
import logic.Player;
import logic.persistency.FileDetails;

public class GameMainController {
    private final int MAX_PLAYERS_NUM = 4;
    private final IControllerInputOutput inputOutputController;
    private Game game;
    private boolean isPersisted;
    
    public GameMainController(IControllerInputOutput inputOutputController) {
        this.inputOutputController = inputOutputController;
        this.isPersisted = false;
    }
    
    public void start() {
        boolean exitGame = false;
        
        createNewGame();        
        while (!exitGame) {
            playGame(game);
            exitGame = startNewGameOrExit();            
        }
    }
    
    public Game createGameFromXML (String xmlData) {
        //TODO: implement
        GameDetails gameDetails = new GameDetails();
        game = new Game(gameDetails);
        return game;
    }
    
    private void createNewGame() {
        if (inputOutputController.isGameFromFile()) {
            game = createGameFromXML(inputOutputController.getGameFilePath());
        }
        else {
            createGameFromUserInput();
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
        options.add(IControllerInputOutput.UserOptions.ONE.getOption());
        options.add(IControllerInputOutput.UserOptions.TWO.getOption());
        options.add(IControllerInputOutput.UserOptions.THREE.getOption());
        
        inputOutputController.showEndOfGameMenu();
        IControllerInputOutput.UserOptions option = inputOutputController.askUserChooseOption(options);
        
        if (option == IControllerInputOutput.UserOptions.ONE) {
            replayGame();
            return false;
        }
        else if (option == IControllerInputOutput.UserOptions.TWO) {
            createNewGame();
            return false;
        }
        
        return true;
    }
    
    private void replayGame() {
        game.reset();
    }

    private void createGameFromUserInput() {
        boolean isGameInitialized = false;

        while (!isGameInitialized) {
            GameDetails initialUserInput = inputOutputController.getNewGameInput();
            if (isGameInputValid(initialUserInput)) {
                game = new Game(initialUserInput);
                isGameInitialized = true;
            } else {
                inputOutputController.showWrongNewGameInput();
            }
        }
    }
    
    //TODO: more edge cases???
    //TODO: check load from file flow.. currently it unhandeled
    private boolean isGameInputValid(GameDetails input) {
        String[] playerNames = input.getPlayersNames();

        if (input.getTotalPlayersNumber() < 2 || input.getTotalPlayersNumber() > MAX_PLAYERS_NUM) {
            return false;
        }
        if (playerNames.length < input.getHumenPlayersNum()) {
            return false;
        }
        //check names validity and each name is unique
        for (int i = 0; i < input.getHumenPlayersNum(); i++) {
            if (playerNames[i].isEmpty()) {
                return false;
            }
            //check names are unique
            for (int j = i + 1; j < input.getHumenPlayersNum(); j++) {
                if (playerNames[i].equals(playerNames[j])) {
                    return false;
                }
            }
        }

        return true;
    }

    private void playGame(Game game) {
        Player currentPlayer;
        while (!game.checkIsGameOver()) {
            //loop over the players starting from the currentPlayer
            currentPlayer = game.getCurrentPlayer();
            if (!currentPlayer.isResign()) {
                inputOutputController.showGameStatus(game.getBoard(), currentPlayer);
                if (inputOutputController.isPlayerResign(currentPlayer)) {//TODO: What do we need to do with the player tiles?
                     currentPlayer.setIsResign(true);
                 }
                else {
                    handleGameSaving(currentPlayer);
                    //TODO: continue the game flow...
                }
            }
            game.moveToNextPlayer();
        }        
        inputOutputController.showEndOfGame(game.getWinner());
    }
    
    private void handleGameSaving(Player player) {
        if (player instanceof HumanPlayer) {
            FileDetails fileDetails = inputOutputController.askUserToSaveGame(isPersisted);
            if (fileDetails != null && fileDetails.isNewFile()) {
                //TODO: implement - save game to XML file
                isPersisted = true;
            }
        }
    }
}
