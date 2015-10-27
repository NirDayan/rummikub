package rummikub;

import rummikub.GameDetails;

public class Rummikub {
    
    private GameDetails gameDetails;

    public static void main(String[] args) {
        System.out.println("This is a Git test, by Lior");
    }    

    public void createGame(String gameName, int computerPlayersNum, int humenPlayersNum) {
        gameDetails = new GameDetails();
        gameDetails.setGameName(gameName);
        gameDetails.setHumenPlayersNum(humenPlayersNum);
        gameDetails.setComputerPlayersNum(computerPlayersNum);
    }
    
    public GameDetails getGameDetails(String gameName) {
        return gameDetails;
    }
    
    
}
