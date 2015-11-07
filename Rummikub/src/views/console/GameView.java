package views.console;

import controllers.IController.UserOptions;
import java.util.Scanner;
import logic.GameDetails;
import java.util.ArrayList;
import java.lang.Integer;
import logic.Player;

public class GameView {
    
    private final Scanner scanner;
    
    public GameView () {
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
                
        if (option.equals(UserOptions.TWO))
            return true;
        return false;
    }
    
    public boolean askPlayerIfResign(Player player) {
        System.out.println("Player " + player.getName() + " What would you like to do?");
        System.out.println("1. Continue play");
        System.out.println("2. Resign from game");
        
        ArrayList<Integer> usrOptions = new ArrayList<Integer>();
        usrOptions.add(UserOptions.ONE.getOption());
        usrOptions.add(UserOptions.TWO.getOption());
        
        UserOptions option = askUserChooseOption(usrOptions);
        
        if (option.equals(UserOptions.ONE))
            return false;
        
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
            chooseOptions += availableOptions.get(i) ;
            if (i < availableOptions.size() - 1) {
                chooseOptions += ", ";
            }
            else {
                chooseOptions += ": ";
            }
        }
        System.out.print(chooseOptions);
        
        while(!isInputValid) {
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
}