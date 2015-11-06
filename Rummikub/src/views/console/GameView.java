package views.console;

import java.util.Scanner;
import logic.GameDetails;
import java.util.ArrayList;
import java.lang.Integer;
import java.util.InputMismatchException;

public class GameView {
    
    private final Scanner scanner;
    private enum UserOptions {
        FIRST(1),
        SECOND(2);
        
        private final int option;
        
        UserOptions(final int option) {
            this.option = option;
        }
        
        public int getOption() { return option; }
        
        public static UserOptions getOptionByInt(int option) {
            UserOptions res = FIRST;
            switch (option) {
                case 1:
                    res = FIRST;
                    break;
                case 2:
                    res =  SECOND;
                    break;
                default:
                    throw new InputMismatchException();
            }
            //we should not get here
            return res;
        }
    }
    
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
        usrOptions.add(UserOptions.FIRST.getOption());
        usrOptions.add(UserOptions.SECOND.getOption());
        
        UserOptions option = askUserChooseOption(usrOptions);
                
        if (option.equals(UserOptions.SECOND))
            return true;
        return false;
    }
    
    public boolean askPlayerIfResign() {
        return false;
    }
    
    private UserOptions askUserChooseOption(ArrayList<Integer> availableOptions) {
        boolean isInputValid = false;
        int option;
        String wrongInputStr = "Wrong input. Please choose between options: ";
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
                }
            } catch (Exception e) {
                System.out.println(wrongInputStr);
            }
        }     
        
        //we should not get into this line
        return UserOptions.FIRST;
    }
}