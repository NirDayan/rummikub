package logic;

public class GameDetails {
    private String gameName;
    private int computerPlayersNum;
    private int humenPlayersNum;

    public String getGameName() {
        return gameName;
    }

    public int getComputerPlayersNum() {
        return computerPlayersNum;
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
