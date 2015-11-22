package controllers;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import logic.Board;
import logic.GameDetails;
import logic.MoveTileData;
import logic.Player;
import logic.persistency.FileDetails;

public interface IControllerInputOutput {    
    public enum UserOptions {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6);
        
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
                case 6:
                    res = SIX;
                    break;
                default:
                    throw new InputMismatchException();
            }
            //we should not get here
            return res;
        }
    }
    
    public GameDetails getNewGameInput(int minPlayersNum, int maxPlayersNum);
    
    public void showWrongInputMessage();
    
    public void showMessage(String msg);
    
    public boolean isGameFromFile();
    
    public FileDetails getFileDetails();
    
    public UserOptions askUserChooseOption(ArrayList<Integer> availableOptions);
    
    public void showEndOfGame(Player winner);
    
    public void showEndOfGameMenu();
    
    public void showUserActionsMenu(Player player);
    
    public FileDetails askUserToSaveGame(boolean isAlreadySaved, Player player);
    
    public void showGameStatus(Board board, Player player);
    
    public void showFinishTurnWithoutAction();
    
    public MoveTileData getAddTileData();
    
    public MoveTileData getMoveTileData();
    
    public void askUserFirstSequenceAvailable(Player player);
    
    public List<Integer> getOrderedTileIndicesForSequence(Player player);
    
    public void punishPlayerMessage(Player player);
    
    public void announcePlayerResigned(Player player);
    
    public void playerTryToAddTileToBoard(Player player, MoveTileData addTileData);
    
    public void playerTryToMoveTile(Player player, MoveTileData moveTileData);
    
    public void announcePlayerTakeTileFromDeck(Player player);
    
    public void announcePlayerFinishTurn(Player player);
    
    public void showPlayerCantTakeTileAfterChange(Player player);
    
    public void announceWrongBoard(Player player);
}