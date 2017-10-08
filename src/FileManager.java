import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileManager {
    private final String fileRegex = "/@@///@#19Abd";
    private final String versionInfoFileName = "versionInfo.inf";

    //populates the fileNameList with old and new file names extracted from 'thefile'.
    public ArrayList<FileNameList> setUpNameList(String[] tokens) {
        ArrayList<FileNameList> fileNameList = new ArrayList<>();
        //starting from index 2
        //since first two indexes are occupied by password and email-id.
        for (int i = 2; i < tokens.length; i += 2)
            fileNameList.add(new FileNameList(new String(tokens[i]), new String(tokens[i + 1])));

        return fileNameList;
    }

    public void deleteVersionInfoFile(File rootFolderLoc) {
        File versionInfoFile = new File(rootFolderLoc + "\\" + versionInfoFileName);
        versionInfoFile.delete();
    }

    public void saveFile(File thefile, String pass, String emailId, ArrayList<FileNameList> fileNameList) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(thefile));
            //writing password to the beginning of 'thefile'
            writer.write(pass + fileRegex);
            //writing emailId after password.
            writer.write(emailId + fileRegex);

            //writing old and new file names

            for (FileNameList fileName : fileNameList) {
                writer.write(fileName.getOldFileName() + fileRegex);
                writer.write(fileName.getNewFileName() + fileRegex);
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //renames the files while encrypting .
    void encryptFileRenamer(File rootFolderLoc, ArrayList<FileNameList> fileNameList) {
        //renaming all old file names to the new file names
        for (int i = 0; i < fileNameList.size(); i++) {
            (new File(rootFolderLoc + "\\" + fileNameList.get(i).getOldFileName())).renameTo(
                    new File(rootFolderLoc + "\\" + fileNameList.get(i).getNewFileName()));
        }
    }

    //renames the files to the old names while restoring.
    void decryptFileRenamer(File rootFolderLoc, ArrayList<FileNameList> fileNameList) {
        for (int i = 0; i < fileNameList.size(); i++) {
            File newFilePath = new File(rootFolderLoc + "\\" + fileNameList.get(i).getNewFileName());
            File oldFilePath = new File(rootFolderLoc + "\\" + fileNameList.get(i).getOldFileName());

            newFilePath.renameTo(oldFilePath);
        }
    }
}
