import java.io.File;

public class FileNameList {

    private String mOldFileName, mNewFileName;

    public FileNameList(String oldFileName, String newFileName) {
        mOldFileName = oldFileName;
        mNewFileName = newFileName;
    }

    public String getOldFileName() {
        return mOldFileName;
    }

    public String getNewFileName() {
        return mNewFileName;
    }

}
