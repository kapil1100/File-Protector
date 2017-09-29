import java.io.File;

public class NameList {

    private File mOldFilePath, mNewFilePath;

    public NameList(File oldFilePath, File newFilePath) {
        mOldFilePath = oldFilePath;
        mNewFilePath = newFilePath;
    }

    public File getOldFilePath() {
        return mOldFilePath;
    }

    public File getNewFilePath() {
        return mNewFilePath;
    }

}
