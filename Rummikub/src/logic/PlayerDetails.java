package logic;

public class PlayerDetails {
    private final String name;
    private final boolean isHuman;
    private int ID;

    public PlayerDetails(int ID, String name, boolean isHuman) {
        this.ID = ID;
        this.name = name;
        this.isHuman = isHuman;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public int getID() {
        return ID;
    }
}
