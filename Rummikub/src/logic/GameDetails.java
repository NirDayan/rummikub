package logic;

import java.util.ArrayList;
import java.util.List;

public class GameDetails {
    private String gameName;
    private int computerPlayersNum;//between 0 to playersNumber
    private int humenPlayersNum;
    private List<PlayerDetails> playersDetails;
    private String savedFilePath;
    
    public GameDetails() {}
    
    public GameDetails(String gameName, List<PlayerDetails> playerDetails, 
            String savedFilePath) {
        this.gameName = gameName;
        this.playersDetails = playerDetails;
        humenPlayersNum=0; computerPlayersNum=0;
        for (PlayerDetails details : playerDetails){
            if (details.isHuman)
                humenPlayersNum++;
            else
                computerPlayersNum++;
        }
        this.savedFilePath = savedFilePath;
    }

    public String getGameName() {
        return gameName;
    }
    
    public String getSavedFilePath() {
        return savedFilePath;
    }
    
    public List<String> getPlayersNames() {
        List<String> names = new ArrayList<>();
        getPlayersDetails().stream().forEach(p -> names.add(p.name));
        return names;
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

    public List<PlayerDetails> getPlayersDetails() {
        return playersDetails;
    }
}
