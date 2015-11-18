package controllers.console;

import controllers.IControllerInputOutput;
import controllers.IControllerInputOutput.UserOptions;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import logic.Game;
import logic.GameDetails;
import logic.HumanPlayer;
import logic.MoveTileData;
import logic.Player;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;

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

    public Game createGameFromXML(String xmlPath) throws Exception {
        return game = GamePersistency.load(xmlPath);
    }

    private void createNewGame() {
        boolean isInputValid = false;
        while (!isInputValid) {
            try {
                if (inputOutputController.isGameFromFile()) {
                    game = createGameFromXML(inputOutputController.getGameFilePath());
                }
                else {
                    createGameFromUserInput();
                }
                isInputValid = true;
            } catch (FileNotFoundException e) {
                inputOutputController.showErrorMessage(e.getMessage());
            } catch (Exception e) {
                inputOutputController.showWrongInputMessage();
            }
        }
    }

    /**
     * Ask the user to choose between: 1) Replay last game 2) Create a new game
     * 3) Exit Game
     *
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
                game.reset();
                isGameInitialized = true;
            }
            else {
                inputOutputController.showWrongInputMessage();
            }
        }
    }

    private boolean isGameInputValid(GameDetails input) {
        if (input.getTotalPlayersNumber() < 2 || input.getTotalPlayersNumber() > MAX_PLAYERS_NUM) {
            return false;
        }
        if (input.getPlayersNames().size() < input.getHumenPlayersNum()) {
            return false;
        }
        if (checkPlayersNameValidity(input.getPlayersNames()))
            return false;

        return true;
    }

    public static boolean checkPlayersNameValidity(List<String> playersNames) {
        for (String name : playersNames) {
            if (name.isEmpty()) {
                return false;
            }
        }
        //check names are unique
        Set<String> namesSet = new HashSet<>(playersNames);
        if (namesSet.size() < playersNames.size()) {
            return false;
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
        FileDetails fileDetails;
        boolean isInputValid = false;

        while (!isInputValid) {
            fileDetails = inputOutputController.askUserToSaveGame(isPersisted, player);
            if (fileDetails != null) {
                try {
                    GamePersistency.save(fileDetails, game);
                    isPersisted = true;
                    isInputValid = true;
                } catch (Exception ex) {
                    inputOutputController.showErrorMessage(ex.getMessage());
                }
            }
            else
                isInputValid = true;
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
        } while (!isPlayerFinished);
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
