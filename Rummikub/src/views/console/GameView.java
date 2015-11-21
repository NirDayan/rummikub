package views.console;

import controllers.IControllerInputOutput.UserOptions;
import java.util.Scanner;
import logic.GameDetails;
import java.util.ArrayList;
import java.util.List;
import logic.Board;
import logic.MoveTileData;
import logic.Player;
import logic.PlayerDetails;
import logic.persistency.FileDetails;
import logic.tile.Sequence;
import logic.tile.Tile;

public class GameView {

    private final Scanner scanner;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final int INDEX_NOT_FOUND = -1;
    private static final int END_OF_INPUT = -1;

    public GameView() {
        this.scanner = new Scanner(System.in);
    }

    public GameDetails getNewGameInput(int minPlayersNum, int maxPlayersNum) {
        int playersNumber = getPlayersNumber(minPlayersNum, maxPlayersNum);
        int computerPlayersNumber = getComputerPlayersNumber(playersNumber);
        String gameName = getGameName();
        List<PlayerDetails> playersDetails = getPlayersDetails(playersNumber, computerPlayersNumber);
        
        return new GameDetails("Rummikub", playersDetails, null);
    }

    public void showWrongInputMessage() {
        System.out.println("Wrong input, please try again:");
    }

    public void showErrorMessage(Exception ex) {
        if (ex != null)
            if (ex.getMessage() != null) {
                System.out.println("Error. " + ex.getMessage() + ", please try again:");
            }
            else {
                System.out.println("Error. " + ex.getCause().getMessage() + ", please try again:");
            }
        else {
            System.out.println("Error, please try again:");
        }
    }

    public boolean isGameFromFile() {

        System.out.println("Welcome to rummikub game!");
        System.out.println("What would you like to do?");
        System.out.println("1. Create a new game");
        System.out.println("2. Load game from file");

        ArrayList<Integer> usrOptions = new ArrayList<Integer>();
        usrOptions.add(UserOptions.ONE.getOption());
        usrOptions.add(UserOptions.TWO.getOption());

        UserOptions option = askUserChooseOption(usrOptions);

        if (option.equals(UserOptions.TWO)) {
            return true;
        }
        return false;
    }

    public void showUserActionsMenu(Player player) {
        System.out.println(" What would you like to do?");
        System.out.println("1. Resign from game");
        System.out.println("2. Add tile into the board");
        System.out.println("3. Move tile in the board");
        System.out.println("4. Take one tile from the deck");
        System.out.println("5. Add sequence to the board");
        System.out.println("6. Finish your turn");
    }

    public void showEndOfGameMenu() {
        System.out.println("What would you like to do?");
        System.out.println("1. Replay last game");
        System.out.println("2. Create a new game");
        System.out.println("3. Exit Game");
    }

    public UserOptions askUserChooseOption(ArrayList<Integer> availableOptions) {
        boolean isInputValid = false;
        int option;
        String wrongInputStr = "Wrong input";
        String chooseOptions = "Please choose your option between ";

        for (int i = 0; i < availableOptions.size(); i++) {
            chooseOptions += availableOptions.get(i);
            if (i < availableOptions.size() - 1) {
                chooseOptions += ", ";
            }
            else {
                chooseOptions += ": ";
            }
        }
        System.out.print(chooseOptions);

        while (!isInputValid) {
            try {
                option = scanner.nextInt();
                if (availableOptions.contains(option)) {
                    return UserOptions.getOptionByInt(option);
                }
                else {
                    System.out.println(wrongInputStr);
                    System.out.print(chooseOptions);
                }
            } catch (Exception e) {
                System.out.println(wrongInputStr);
                System.out.print(chooseOptions);
                scanner.next();
            }
        }

        //we should not get into this line
        return UserOptions.ONE;
    }

    public FileDetails askUserToSaveGame(boolean isAlreadySaved, Player player) {
        FileDetails result = null;

        printPlayerName(player);
        System.out.println("Would you like to save the game? Please choose one option: ");
        System.out.println("1. Save");
        System.out.println("2. Continue without saving");

        ArrayList<Integer> options = new ArrayList<>();
        options.add(UserOptions.ONE.getOption());
        options.add(UserOptions.TWO.getOption());

        UserOptions option = askUserChooseOption(options);
        if (option.equals(UserOptions.ONE)) {
            if (isAlreadySaved) {
                System.out.println("Would you like to save the game into the last file,"
                        + " or create a new file? Please choose one option: ");
                System.out.println("1. Save into the last file");
                System.out.println("2. Save as a new file");
                options = new ArrayList<>();
                options.add(UserOptions.ONE.getOption());
                options.add(UserOptions.TWO.getOption());

                option = askUserChooseOption(options);
                if (option.equals(UserOptions.ONE)) {
                    result = new FileDetails(null, null, false);
                }
                else {
                    return getNewFileDetails();
                }
            }
            else {
                return getNewFileDetails();
            }
        }

        return result;
    }

    public void showFinishTurnWithoutAction() {
        System.out.println("You must perform any action before you can finish your turn!");
    }

    public String getXMLFilePath() {
        String fileFullPath = "";
        scanner.nextLine(); //throw away the \n not consumed by nextInt()
        System.out.print("Please enter the file path: ");
        fileFullPath += scanner.nextLine();
        fileFullPath += "\\";
        System.out.print("Please enter the file name: ");
        fileFullPath += scanner.nextLine();
        fileFullPath += ".xml";
        return fileFullPath;
    }

    private FileDetails getNewFileDetails() {
        boolean isInputValid = false;
        String filePath, fileName;
        FileDetails fileDetails = null;

        while (!isInputValid) {
            try {
                scanner.nextLine(); //throw away the \n not consumed by nextInt()
                System.out.print("Please enter the file path: ");
                filePath = scanner.nextLine();
                System.out.print("Please enter the file name: ");
                fileName = scanner.nextLine();
                fileDetails = new FileDetails(filePath, fileName, true);
                isInputValid = true;
            } catch (Exception err) {
                showWrongInputMessage();
                scanner.next();
            }
        }

        return fileDetails;
    }

    public void showEndOfGame(Player winner) {
        System.out.println("GAME OVER!");
        if (winner != null) {
            System.out.println("The winner is: " + winner.getName());
        }
        else {
            System.out.println("There is no winner");
        }
    }

    public void showGameStatus(Board board, Player player) {
        printPlayerName(player);
        System.out.println("------- " + player.getName() + " tiles start -------");
        printTiles(player.getTiles());
        System.out.println("------- " + player.getName() + " tiles end -------");
        System.out.println("------- Board start -------");
        printBoard(board);
        System.out.println("------- Board end -------");
    }

    private void printTiles(List<Tile> tiles) {
        for (int i = 0; i < tiles.size(); i++) {
            System.out.print("[#" + i + "]: ");
            printTile(tiles.get(i));
            System.out.print(", ");
        }
        System.out.println();
    }

    private void printSequene(Sequence sequence) {
        for (int i = 0; i < sequence.getSize(); i++) {
            System.out.print("[#" + i + "]");
            printTile(sequence.getTile(i));
            System.out.print(", ");
        }
    }

    private void printBoard(Board board) {
        List<Sequence> boardSequences = board.getSequences();
        for (int i = 0; i < boardSequences.size(); i++) {
            System.out.print("[Sequence #" + i + "]");
            printSequene(boardSequences.get(i));
            System.out.println();
        }
    }

    private void printTile(Tile tile) {
        if (tile.isJoker()) {
            System.out.print("J");
            return;
        }
        String color;
        switch (tile.getColor()) {
            case Black:
                color = ANSI_BLACK;
                break;
            case Blue:
                color = ANSI_BLUE;
                break;
            case Red:
                color = ANSI_RED;
                break;
            case Yellow:
                color = ANSI_YELLOW;
                break;
            default:
                color = ANSI_RESET;
        }
        System.out.print(color + tile.getValue() + ANSI_RESET);
    }

    public MoveTileData getAddTileData() {
        boolean isInputValid = false;
        int sourceIndex = INDEX_NOT_FOUND;
        int targetSequenceIndex = INDEX_NOT_FOUND;
        int targetSequencePosition = INDEX_NOT_FOUND;
        MoveTileData result = new MoveTileData();

        while (!isInputValid) {
            try {
                scanner.nextLine(); //throw away the \n not consumed by nextInt()
                System.out.print("Please enter the tile index from your hand: ");
                sourceIndex = scanner.nextInt();
                System.out.print("Please enter the sequence index in the board: ");
                targetSequenceIndex = scanner.nextInt();
                System.out.print("Please enter the position in the sequence: ");
                targetSequencePosition = scanner.nextInt();
                isInputValid = true;
            } catch (Exception err) {
                showWrongInputMessage();
                scanner.next();
            }
        }
        result.setSourceSequencePosition(sourceIndex);
        result.setTargetSequenceIndex(targetSequenceIndex);
        result.setTargetSequencePosition(targetSequencePosition);

        return result;
    }

    public MoveTileData getMoveTileData() {
        boolean isInputValid = false;
        int sourceSequenceIndex = INDEX_NOT_FOUND;
        int sourceSequencePosition = INDEX_NOT_FOUND;
        int targetSequenceIndex = INDEX_NOT_FOUND;
        int targetSequencePosition = INDEX_NOT_FOUND;
        MoveTileData result = new MoveTileData();

        while (!isInputValid) {
            try {
                scanner.nextLine(); //throw away the \n not consumed by nextInt()
                System.out.print("Please enter the source sequence index in the board: ");
                sourceSequenceIndex = scanner.nextInt();
                System.out.print("Please enter the position in the sequence: ");
                sourceSequencePosition = scanner.nextInt();
                System.out.print("Please enter the target sequence index in the board: ");
                targetSequenceIndex = scanner.nextInt();
                System.out.print("Please enter the position in the sequence: ");
                targetSequencePosition = scanner.nextInt();
                isInputValid = true;
            } catch (Exception err) {
                showWrongInputMessage();
                scanner.next();
            }
        }
        result.setSourceSequenceIndex(sourceSequenceIndex);
        result.setSourceSequencePosition(sourceSequencePosition);
        result.setTargetSequenceIndex(targetSequenceIndex);
        result.setTargetSequencePosition(targetSequencePosition);

        return result;
    }

    public void askUserFirstSequenceAvailable(Player player) {
        System.out.println("What would you like to do?");
        System.out.println("1. Resign from the game");
        System.out.println("2. Create your first sequence and start play!");
        System.out.println("3. Take one tile from the deck");
    }

    public List<Integer> getOrderedTileIndicesForSequence(Player player) {
        List<Integer> list = new ArrayList<>();
        boolean isFinished = false;
        int index;
        System.out.println("Please enter tile indices in the correct order, one by one. Finish with " + END_OF_INPUT);

        while (!isFinished) {
            try {
                index = scanner.nextInt();
                if (index != END_OF_INPUT) {
                    if (!list.contains(index)) {
                        list.add(index);
                    }
                    else {
                        System.out.println(index + " already entered, please enter another input:");
                    }
                }
                else {
                    isFinished = true;
                }
            } catch (Exception err) {
                showWrongInputMessage();
            }
        }

        return list;
    }

    private void printPlayerName(Player player) {
        System.out.println("=========================================");
        System.out.println("*****  Player " + player.getName() + "  *****");
        System.out.println("=========================================");
    }

    public void punishPlayerMessage(Player player) {
        System.out.println("Invalid sequence on the board.");
        System.out.println("Player " + player.getName() + " is punished!");
    }

    private int getPlayersNumber(int minPlayersNum, int maxPlayersNum) {
        boolean isValid = false;
        int playersNumber = 0;
        
        System.out.println("How many players are participating in the game?");
        while(!isValid) {
            System.out.print("Please choose a number between 2-4: ");
            try{
                playersNumber = scanner.nextInt();
                if (playersNumber >= minPlayersNum && playersNumber <= maxPlayersNum) {
                    isValid = true;
                }
                else {                    
                    showWrongInputMessage();
                }
            }
            catch(Exception e) {
                showWrongInputMessage();
            }
        }
        
        return playersNumber;
    }

    private int getComputerPlayersNumber(int playersNumber) {
        boolean isValid = false;
        int compPlayersNum = 0;
        
        System.out.println("How many computer players are participating in the game?");
        while(!isValid) {
            System.out.print("Please choose a number between 0-" +  playersNumber + ": ");
            try{
                compPlayersNum = scanner.nextInt();
                if (compPlayersNum >= 0 && compPlayersNum <= playersNumber) {
                    isValid = true;
                }
                else {                    
                    showWrongInputMessage();
                }
            }
            catch(Exception e) {
                showWrongInputMessage();
            }
        }
        
        return compPlayersNum;
    }

    private String getGameName() {
        boolean isValid = false;
        String gameName = "Game Name";//default value
        
        scanner.nextLine(); //throw away the \n not consumed by nextInt()
        while(!isValid) {
            System.out.print("Please enter the game name: ");
            try{
                gameName = scanner.nextLine();
                if (!gameName.isEmpty()) {
                    isValid = true;
                }
                else {
                    showWrongInputMessage();
                }
            }
            catch(Exception e) {
                showWrongInputMessage();
            }
        }
        
        return gameName;
    }

    private List<PlayerDetails> getPlayersDetails(int playersNumber, int computerPlayersNumber) {
        List<PlayerDetails> playersDetails = new ArrayList<>();
        int currPlayerID;
        String currPlayerName;
        PlayerDetails currPlayerDetails;
        int humanPlayerIndex = 0;
        int humanPlayersNum = playersNumber - computerPlayersNumber;
        
        for (int i = 0; i < playersNumber; i++) {
            //first create human players
            if (humanPlayerIndex < humanPlayersNum) {
                currPlayerName = getPlayerName(humanPlayerIndex);
                currPlayerDetails = new PlayerDetails(0, currPlayerName, true);
                humanPlayerIndex++;
            } else {
                //create computer player
                currPlayerDetails = new PlayerDetails(0, "computer" + i, false);
            }
            
            playersDetails.add(currPlayerDetails);
        }
        
        return playersDetails;
    }

    private String getPlayerName(int playerIndex) {
        boolean isValid = false;
        int playerValue = (playerIndex + 1);
        String playerName = "Player #" + playerValue;//Default player name
        
        while (!isValid) {
            System.out.print("Please enter the name of human player #" + playerValue + ": ");
            try{
                playerName = scanner.nextLine();
                if (!playerName.isEmpty()) {
                    isValid = true;
                }
                else {
                    showWrongInputMessage();
                }
            }
            catch (Exception e) {
                showWrongInputMessage();
            }
        }   
        
        return playerName;
    }
}
