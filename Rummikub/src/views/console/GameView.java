package views.console;

import java.util.Scanner;
import logic.GameDetails;

public class GameView {
    
    private final Scanner scanner;
    private final int NEW_GAME = 1;
    private final int LOAD_GAME = 2;  
    
    public GameView () {
        this.scanner = new Scanner(System.in);
    }
    
    public GameDetails getUserInput() {
        GameDetails result = null;
        boolean isInputValid = false;
        String wrongInputStr = "Wrong input. Please choose between options 1, 2:";
        
        System.out.println("Welcome to rummikub game!");
        System.out.println("What would you like to do?");
        System.out.println("1. Create a new game");
        System.out.println("2. Load game from file");
        
        while(!isInputValid) {
            try {
                int initGameOption = scanner.nextInt();
                if (initGameOption == NEW_GAME) {
                    result = this.getNewGameInput();
                    isInputValid = true;
                }
                else if(initGameOption == LOAD_GAME) {
                    result = this.getSavedGameInput();
                    isInputValid = true;
                }
                else {
                    System.out.println(wrongInputStr);
                }
            } catch (Exception e) {
                System.out.println(wrongInputStr);
            }
        }
        
        return result;
    }
    
    private GameDetails getNewGameInput() {
        //TODO: get new game input from the user an check input validity
        //      Currently use a mock
        String[] playersNames = {"player1", "player2", "player3", "player"};
        return new GameDetails(4, 2, 2, "GameName", playersNames, null);
    }
    
    private GameDetails getSavedGameInput() {
        //TODO: get new file path from the user an check input validity
        //      Currently use a mock
        String filePath = "filePath";
        return new GameDetails(0, 0, 0, null, null, filePath);        
    }
}
