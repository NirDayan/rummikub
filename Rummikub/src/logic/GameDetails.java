package logic;

import java.util.List;

public class GameDetails {
    private String gameName;
    private int computerPlayersNum;//between 0 to playersNumber
    private int humenPlayersNum;
    private List<String> playersNames;
    private String savedFilePath;
    
    public GameDetails() {}
    
    public GameDetails(int computerPlayersNum,
            int humenPlayersNum, String gameName, List<String> playersNames, 
            String savedFilePath) {
        this.computerPlayersNum = computerPlayersNum;
        this.humenPlayersNum = humenPlayersNum;
        this.gameName = gameName;
        this.playersNames = playersNames;
        this.savedFilePath = savedFilePath;
    }

    public String getGameName() {
        return gameName;
    }
    
    public String getSavedFilePath() {
        return savedFilePath;
    }
    
    public List<String> getPlayersNames() {
        return playersNames;
    }

    public int getComputerPlayersNum() {
        return computerPlayersNum;
    }

    public int getTotalPlayersNumber() {
        return computerPlayersNum + humenPlayersNum;
    }
    
    public int getHumenPlayersNum() {
        return humenPlayersNum;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setComputerPlayersNum(int computerPlayersNum) {
        this.computerPlayersNum = computerPlayersNum;
    }

    public void setHumenPlayersNum(int humenPlayersNum) {
        this.humenPlayersNum = humenPlayersNum;
    }
}
