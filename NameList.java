import java.io.File;

public class NameList {

	private File mOldFile,mNewFile;

	public NameList(File oldFile, File newFile) {
		mOldFile = oldFile;
		mNewFile = newFile;
	}

	public File getOldFile() {
		return mOldFile;
	}

	public File getNewFile() {
		return mNewFile;
	}

}
