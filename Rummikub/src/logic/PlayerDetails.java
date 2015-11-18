package logic;

public class PlayerDetails {
    public String name;
    public boolean isHuman;
    public int ID;

    public PlayerDetails(int ID, String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.ID = ID;
    }
}
