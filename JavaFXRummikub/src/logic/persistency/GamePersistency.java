package logic.persistency;

import generated.Rummikub;
import generated.Tile;
import generated.Players;
import generated.PlayerType;
import generated.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import static logic.Game.checkPlayersNameValidity;
import logic.GameDetails;
import logic.Player;
import org.xml.sax.SAXException;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;
import ws.rummikub.RummikubWebService;

public class GamePersistency {
    private static final String RESOURCES = "resources";

    public static Game load(FileDetails fileDetails) throws Exception {
        Schema schema = GamePersistency.getSchemaFromXSD();
        InputStream xmlInputStream = new BufferedInputStream(
                new FileInputStream(fileDetails.getFullPath()));

        Rummikub rummikubXSDObj = GamePersistency.readXMLAndCreateRummikubXSDOBj(schema, xmlInputStream);
        GameDetails gameDetails = XSDObjToGameObjConverter.getGameDetailsFromXSDObj(rummikubXSDObj);
        checkPlayersNameValidity(gameDetails.getPlayersNames());
        Game game = new Game(gameDetails);
        XSDObjToGameObjConverter.createGameFromXSDObj(game, rummikubXSDObj);
        if (game.getBoard().isValid() == false)
            throw new PersistencyException("The Board is invalid in the given file.");
        game.setSavedFileDetails(fileDetails);
        return game;
    }

    public static void save(FileDetails fileDetails, Game game) throws Exception {
        Schema schema = getSchemaFromXSD();

        // Copy the real game object to an XSD format object
        Rummikub rummikubXSDObj = getRummikubXSDObject(game);

        File file = getFileFromFileDetails(fileDetails, game);
        writeXSDObjToFile(schema, rummikubXSDObj, file);
    }

    private static Rummikub readXMLAndCreateRummikubXSDOBj(Schema schema, InputStream xmlInputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        //attach the Schema to the unmarshaller so it will use it to run validation.
        unmarshaller.setSchema(schema);

        return (Rummikub) unmarshaller.unmarshal(xmlInputStream);
    }

    private static Schema getSchemaFromXSD() throws SAXException {
        // Get the Schema from the XSD file
        URL csdURL = GamePersistency.class.getResource("/" + RESOURCES + "/" + "rummikub.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(csdURL);
        return schema;
    }

    static private Rummikub getRummikubXSDObject(Game game) {
        Rummikub rummikubXSD = new Rummikub();
        rummikubXSD.setName(game.getName());
        rummikubXSD.setCurrentPlayer(game.getCurrentPlayer().getName());
        rummikubXSD.setBoard(getBoardXSDObj(game.getBoard()));
        rummikubXSD.setPlayers(getPlayersXSDObj(game.getPlayers()));
        return rummikubXSD;
    }

    private static File getFileFromFileDetails(FileDetails fileDetails, Game game) throws IOException {
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

    private static File createFile(FileDetails fileDetails) throws IOException {
        File file = new File(fileDetails.getFolderPath(), fileDetails.getFileName());
        File folder = file.getParentFile();
        if (!folder.mkdirs() && !folder.exists())
            throw new PersistencyException("Cannot create the folder: " + folder);
        file.createNewFile();
        if (file.canWrite() == false && file.setWritable(true) == false)
            throw new PersistencyException("Access To File Denied");
        return file;
    }

    private static void writeXSDObjToFile(Schema schema, Rummikub rummikubXSDObj, File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(rummikubXSDObj, file);
    }

    static private generated.Board getBoardXSDObj(Board gameBoard) {
        generated.Board boardXSD = new generated.Board();
        List<generated.Board.Sequence> xsdSequences = boardXSD.getSequence();
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

    static private generated.Board.Sequence convertRealSequenceToXSD(logic.tile.Sequence gameSeq) {
        generated.Board.Sequence xsdSeq = new generated.Board.Sequence();
        xsdSeq.getTile().addAll(convertGameTileListToXSD(gameSeq.toList()));
        return xsdSeq;
    }

    static private Players.Player convertPlayerToXSDObj(Player gamePlayer) {
        Players.Player xsdPlayer = new Players.Player();
        xsdPlayer.setName(gamePlayer.getName());
        xsdPlayer.setType(getXSDPlayerTypeFromGamePlayer(gamePlayer));
        xsdPlayer.setTiles(convertGameTilesToXSDPlayerTiles(gamePlayer.getTiles()));
        xsdPlayer.setPlacedFirstSequence(gamePlayer.isFirstStep() == false);
        return xsdPlayer;
    }

    static private PlayerType getXSDPlayerTypeFromGamePlayer(Player gamePlayer) {
        if (gamePlayer.isHuman())
            return PlayerType.HUMAN;
        else
            return PlayerType.COMPUTER;
    }

    static private Players.Player.Tiles convertGameTilesToXSDPlayerTiles(ArrayList<logic.tile.Tile> gameTiles) {
        Players.Player.Tiles xsdTiles = new Players.Player.Tiles();
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

    static private generated.Color convertGameColorToXSD(logic.tile.Color gameColor) {
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

    public static void loadGameInServer(FileDetails fileDetails, RummikubWebService server) throws DuplicateGameName_Exception, InvalidParameters_Exception, InvalidXML_Exception, IOException {
        String fileContent = readFile(fileDetails.getFullPath(), StandardCharsets.UTF_8);
        server.createGameFromXML(fileContent);
    }

    public static class PersistencyException extends RuntimeException {
        public PersistencyException(String msg) {
            super(msg);
        }
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
