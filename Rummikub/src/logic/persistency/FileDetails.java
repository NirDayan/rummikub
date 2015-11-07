package logic.persistency;

public class FileDetails {
    private final String path, fileName;
    private final boolean isNewFile;

    public FileDetails(String path, String fileName, boolean isNewFile) {
        this.path = path;
        this.fileName = fileName;
        this.isNewFile = isNewFile;
    }

    public boolean isNewFile() {
        return isNewFile;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }
}
