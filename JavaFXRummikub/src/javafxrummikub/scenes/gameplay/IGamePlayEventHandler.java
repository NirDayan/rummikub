package javafxrummikub.scenes.gameplay;

import java.util.List;
import ws.rummikub.Tile;

public interface IGamePlayEventHandler {

    public void gameStart(String playerName, List<String> allPlayerNames, List<Tile> currPlayerTiles);

}
