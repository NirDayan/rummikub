package logic;

import ws.rummikub.PlayerStatus;

public class PlayerDetails {
    private final boolean isHuman;
    private String name;
    private int ID;
    private PlayerStatus status;

    public PlayerDetails(int ID, String name, boolean isHuman, PlayerStatus status) {
        this.ID = ID;
        this.name = name;
        this.isHuman = isHuman;
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public boolean getIsHuman() {
        return isHuman;
    }

    public int getID() {
        return ID;
    }
    
    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
}
