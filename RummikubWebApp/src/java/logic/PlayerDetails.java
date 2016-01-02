package logic;

public class PlayerDetails {
    private final boolean isHuman;
    private String name;
    private int ID;

    public PlayerDetails(int ID, String name, boolean isHuman) {
        this.ID = ID;
        this.name = name;
        this.isHuman = isHuman;
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
}
