package logic;

public class PlayerDetails {
    public final String name;
    public final boolean isHuman;
    public final int ID;

    public PlayerDetails(int ID, String name, boolean isHuman) {
        this.ID = ID;
        this.name = name;
        this.isHuman = isHuman;
    }
}
