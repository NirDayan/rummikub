
package ws;

import javax.jws.WebService;
import ws.rummikub.DuplicateGameName_Exception;
import ws.rummikub.GameDoesNotExists_Exception;
import ws.rummikub.InvalidParameters_Exception;
import ws.rummikub.InvalidXML_Exception;


@WebService(serviceName = "RummikubWebServiceService", portName = "RummikubWebServicePort", endpointInterface = "ws.rummikub.RummikubWebService", targetNamespace = "http://rummikub.ws/", wsdlLocation = "WEB-INF/wsdl/RummikubWS/RummikubWebServiceService.wsdl")
public class RummikubWS {

    public java.util.List<ws.rummikub.Event> getEvents(int eventId, int playerId) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void createSequence(int playerId, java.util.List<ws.rummikub.Tile> tiles) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name) throws DuplicateGameName_Exception, InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public ws.rummikub.GameDetails getGameDetails(java.lang.String gameName) throws GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public ws.rummikub.PlayerDetails getPlayerDetails(int playerId) throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void addTile(int playerId, int sequenceIndex, int sequencePosition, ws.rummikub.Tile tile) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void moveTile(int playerId, int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void finishTurn(int playerId) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void resign(int playerId) throws InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.String createGameFromXML(java.lang.String xmlData) throws InvalidParameters_Exception, InvalidXML_Exception, DuplicateGameName_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.util.List<ws.rummikub.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
