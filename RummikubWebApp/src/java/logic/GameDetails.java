package logic;

import java.util.ArrayList;
import java.util.List;
import logic.persistency.FileDetails;
import ws.rummikub.GameStatus;

public class GameDetails {
    private String gameName;
    private int computerPlayersNum;//between 0 to playersNumber
    private int humenPlayersNum;
    private List<PlayerDetails> playersDetails;
    private FileDetails savedFileDetails;
    private GameStatus status;
        
    public GameDetails(String gameName, List<PlayerDetails> playerDetails, 
            FileDetails savedFileDetails, GameStatus status) {
        this.gameName = gameName;
        this.playersDetails = playerDetails;
        humenPlayersNum=0; computerPlayersNum=0;
        for (PlayerDetails details : playerDetails){
            if (details.getIsHuman())
                humenPlayersNum++;
            else
                computerPlayersNum++;
        }
        this.savedFileDetails = savedFileDetails;
        this.status = status;
    }

    public String getGameName() {
        return gameName;
    }
    
    public FileDetails getSavedFileDetails() {
        return savedFileDetails;
    }
    
    public List<String> getPlayersNames() {
        List<String> names = new ArrayList<>();
        for (PlayerDetails p : playersDetails) {
            names.add(p.getName());
        }
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

    public boolean isLoadedFromFile() {
        return savedFileDetails != null;
    }

    GameStatus getStatus() {
        return status;
    }
}
