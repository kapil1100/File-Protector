import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class EncryptListener implements ActionListener {

    private final String knownFolderName = "df48eabsls3daj6ajhiaj7hdkls";
    private final String knownFileName = "ckaad35dk2eedjk341jaj3jaj8";
    private final String programVersion = "File Protector v2.1";
    private final String versionInfoFileName = "versionInfo.inf";
    private final int numOfRndmFolders = 50;

    private ArrayList<FileNameList> fileNameList;
    private ArrayList<File> oldFolderList;

    public void actionPerformed(ActionEvent e) {

        //saving location of the folder selected by the user in the variable 'rootFolderLoc'.
        File rootFolderLoc = new FolderManager().getSelectedFolder("Select a folder to encrypt: ");

        //if the user didn't selected any folder.
        if (rootFolderLoc == null)
            return;

        //check whether the folder is already encrypted or not.
        if (new EncryptionChecker().isEncrypted(rootFolderLoc)) {
            JOptionPane.showMessageDialog(null,
                    "The folder is already encrypted!!");
            return;
        }

        //display warning message before encryption
        int reply = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to encryptButton this folder."
                        + "\nYou may not be able to restore it!",
                "Warning!", JOptionPane.YES_NO_OPTION);

        // if user not clicked the yes button, then terminate the encryption process
        if (reply != JOptionPane.YES_OPTION)
            return;

        fileNameList = new ArrayList<>();
        oldFolderList = new ArrayList<>();

        String[] fileNames = rootFolderLoc.list();

        Loader loader = new Loader("Reading files...");

        //creating a swing worker.(this will run these commands in background)
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {

                for (String fileName : fileNames) {
                    File currentFilePath = new File(rootFolderLoc + "\\" + fileName);
                    if (currentFilePath.isDirectory()) {
                        // if the currentFilePath rootFolderLoc is a folder then process it further
                        // to change all the files inside the inner folders.
                        processIt(currentFilePath, "");
                    } else
                        changeIt(currentFilePath, "");
                }

                return null;
            }

            @Override
            //hides/disposes the loader when background tasks are done executing.
            protected void done() {
                loader.hideLoader();
            }
        };

        worker.execute();
        loader.showLoader();

        // the old locations and the new locations
        // will be stored in "thefile"
        File thefile = new File(rootFolderLoc +
                "\\" + knownFolderName + "\\" + knownFileName);

        Object[] customButtons = {
                "Ok", "Cancel"
        };

        PasswordManager passwordManager = new PasswordManager();

        String password1 = passwordManager.getPassword("Password:", "Enter Password: ",
                customButtons, rootFolderLoc, "", new String[0]);
        String confirmationPassword, titleMessage = "Password: ", message = "Confirm Password: ";

        //if user clicked ok button on password prompt.
        if (password1 != null) {
            //repeat until confirmation password matches password1.
            do {
                confirmationPassword = passwordManager.getPassword(titleMessage, message,
                        customButtons, rootFolderLoc, "", new String[0]);
                //if the user pressed cancel button on confirm password prompt.
                if (confirmationPassword == null) {
                    JOptionPane.showMessageDialog(null, "Encryption Unsuccessfull!!!");
                    break;
                } else if (confirmationPassword.equals(password1)) {
                    //encrypt the files when user confirms the password.

                    Loader encryptionLoader = new Loader("Encrypting files...");

                    //creating a swing worker.(this will run these commands in background)
                    SwingWorker<Void, Void> encryptionWorker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Object[] customButtons = {"Add Email", "Do not add Email"};

                            encryptFiles(rootFolderLoc, thefile, password1,
                                    new EmailManager().getEmailId("Email-Id:",
                                            "Enter Email-Id: ", customButtons));

                            //add a version info file.
                            addVersionInfo(rootFolderLoc);

                            return null;
                        }

                        @Override
                        //hides/disposes the loader when background tasks are done executing.
                        protected void done() {
                            encryptionLoader.hideLoader();
                        }
                    };

                    encryptionWorker.execute();
                    encryptionLoader.showLoader();

                    JOptionPane.showMessageDialog(null, "Encryption Successfull !!");
                    break;
                } else
                    titleMessage = "Password didn't matched!";
            } while (!confirmationPassword.equals(password1));
        } else {
            //if user didn't clicked ok button.
            JOptionPane.showMessageDialog(null, "Encryption Unsuccessful!!!");
        }
    }

    private void encryptFiles(File rootFolderLoc, File thefile, String password, String emailId) {
        //add randomly named folders and update the fileNameList accordingly.
        ArrayList<String> folderNameList = new FolderManager().addFolders(rootFolderLoc);
        alterFileLocList(folderNameList);
        //save the file (with known file name).
        new FileManager().saveFile(thefile, password, emailId, fileNameList);
        //rename the files from old to new randomly assigned names.
        new FileManager().encryptFileRenamer(rootFolderLoc, fileNameList);
        //delete the old folders
        new FolderManager().deleteOldFolders(oldFolderList);
    }

    private void addVersionInfo(File rootFolderLoc) {
        File infoFile = new File(rootFolderLoc + "\\" + versionInfoFileName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(infoFile));
            writer.write("This folder is encytped using : ");
            writer.newLine();
            writer.write(programVersion);
            writer.newLine();
            writer.newLine();
            writer.write("Created by - Kapil Bansal");
            writer.newLine();
            writer.newLine();
            writer.write("********* Do NOT remove this versionInfo file *********");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        infoFile.setReadOnly();
    }


    //assign a new file name to the file
    private void changeIt(File oldFilePath, String innerDirectories) {
        //generating a random name for the file.
        String rndmFileName = new SessionIdentifierGenerator().nextSessionId();

        String oldFileName = new String(innerDirectories + oldFilePath.getName());
        String newFileName = new String(rndmFileName);
        FileNameList n = new FileNameList(oldFileName, newFileName);
        fileNameList.add(n);
    }

    // read and alter the further files in the inner folder
    private void processIt(File folderPath, String innerDirectories) {
        //adding old folder paths to the list to delete these folders later.
        oldFolderList.add(folderPath);

        String[] fileNames = folderPath.list();
        innerDirectories += folderPath.getName() + "\\";
        for (String fileName : fileNames) {
            File currentFilePath = new File(folderPath + "\\" + fileName);
            if (currentFilePath.isFile()) {
                changeIt(currentFilePath, innerDirectories);
            } else if (currentFilePath.isDirectory())
                processIt(currentFilePath, innerDirectories);
        }
    }

    //setting new file names with new folder names(randomly assigned folder names)
    //or assigning folders to the new files
    private void alterFileLocList(ArrayList<String> folderNameList) {
        int numberOfFiles = fileNameList.size();
        for (int fileIndex = 0, folderIndex = 0; fileIndex < numberOfFiles; fileIndex++, folderIndex++) {
            //since, there are 50 folders
            if (folderIndex == numOfRndmFolders)
                folderIndex = 0;
            fileNameList.get(fileIndex).setNewFileName(folderNameList.get(folderIndex) +
                    "\\" + fileNameList.get(fileIndex).getNewFileName());
        }
    }

    //generates random strings(file/folder names).
    public final class SessionIdentifierGenerator {
        private SecureRandom random = new SecureRandom();

        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }
}

