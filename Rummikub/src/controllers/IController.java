package controllers;

import java.util.ArrayList;
import java.util.InputMismatchException;
import logic.GameDetails;
import logic.Player;

public interface IController {    
    public enum UserOptions {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5);
        
        private final int option;
        
        UserOptions(final int option) {
            this.option = option;
        }
        
        public int getOption() { return option; }
        
        public static UserOptions getOptionByInt(int option) {
            UserOptions res = ONE;
            switch (option) {
                case 1:
                    res = ONE;
                    break;
                case 2:
                    res =  TWO;
                    break;
                case 3:
                    res = THREE;
                    break;
                case 4:
                    res = FOUR;
                    break;
                case 5:
                    res = FIVE;
                    break;
                default:
                    throw new InputMismatchException();
            }
            //we should not get here
            return res;
        }
    }
    
    public GameDetails getNewGameInput();
    
    public void showWrongNewGameInput();
    
    public boolean isGameFromFile();
    
    public String getGameFilePath();
    
    public boolean isPlayerResign(Player player);
    
    public UserOptions askUserChooseOption(ArrayList<Integer> availableOptions);
    
    public void showEndOfGame(Player winner);
    
    public void showEndOfGameMenu();
}
