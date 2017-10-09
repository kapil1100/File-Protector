import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class FolderManager {

    private final String knownFolderName = "df48eabsls3daj6ajhiaj7hdkls";
    private final int numOfRndmFolders = 50;

    //prompts the user to select a folder to encrypt or decrypt.
    public File getSelectedFolder(String dialogTitle) {
        JFileChooser choose = new JFileChooser();
        choose.setCurrentDirectory(new File("."));
        choose.setDialogTitle(dialogTitle);
        choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        choose.setAcceptAllFileFilterUsed(false);
        int reply = choose.showOpenDialog(null);

        //if the user didn't selected any folder then return.
        if (reply != JFileChooser.OPEN_DIALOG)
            return null;

        return choose.getSelectedFile();
    }

    //setUp old folders list(in this case randomly named folders).
    public ArrayList<File> setUpOldFolderList(File rootFolderLoc) {
        //since all the files in rootFolderLoc are folders.
        ArrayList<File> oldFolderList = new ArrayList<>();
        File[] folder = rootFolderLoc.listFiles();
        for (int i = 0; i < numOfRndmFolders; i++) {
            if (folder[i].isFile())
                i--;
            else
                oldFolderList.add(folder[i]);
        }
        return oldFolderList;
    }

    //creates the original folders in rootFolderLoc during restoration(with all the inner folders).
    public void makeOriginalFolders(ArrayList<FileNameList> fileNameList, File rootFolderLoc) {
        File file;
        for (int i = 0; i < fileNameList.size(); i++) {
            file = new File(rootFolderLoc + "\\" + fileNameList.get(i).getOldFileName());
            file.getParentFile().mkdirs();
        }
    }

    //will delete all the folders in the oldFolderList
    public void deleteOldFolders(ArrayList<File> oldFolderList) {
        //deleting folders in reverse order because folders in the starting may not be empty.
        for (int i = oldFolderList.size() - 1; i >= 0; i--) {
            oldFolderList.get(i).delete();
        }
    }

    //add fifty folders in the directory
    public ArrayList<String> addFolders(File rootFolderLoc) {
        ArrayList<String> folderNameList = new ArrayList<>();
        String rndmFolderName;
        File newFolder;
        //adding the known folder name in the starting of the list
        folderNameList.add(knownFolderName);

        //adding numOfRndmFolders-1 randomly named folders
        for (int i = 0; i < numOfRndmFolders - 1; i++) {
            rndmFolderName = (new EncryptListener().new SessionIdentifierGenerator()).nextSessionId();
            newFolder = new File(rootFolderLoc + "\\" + rndmFolderName);
            //making a new folder with random name in the rootFolderLoc.
            newFolder.mkdir();
            folderNameList.add(rndmFolderName);
        }
        //adding 1 more known named folder
        newFolder = new File(rootFolderLoc + "\\" + knownFolderName);
        newFolder.mkdir();

        return folderNameList;
    }
}
