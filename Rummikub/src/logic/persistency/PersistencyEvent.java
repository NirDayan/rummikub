package logic.persistency;

import java.util.EventObject;

public class PersistencyEvent extends EventObject{

    private final FileDetails fileDetails;
    
    public PersistencyEvent(Object source, FileDetails fileDetails) {
        super(source);
        this.fileDetails = fileDetails;
    }    
    
    public FileDetails getFileDetails() {
        return fileDetails;
    }
}
