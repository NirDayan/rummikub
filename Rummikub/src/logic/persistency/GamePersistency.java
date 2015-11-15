package logic.persistency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import logic.Board;
import logic.ComputerPlayer;
import logic.Game;
import logic.Player;
import org.xml.sax.SAXException;
import rummikubxml.Board.Sequence;
import rummikubxml.Color;
import rummikubxml.PlayerType;
import rummikubxml.Players;
import rummikubxml.Players.Player.Tiles;
import rummikubxml.Rummikub;
import rummikubxml.Tile;

public class GamePersistency {
    private static final String RESOURCES = "resources";

    public enum PersistencyOptions {
        SAVE, SAVE_AS;
    }

    public static void save(FileDetails fileDetails, Game game) throws Exception {
        // Get the Schema from the XSD file
        URL csdURL = GamePersistency.class.getResource("/" + RESOURCES + "/" + "rummikub.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(csdURL);

        // Copy the real game object to an XSD format object
        Rummikub rummikubByXSD = getRummikubXSDObject(game.getBoard(), game.getPlayers(), game.getCurrentPlayer());

        // Create the file if not exist
        File file = new File(fileDetails.getFolderPath(), fileDetails.getFileName());
        File folder = file.getParentFile();
        if (!folder.mkdirs() && !folder.exists())
            throw new IOException("Cannot create: " + folder);
        
        if (file.canWrite() == false && file.setWritable(true) == false)
            throw new IOException("Access Denied");
        // Write the XSD object to the file specified
        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(rummikubByXSD, file);
    }

    static private Rummikub getRummikubXSDObject(Board gameBoard, List<Player> gamePlayers, Player gameCurrentPlayer) {
        Rummikub rummikubXSD = new Rummikub();
        rummikubXSD.setName("Rummikub#1");//Temp for exercise 1&2 - need to change when multiple games are available
        rummikubXSD.setCurrentPlayer(gameCurrentPlayer.getName());
        rummikubXSD.setBoard(getBoardXSDObj(gameBoard));
        rummikubXSD.setPlayers(getPlayersXSDObj(gamePlayers));
        return rummikubXSD;
    }

    static private rummikubxml.Board getBoardXSDObj(Board gameBoard) {
        rummikubxml.Board boardXSD = new rummikubxml.Board();
        List<rummikubxml.Board.Sequence> xsdSequences = boardXSD.getSequence();
        List<logic.tile.Sequence> gameSequences = gameBoard.getSequences();
        for (logic.tile.Sequence gameSeq : gameSequences) {
            xsdSequences.add(convertRealSequenceToXSD(gameSeq));
        }
        return boardXSD;
    }

    static private Players getPlayersXSDObj(List<Player> gamePlayers) {
        Players xsdPlayersContiner = new Players();
        List<Players.Player> xsdPlayersList = xsdPlayersContiner.getPlayer();
        for (Player gamePlayer : gamePlayers) {
            xsdPlayersList.add(convertPlayerToXSDObj(gamePlayer));
        }
        return xsdPlayersContiner;
    }

    static private Sequence convertRealSequenceToXSD(logic.tile.Sequence gameSeq) {
        rummikubxml.Board.Sequence xsdSeq = new Sequence();
        xsdSeq.getTile().addAll(convertGameTileListToXSD(gameSeq.toList()));
        return xsdSeq;
    }

    static private Players.Player convertPlayerToXSDObj(Player gamePlayer) {
        Players.Player xsdPlayer = new Players.Player();
        xsdPlayer.setName(gamePlayer.getName());
        xsdPlayer.setType(getXSDPlayerTypeFromGamePlayer(gamePlayer));
        xsdPlayer.setTiles(convertGameTilesToXSDPlayerTiles(gamePlayer.getTiles()));
        return xsdPlayer;
    }

    static private PlayerType getXSDPlayerTypeFromGamePlayer(Player gamePlayer) {
        if (gamePlayer instanceof ComputerPlayer)
            return PlayerType.COMPUTER;
        else
            return PlayerType.HUMAN;
    }

    static private Players.Player.Tiles convertGameTilesToXSDPlayerTiles(ArrayList<logic.tile.Tile> gameTiles) {
        Tiles xsdTiles = new Players.Player.Tiles();
        xsdTiles.getTile().addAll(convertGameTileListToXSD(gameTiles));
        return xsdTiles;
    }

    static private List<Tile> convertGameTileListToXSD(List<logic.tile.Tile> gameTiles) {
        List<Tile> xsdTiles = new ArrayList<>();
        for (logic.tile.Tile gameTile : gameTiles) {
            xsdTiles.add(convertRealTileToXSD(gameTile));
        }
        return xsdTiles;
    }

    static private Tile convertRealTileToXSD(logic.tile.Tile gameTile) {
        Tile xsdTile = new Tile();
        xsdTile.setValue(gameTile.getValue());
        xsdTile.setColor(convertGameColorToXSD(gameTile.getColor()));
        return xsdTile;
    }

    static private Color convertGameColorToXSD(logic.tile.Color gameColor) {
        switch (gameColor) {
            case Black:
                return Color.BLACK;
            case Blue:
                return Color.BLUE;
            case Red:
                return Color.RED;
            case Yellow:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }
}
