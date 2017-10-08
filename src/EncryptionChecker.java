import java.io.File;

public class EncryptionChecker {

    private final String knownFolderName = "df48eabsls3daj6ajhiaj7hdkls";
    private final String knownFileName = "ckaad35dk2eedjk341jaj3jaj8";

    public boolean isEncrypted(File rootFolderLoc) {
        File file = new File(rootFolderLoc + "\\" + knownFolderName + "\\" + knownFileName);
        //return true if the file exists and false if it isn't.
        return file.exists();
    }
}
