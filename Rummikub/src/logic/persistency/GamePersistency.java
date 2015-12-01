package logic.persistency;

import xml.Rummikub;
import xml.Tile;
import xml.Players;
import xml.PlayerType;
import xml.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import logic.Board;
import logic.Game;
import logic.Player;
import org.xml.sax.SAXException;

public class GamePersistency {
    private static final String RESOURCES = "resources";

    public Game load(FileDetails fileDetails) throws Exception {
        Schema schema = getSchemaFromXSD();
        InputStream xmlInputStream = new BufferedInputStream(
                new FileInputStream(fileDetails.getFullPath()));

        // Read Form XML and create the XSD Game Object
        Rummikub rummikubXSDObj = readXMLAndCreateRummikubXSDOBj(schema, xmlInputStream);
        
        // Create the game from the XSD Object and verify it is valid
        Game game = new XSDObjToGameObjConverter(rummikubXSDObj).convert();
        if (game.getBoard().isValid() == false){
            throw new PersistencyException("The Board is invalid in the given file.");
        }
        
        // set 'last saved' to the file the game was loaded from
        game.setSavedFileDetails(fileDetails);
        
        return game;
    }

    public void save(FileDetails fileDetails, Game game) throws Exception {
        Schema schema = getSchemaFromXSD();

        // Copy the real game object to an XSD format object
        Rummikub rummikubXSDObj = getRummikubXSDObject(game);

        File file = getFileFromFileDetails(fileDetails, game);
        writeXSDObjToFile(schema, rummikubXSDObj, file);
    }

    private Rummikub readXMLAndCreateRummikubXSDOBj(Schema schema, InputStream xmlInputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        //attach the Schema to the unmarshaller so it will use it to run validation.
        unmarshaller.setSchema(schema);

        return (Rummikub) unmarshaller.unmarshal(xmlInputStream);
    }

    private Schema getSchemaFromXSD() throws SAXException {
        // Get the Schema from the XSD file
        URL csdURL = GamePersistency.class.getResource("/" + RESOURCES + "/" + "rummikub.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(csdURL);
        return schema;
    }

    private Rummikub getRummikubXSDObject(Game game) {
        Rummikub rummikubXSD = new Rummikub();
        rummikubXSD.setName(game.getName());
        rummikubXSD.setCurrentPlayer(game.getCurrentPlayer().getName());
        rummikubXSD.setBoard(getBoardXSDObj(game.getBoard()));
        rummikubXSD.setPlayers(getPlayersXSDObj(game.getPlayers()));
        return rummikubXSD;
    }

    private File getFileFromFileDetails(FileDetails fileDetails, Game game) throws IOException {
        if (fileDetails.isNewFile()) {
            return createFile(fileDetails);
        }
        else {
            File file = new File(game.getSavedFileDetails().getFullPath());
            if (file.canWrite() == false && file.setWritable(true) == false)
                throw new PersistencyException("Access To File Denied");
            return file;
        }
    }

    private File createFile(FileDetails fileDetails) throws IOException {
        File file = new File(fileDetails.getFolderPath(), fileDetails.getFileName());
        File folder = file.getParentFile();
        if (!folder.mkdirs() && !folder.exists())
            throw new PersistencyException("Cannot create the folder: " + folder);
        file.createNewFile();
        if (file.canWrite() == false && file.setWritable(true) == false)
            throw new PersistencyException("Access To File Denied");
        return file;
    }

    private void writeXSDObjToFile(Schema schema, Rummikub rummikubXSDObj, File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(rummikubXSDObj, file);
    }

    private xml.Board getBoardXSDObj(Board gameBoard) {
        xml.Board boardXSD = new xml.Board();
        List<xml.Board.Sequence> xsdSequences = boardXSD.getSequence();
        List<logic.tile.Sequence> gameSequences = gameBoard.getSequences();
        for (logic.tile.Sequence gameSeq : gameSequences) {
            xsdSequences.add(convertRealSequenceToXSD(gameSeq));
        }
        return boardXSD;
    }

    private Players getPlayersXSDObj(List<Player> gamePlayers) {
        Players xsdPlayersContiner = new Players();
        List<Players.Player> xsdPlayersList = xsdPlayersContiner.getPlayer();
        for (Player gamePlayer : gamePlayers) {
            xsdPlayersList.add(convertPlayerToXSDObj(gamePlayer));
        }
        return xsdPlayersContiner;
    }

    private xml.Board.Sequence convertRealSequenceToXSD(logic.tile.Sequence gameSeq) {
        xml.Board.Sequence xsdSeq = new xml.Board.Sequence();
        xsdSeq.getTile().addAll(convertGameTileListToXSD(gameSeq.toList()));
        return xsdSeq;
    }

    private Players.Player convertPlayerToXSDObj(Player gamePlayer) {
        Players.Player xsdPlayer = new Players.Player();
        xsdPlayer.setName(gamePlayer.getName());
        xsdPlayer.setType(getXSDPlayerTypeFromGamePlayer(gamePlayer));
        xsdPlayer.setTiles(convertGameTilesToXSDPlayerTiles(gamePlayer.getTiles()));
        return xsdPlayer;
    }

    private PlayerType getXSDPlayerTypeFromGamePlayer(Player gamePlayer) {
        if (gamePlayer.isHuman())
            return PlayerType.HUMAN;
        else
            return PlayerType.COMPUTER;
    }

    private Players.Player.Tiles convertGameTilesToXSDPlayerTiles(ArrayList<logic.tile.Tile> gameTiles) {
        Players.Player.Tiles xsdTiles = new Players.Player.Tiles();
        xsdTiles.getTile().addAll(convertGameTileListToXSD(gameTiles));
        return xsdTiles;
    }

    private List<Tile> convertGameTileListToXSD(List<logic.tile.Tile> gameTiles) {
        List<Tile> xsdTiles = new ArrayList<>();
        for (logic.tile.Tile gameTile : gameTiles) {
            xsdTiles.add(convertRealTileToXSD(gameTile));
        }
        return xsdTiles;
    }

    private Tile convertRealTileToXSD(logic.tile.Tile gameTile) {
        Tile xsdTile = new Tile();
        xsdTile.setValue(gameTile.getValue());
        xsdTile.setColor(convertGameColorToXSD(gameTile.getColor()));
        return xsdTile;
    }

    private xml.Color convertGameColorToXSD(logic.tile.Color gameColor) {
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

    public static class PersistencyException extends RuntimeException {
        public PersistencyException(String msg) {
            super(msg);
        }
    }
}
