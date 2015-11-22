package logic.persistency;

public class FileDetails {
    private String folderPath, fileName;
    private final boolean isNewFile;

    public FileDetails(String folderPath, String fileName, boolean isNewFile) {
        this.folderPath = folderPath;
        this.fileName = fileName;
        this.isNewFile = isNewFile;
        
        if (fileName != null) {
            if (fileName.length() <= 4) {
                this.fileName += ".xml";
            }
            else if (false == fileName.substring(fileName.length() - 4, fileName.length()).equals(".xml")) {
                this.fileName += ".xml";
            }
        }
    }

    public boolean isNewFile() {
        return isNewFile;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullPath() {
        return (folderPath + "\\" + fileName);
    }
}
