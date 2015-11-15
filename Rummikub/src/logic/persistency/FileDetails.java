package logic.persistency;

public class FileDetails {
    private final String folderPath, fileName;
    private final boolean isNewFile;

    public FileDetails(String folderPath, String fileName, boolean isNewFile) {
        this.folderPath = folderPath;
        this.fileName = fileName;
        this.isNewFile = isNewFile;
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
}
