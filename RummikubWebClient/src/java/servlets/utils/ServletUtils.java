package servlets.utils;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import ws.rummikub.RummikubWebService;
import ws.rummikub.RummikubWebServiceService;

public class ServletUtils {
    private static final String WS_ATTRIBUTE_NAME = "rummikubWS";
    private static final String serverAddress = "127.0.0.1";
    private static final String serverPort = "8080";
    private static final String webserviceRoot = "rummikub";
    private static final String webserviceName = "RummikubWS";

    public static final String INVALID_ADD_TILE_PARAMETERS_ERR_MSG = "Invalid add tile parameters";
    public static final String INVALID_MOVE_TILE_PARAMETERS_ERR_MSG = "Invalid move tile parameters";
    public static final String INVALID_TAKE_BACK_TILE_PARAMETERS_ERR_MSG = "Invalid take back tile parameters";

    public static RummikubWebService getWebService(ServletContext servletContext) {
        if (servletContext.getAttribute(WS_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(WS_ATTRIBUTE_NAME, ConnectToWS());
        }
        return (RummikubWebService) servletContext.getAttribute(WS_ATTRIBUTE_NAME);
    }

    private static RummikubWebService ConnectToWS() {
        try {
            URL url = new URL("http://" + serverAddress + ":" + serverPort + "/" + webserviceRoot + "/" + webserviceName);
            RummikubWebServiceService service = new RummikubWebServiceService(url);
            return service.getRummikubWebServicePort();
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }
}
