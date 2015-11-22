package controllers.console;

import controllers.IControllerInputOutput;
import controllers.IControllerInputOutput.UserOptions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import logic.ComputerAI;
import logic.Game;
import logic.GameDetails;
import logic.MoveTileData;
import logic.Player;
import logic.PlayerDetails;
import logic.persistency.FileDetails;
import logic.persistency.GamePersistency;
import logic.persistency.GamePersistency.PersistencyException;
import logic.tile.Tile;

public class GameMainController {
    private static final int MAX_PLAYERS_NUM = 4;
    private static final int MIN_PLAYERS_NUM = 2;
    private static final String COMPUTER_NAME_PREFIX = "Computer #";
    private final IControllerInputOutput inputOutputController;
    private Game game;
    private boolean isPersisted;
    private static int nextPlayerID = 1;
    private final ComputerAI computerAI;

    public GameMainController(IControllerInputOutput inputOutputController) {
        this.inputOutputController = inputOutputController;
        this.isPersisted = false;
        this.computerAI = new ComputerAI();
    }

    public void start() {
        boolean exitGame = false;

        createNewGame();
        while (!exitGame) {
            playGame(game);
            exitGame = startNewGameOrExit();
        }
    }

    public Game createGameFromXML(FileDetails fileDetails) throws Exception {
        return GamePersistency.load(fileDetails);
    }

    private void createNewGame() {
        boolean isInputValid = false;
        while (!isInputValid) {
            try {
                if (inputOutputController.isGameFromFile()) {
                    game = createGameFromXML(inputOutputController.getFileDetails());
                    isPersisted = true;
                }
                else {
                    createGameFromUserInput();
                }
                isInputValid = true;
            } catch (PersistencyException ex) {
                inputOutputController.showMessage(ex.getMessage());
            } catch (Exception ex) {
                inputOutputController.showMessage("File is not valid, Please try again.");
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
            GameDetails initialUserInput = inputOutputController.getNewGameInput(MIN_PLAYERS_NUM, MAX_PLAYERS_NUM);
            if (isGameInputValid(initialUserInput)) {
                generateIDsForPlayers(initialUserInput);
                generateComputerPlayersNames(initialUserInput);
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
        if (!checkPlayersNameValidity(input.getPlayersNames()))
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
        boolean isGameOver = false;
        while (!isGameOver) {
            //loop over the players starting from the currentPlayer
            currentPlayer = game.getCurrentPlayer();
            if (!game.isPlayerResign(currentPlayer.getID())) {
                performPlayerGameRound(currentPlayer);
            }
            if (!( isGameOver = game.checkIsGameOver())) {
                game.moveToNextPlayer();
            }            
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
                    setLastSavedFilePath(fileDetails);
                    isPersisted = true;
                    isInputValid = true;
                } catch (PersistencyException ex) {
                    inputOutputController.showMessage(ex.getMessage());
                } catch (Exception ex) {
                    inputOutputController.showMessage("Saving failed, Please try again.");
                }
            }
            else
                isInputValid = true;
        }
    }

    private void performPlayerGameRound(Player player) {
        if (player.isHuman()) {
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
        options.add(IControllerInputOutput.UserOptions.SIX.getOption());
        UserOptions option;
        boolean isPlayerFinished = false;
        boolean isPlayerPerformAnyChange = false;
        boolean isBackupNeeded = true;
        List<Tile> computerSequence = null;

        do {
            if (player.isHuman()) {
                option = getPlayerOption(player, options);
            }
            else {
                computerSequence = computerAI.getRelevantTiles(player.getTiles());
                if (computerSequence != null) {
                    option = UserOptions.FIVE; //Create a new sequence
                }
                else if (isPlayerPerformAnyChange) {
                    option = UserOptions.SIX; //Finish turn
                }
                else {
                    option = UserOptions.FOUR; // Take Tile From Deck
                }
            }

            if (option == UserOptions.ONE) {//Resign                
                handlePlayerResign(player);
                isPlayerFinished = true;
            }
            else if (option == UserOptions.TWO) {//Add tile into the board
                backupTurn(player, isBackupNeeded);
                isBackupNeeded = false;
                isPlayerPerformAnyChange |= handleAddTile(player);
                inputOutputController.showGameStatus(game.getBoard(), player);
            }
            else if (option == UserOptions.THREE) {//Move tile in the board
                backupTurn(player, isBackupNeeded);
                isBackupNeeded = false;
                handleMoveTile(player);
                inputOutputController.showGameStatus(game.getBoard(), player);
            }
            else if (option == UserOptions.FOUR) {//Take one tile from the deck
                if (isPlayerPerformAnyChange) {
                    inputOutputController.showPlayerCantTakeTileAfterChange(player);
                }
                else {
                    handlePlayerTakeTileFromDeck(player);
                    isPlayerFinished = true;
                }
            }
            else if (option == UserOptions.FIVE) {//Create a new sequence
                backupTurn(player, isBackupNeeded);
                isBackupNeeded = false;
                if (player.isHuman()) {
                    if (!createSequence(player)) {
                        inputOutputController.showWrongInputMessage();
                    }
                }
                else {
                    game.createSequenceByTilesList(player.getID(), computerSequence);
                }
                inputOutputController.showGameStatus(game.getBoard(), player);
                isPlayerPerformAnyChange = true;
            }
            else if (option == UserOptions.SIX) {//Finish turn
                isPlayerFinished = handlePlayerFinishTurn(player, isPlayerPerformAnyChange);
            }
        } while (!isPlayerFinished);
    }

    private boolean handleAddTile(Player player) {
        boolean isValid = false;
        MoveTileData addTileData;
        while (!isValid) {
            addTileData = inputOutputController.getAddTileData();
            inputOutputController.playerTryToAddTileToBoard(player, addTileData);
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
            inputOutputController.playerTryToMoveTile(player, moveTileData);
            isValid = game.moveTile(moveTileData);
            if (!isValid) {
                inputOutputController.showWrongInputMessage();
            }
        }

        return isValid;
    }

    private void performFirstStep(Player player) {
        ArrayList<Integer> options = new ArrayList<>();
        options.add(IControllerInputOutput.UserOptions.ONE.getOption());
        options.add(IControllerInputOutput.UserOptions.TWO.getOption());
        options.add(IControllerInputOutput.UserOptions.THREE.getOption());
        UserOptions option;
        List<Tile> computerSequence = null;

        inputOutputController.showGameStatus(game.getBoard(), player);
        inputOutputController.askUserFirstSequenceAvailable(player);
        if (player.isHuman()) {
            option = inputOutputController.askUserChooseOption(options);
        }
        else {
            computerSequence = computerAI.getRelevantTiles(player.getTiles());
            if (computerSequence != null) {
                option = UserOptions.TWO;
            }
            else {
                option = UserOptions.THREE;
            }
        }

        if (option == UserOptions.ONE) {//Resign            
            handlePlayerResign(player);
        }

        else if (option == UserOptions.TWO) {//Put the first sequence
            //If the user creates a wrong sequence, the board won't be changed, so we don't need to restore it
            if (player.isHuman()) {
                if (createSequence(player)) {
                    player.setFirstStepCompleted(true);
                    inputOutputController.showGameStatus(game.getBoard(), player);
                }
                else {
                    punishPlayer(player);
                }
            }
            else if (game.createSequenceByTilesList(player.getID(), computerSequence)) {
                player.setFirstStepCompleted(true);
            }
            else {
                punishPlayer(player);
            }
        }
        else {//Take one tile from the deck
            handlePlayerTakeTileFromDeck(player);
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

    private void setLastSavedFilePath(FileDetails fileDetails) {
        if (fileDetails.isNewFile()) {
            game.setSavedFileDetails(fileDetails);
        }
    }

    private void generateIDsForPlayers(GameDetails gameDetails) {
        for (PlayerDetails playerDetails : gameDetails.getPlayersDetails()) {
            playerDetails.setID(nextPlayerID++);
        }
    }

    private void generateComputerPlayersNames(GameDetails gameDetails) {
        int computerPlayerIndex = 0;
        for (PlayerDetails playerDetails : gameDetails.getPlayersDetails()) {
            if (!playerDetails.isHuman()) {
                playerDetails.setName(COMPUTER_NAME_PREFIX + (computerPlayerIndex + 1));
                computerPlayerIndex++;
            }
        }
    }

    private void handlePlayerTakeTileFromDeck(Player player) {
        game.pullTileFromDeck(player.getID());
        inputOutputController.announcePlayerTakeTileFromDeck(player);
    }

    private void backupTurn(Player player, boolean isBackupNeeded) {
        if (isBackupNeeded) {
            game.getBoard().storeBackup();
            player.storeBackup();
        }
    }

    private UserOptions getPlayerOption(Player player, ArrayList<Integer> options) {
        inputOutputController.showGameStatus(game.getBoard(), player);
        inputOutputController.showUserActionsMenu(player);
        if (player.isHuman()) {
            return inputOutputController.askUserChooseOption(options);
        }
        else {
            //TODO: implement ComputerPlayer actions
            return UserOptions.ONE;
        }
    }

    private void handlePlayerResign(Player player) {
        game.playerResign(player.getID());
        inputOutputController.announcePlayerResigned(player);
    }

    private boolean handlePlayerFinishTurn(Player player, boolean playerPerformAnyChange) {
        inputOutputController.announcePlayerFinishTurn(player);
        if (playerPerformAnyChange) {
            if (!game.getBoard().isValid()) {
                game.getBoard().restoreFromBackup();
                player.restoreFromBackup();
                inputOutputController.announceWrongBoard(player);
                punishPlayer(player);
            }
            return true;
        }
        else {
            inputOutputController.showFinishTurnWithoutAction();
            return false;
        }
    }
}
