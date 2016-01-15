package javafxrummikub.scenes.gameplay;

import java.util.List;
import logic.tile.Tile;

public interface IGamePlayEventHandler {

    public void gameStart(String playerName, List<String> allPlayerNames, List<Tile> currPlayerTiles);

    public void sequenceCreated(List<Tile> tiles, String playerName);

    public void PlayerTurn(String playerName);

    public void addTile(int playerTilePosition, int targetSequenceIndex, int targetSequencePosition);

    public void moveTile(int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition);

}
