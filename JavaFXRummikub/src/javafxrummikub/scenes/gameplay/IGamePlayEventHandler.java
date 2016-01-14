package javafxrummikub.scenes.gameplay;

import java.util.List;
import logic.tile.Tile;

public interface IGamePlayEventHandler {

    public void gameStart(String playerName, List<String> allPlayerNames, List<Tile> currPlayerTiles);

    public void sequenceCreated(List<Tile> tiles, String playerName);

}
