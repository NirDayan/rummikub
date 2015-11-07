package logic.persistency;

import java.util.EventObject;

public interface IPersistencyListener {
    
    public void handleSaveGame(PersistencyEvent evt);
    
}
