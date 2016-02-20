package servlets.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {
    public static final String PLAYER_ID = "playerId";
    public static final String EVENTS_ID = "eventsId";
    
    public static int getPlayerId (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object attribute = session != null ? session.getAttribute(PLAYER_ID) : null;
        return attribute != null ? (Integer)attribute : -1;
    }
    
    public static int getEventsId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return -1;
        
        Object attribute = session.getAttribute(EVENTS_ID);
        if (attribute == null){
            session.setAttribute(EVENTS_ID, 0);
            return 0;
        }
        else
            return (Integer)attribute;
    }

    public static void incrementEventsId(HttpServletRequest request, int eventsNum) {
        int newEventsId = getEventsId(request) + eventsNum;
        request.getSession(false).setAttribute(EVENTS_ID, newEventsId);
    }
}
