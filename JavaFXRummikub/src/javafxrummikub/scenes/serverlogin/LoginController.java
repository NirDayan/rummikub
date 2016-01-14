package javafxrummikub.scenes.serverlogin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sun.net.util.IPAddressUtil;
import ws.rummikub.RummikubWebService;
import ws.rummikub.RummikubWebServiceService;

public class LoginController implements Initializable {
    @FXML
    private Label errorMsgLabel;
    @FXML
    private Button connectButton;
    @FXML
    private TextField portText;
    @FXML
    private TextField addressText;

    private String serverAddress;
    private int serverPort;
    private RummikubWebService rummikubGameWS;
    private final String webserviceRoot = "rummikub";
    private final String webserviceName = "RummikubWS";
    private SimpleBooleanProperty serverConnected;
    private static final String SERVER_CONFIG_FILE_PATH = "./ServerConfig.xml";
    private static final String CONNECT_FAILED_MSG = "Server Connection Failed";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serverConnected = new SimpleBooleanProperty(false);
        try {
            readServerConfigFile();
            addressText.setText(serverAddress);
            portText.setText(Integer.toString(serverPort));
        } catch (Exception ex) {
            errorMsgLabel.setText("Server Config File not found");
        }
    }

    public RummikubWebService getRummikubGameWS() {
        return rummikubGameWS;
    }

    public SimpleBooleanProperty getServerConnected() {
        return serverConnected;
    }

    @FXML
    private void clearErrorMsg() {
        errorMsgLabel.setText("");
    }

    @FXML
    private void onConnectButtonPressed(ActionEvent event) {
        clearErrorMsg();
        String addressStr = addressText.getText();
        if (!addressStr.isEmpty()) {
            serverAddress = addressStr;
        }

        String portStr = portText.getText();
        if (!portStr.isEmpty()) {
            serverPort = Integer.parseInt(portStr);
        }

        if (!IPAddressUtil.isIPv4LiteralAddress(serverAddress)) {
            errorMsgLabel.setText("Server IP address is invalid.");
            return;
        }

        try {
            createWSClient();
        } catch (Exception ex) {
            errorMsgLabel.setText(CONNECT_FAILED_MSG);
            return;
        }
        buildServerConfigXML();
        getServerConnected().set(true);
    }

    private void createWSClient() throws MalformedURLException {
        URL url = new URL("http://" + serverAddress + ":" + serverPort + "/" + webserviceRoot + "/" + webserviceName);
        RummikubWebServiceService service = new RummikubWebServiceService(url);
        rummikubGameWS = service.getRummikubWebServicePort();
    }

    private void readServerConfigFile() throws Exception {
        File serverConfigFile = new File(SERVER_CONFIG_FILE_PATH);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(serverConfigFile);

        Element rootElement = document.getDocumentElement();

        Node node = rootElement.getElementsByTagName("ServerAddress").item(0);
        serverAddress = node.getFirstChild().getNodeValue();

        node = rootElement.getElementsByTagName("ServerPort").item(0);
        serverPort = Integer.parseInt(node.getFirstChild().getNodeValue());
    }

    private void buildServerConfigXML() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("ServerConfig");
            doc.appendChild(rootElement);

            Element addressElem = doc.createElement("ServerAddress");
            addressElem.appendChild(doc.createTextNode(serverAddress));
            rootElement.appendChild(addressElem);

            Element portElem = doc.createElement("ServerPort");
            portElem.appendChild(doc.createTextNode(Integer.toString(serverPort)));
            rootElement.appendChild(portElem);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SERVER_CONFIG_FILE_PATH));

            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException pce) {
            System.err.println("Could not save ServerConfig.xml");
        }
    }
}
