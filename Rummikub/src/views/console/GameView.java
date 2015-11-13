package views.console;

import controllers.IController.UserOptions;
import java.util.Scanner;
import logic.GameDetails;
import java.util.ArrayList;
import java.util.List;
import logic.Board;
import logic.Player;
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

    public GameView() {
        this.scanner = new Scanner(System.in);
    }

    public GameDetails getNewGameInput() {
        //TODO: get new game input from the user an check input validity
        //      Currently use a mock
        String[] playersNames = {"player1", "player2", "player3", "player"};
        return new GameDetails(4, 2, 2, "GameName", playersNames, null);
    }

    public String getSavedFilePath() {
        //TODO: get new file path from the user an check input validity
        //      Currently use a mock
        return "filePath";
    }

    public void showWrongNewGameInput() {
        System.out.println("Wrong game input, please try again:");
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

    public boolean askPlayerIfResign(Player player) {
        System.out.println("=========================================");
        System.out.println("*****  Player " + player.getName() + "  *****");
        System.out.println("=========================================");
        System.out.println(" What would you like to do?");
        System.out.println("1. Continue play");
        System.out.println("2. Resign from game");

        ArrayList<Integer> usrOptions = new ArrayList<Integer>();
        usrOptions.add(UserOptions.ONE.getOption());
        usrOptions.add(UserOptions.TWO.getOption());

        UserOptions option = askUserChooseOption(usrOptions);

        if (option.equals(UserOptions.ONE)) {
            return false;
        }

        return true;
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
            } else {
                chooseOptions += ": ";
            }
        }
        System.out.print(chooseOptions);

        while (!isInputValid) {
            try {
                option = scanner.nextInt();
                if (availableOptions.contains(option)) {
                    return UserOptions.getOptionByInt(option);
                } else {
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

    public FileDetails askUserToSaveGame(boolean isAlreadySaved) {
        FileDetails result = null;

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
                } else {
                    return getNewFileDetails();
                }
            } else {
                return getNewFileDetails();
            }
        }

        return result;
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
                System.out.println("Wrong input. Please try again: ");
                scanner.next();
            }
        }

        return fileDetails;
    }

    public void showEndOfGame(Player winner) {
        System.out.println("GAME OVER!");
        if (winner != null) {
            System.out.println("The winner is: " + winner.getName());
        } else {
            System.out.println("There is no winner");
        }
    }

    public void showGameStatus(Board board, Player player) {
        System.out.println("------- Board start -------");
        printBoard(board);
        System.out.println("------- Board end -------");
        System.out.println("------- " + player.getName() + " tiles start -------");
        printTiles(player.getTiles());
        System.out.println("------- " + player.getName() + " tiles end -------");
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
            printTile(sequence.getTile(i));
            System.out.println();
        }
    }

    private void printBoard(Board board) {
        List<Sequence> boardSequences = board.getSequences();
         for (int i = 0; i < boardSequences.size(); i++) {
             System.out.print("[Sequence #" + i + "]");
             printSequene(boardSequences.get(i));
         }
    }

    private void printTile(Tile tile) {
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
}
