package logic.persistency;

import generated.Rummikub;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import logic.Game;
import static logic.Game.checkPlayersNameValidity;
import logic.GameDetails;
import org.xml.sax.SAXException;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;

public class GamePersistency {
    private static final String RESOURCES = "resources";
    private static final String EMPTY_XML_ERR_MSG = "Could not load file due to empty XML file";
    private static final String INVALIG_GAME_ERR_MSG = "Could not load file due to empty XML file";
    private static final String BROKEN_FILE_ERR_MSG = "Could not load file due to broken XML file";
    private static final String DUP_GAME_NAME_ERR_MSG = "Could not load file due to duplicate game name";

    public static Game load(String xmlData) throws InvalidParameters_Exception, InvalidXML_Exception {
        Schema schema;
        Rummikub rummikubXSDObj;
        if (xmlData == null) {
            throw new InvalidParameters_Exception(EMPTY_XML_ERR_MSG, null);
        }
        
        try {
            schema = GamePersistency.getSchemaFromXSD();
            InputStream xmlInputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
            rummikubXSDObj = GamePersistency.readXMLAndCreateRummikubXSDOBj(schema, xmlInputStream);
        } catch (SAXException | JAXBException ex) {
            throw new InvalidXML_Exception(BROKEN_FILE_ERR_MSG, null);
        }
        
        GameDetails gameDetails = XSDObjToGameObjConverter.getGameDetailsFromXSDObj(rummikubXSDObj);
        if (!checkPlayersNameValidity(gameDetails.getPlayersNames())) {
            throw new InvalidXML_Exception(INVALIG_GAME_ERR_MSG, null);
        }
        Game game = new Game(gameDetails);
        XSDObjToGameObjConverter.createGameFromXSDObj(game, rummikubXSDObj);
        if (game.getBoard().isValid() == false) {
            throw new InvalidXML_Exception(INVALIG_GAME_ERR_MSG, null);
        }
        return game;
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

    public static class PersistencyException extends RuntimeException {
        public PersistencyException(String msg) {
            super(msg);
        }
    }
}