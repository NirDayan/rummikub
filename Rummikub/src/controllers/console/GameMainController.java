package controllers.console;

import controllers.IControllerInputOutput;
import controllers.IControllerInputOutput.UserOptions;
import java.util.ArrayList;
import java.util.List;
import logic.Game;
import logic.GameDetails;
import logic.HumanPlayer;
import logic.MoveTileData;
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
                inputOutputController.showWrongInputMessage();
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
            if (!game.isPlayerResign(currentPlayer.getID())) {
                performPlayerGameRound(currentPlayer);
            }
            game.moveToNextPlayer();
        }        
        inputOutputController.showEndOfGame(game.getWinner());
    }
    
    private void handleGameSaving(Player player) {
        FileDetails fileDetails = inputOutputController.askUserToSaveGame(isPersisted, player);
        if (fileDetails != null && fileDetails.isNewFile()) {
            //TODO: implement - save game to XML file
            isPersisted = true;
        }
    }

    private void performPlayerGameRound(Player player) {
        if (player instanceof HumanPlayer) {
            handleGameSaving(player);
        }
        if (game.isPlayerFirstStep(player.getID())) {
            performFirstStep(player);
        }
        else {
            performPlayerStep(player);
        }
    }
    
    private void performPlayerStep(Player player) {
        ArrayList<Integer> options = new ArrayList<>();
        options.add(IControllerInputOutput.UserOptions.ONE.getOption());
        options.add(IControllerInputOutput.UserOptions.TWO.getOption());
        options.add(IControllerInputOutput.UserOptions.THREE.getOption());
        options.add(IControllerInputOutput.UserOptions.FOUR.getOption());
        options.add(IControllerInputOutput.UserOptions.FIVE.getOption());
        UserOptions option;
        boolean isPlayerFinished = false;
        boolean isPlayerPerformAnyChange = false;
        
        do {
            inputOutputController.showGameStatus(game.getBoard(), player);
            inputOutputController.showUserActionsMenu(player);
            if (player instanceof HumanPlayer) {
                option = inputOutputController.askUserChooseOption(options);
            }
            else {
                //TODO: implement ComputerPlayer actions
                option = UserOptions.ONE;
            }

            if (option == UserOptions.ONE) {//Resign
                //TODO: What do we need to do with the player tiles?
                game.playerResign(player.getID());
                isPlayerFinished = true;
            }
            else if (option == UserOptions.TWO) {//Add tile into the board
                isPlayerPerformAnyChange = handleAddTile(player);
            }
            else if (option == UserOptions.THREE) {//Move tile in the board
                isPlayerPerformAnyChange = handleMoveTile(player);
            }
            else if (option == UserOptions.FOUR) {//Take one tile from the deck
                game.pullTileFromDeck(player.getID());
                isPlayerFinished = true;
            }
            else if (option == UserOptions.FIVE) {//Finish turn
                if (isPlayerPerformAnyChange) {
                    isPlayerFinished = true;
                }
                else {
                    inputOutputController.showFinishTurnWithoutAction();
                }
            }            
        }
        while (!isPlayerFinished);
    }

    private boolean handleAddTile(Player player) {
        boolean isValid = false;
        MoveTileData addTileData;
        while (!isValid) {
            addTileData = inputOutputController.getAddTileData();
            isValid = game.addTile(player.getID(), addTileData);
            if (!isValid) {
                inputOutputController.showWrongInputMessage();
            }
        }
        
        return isValid;
    }

    private boolean handleMoveTile(Player player) {
        boolean isValid = false;
        MoveTileData moveTileData;
        while (!isValid) {
            moveTileData = inputOutputController.getMoveTileData();
            isValid = game.moveTile(moveTileData);
            if (!isValid) {
                inputOutputController.showWrongInputMessage();
            }
        }
        
        return isValid;
    }
    
    private void performFirstStep(Player player) {
        inputOutputController.showGameStatus(game.getBoard(), player);
        if (inputOutputController.askUserFirstSequenceAvailable(player)) {
            if (createSequence(player)) {
                player.setFirstStepCompleted(true);
            }
            else {
                punishPlayer(player);
            }
        }
        else {
            game.pullTileFromDeck(player.getID());
        }
    }
    
    private boolean createSequence(Player player) {
        List<Integer> tilesIndices = inputOutputController.getOrderedTileIndicesForSequence(player);

        return game.createSequence(player.getID(), tilesIndices);
    }
    
    private void punishPlayer(Player player) {
        inputOutputController.punishPlayerMessage(player);
        game.punishPlayer(player.getID());
    }
}
