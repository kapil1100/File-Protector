import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class DecryptListener implements ActionListener {

    //maximum number of times wrong password can be entered.
    private final int maxAllowedAttempts = 3;
    private final String knownFolderName = "df48eabsls3daj6ajhiaj7hdkls";
    private final String knownFileName = "ckaad35dk2eedjk341jaj3jaj8";

    private String registeredEmailId = null;

    public void actionPerformed(ActionEvent e) {

        File rootFolderLoc = new FolderManager().getSelectedFolder("Select a folder to restore: ");

        //if the user didn't selected any folder.
        if (rootFolderLoc == null)
            return;

        //if the folder is encrypted then start decryption.
        if (new EncryptionChecker().isEncrypted(rootFolderLoc)) {
            ProgramVersionManager programVersionManager = new ProgramVersionManager();
            //if version file exists
            if (programVersionManager.isVersionFilePresent(rootFolderLoc)) {
                //if the folder is encrypted using same version of program.
                if (programVersionManager.isEncryptedWithSameProgramVersion()) {
                    startRestoration(rootFolderLoc);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "This folder is encrypted using another version of File Protector." +
                                    "\nTry to restore with that version of file protector.",
                            "Can't restore!", JOptionPane.OK_OPTION);

                    programVersionManager.folderIsEncryptedWithProgramVersion();
                    return;
                }
            } else {
                //if version file doesn't exists but folder is encrypted.
                int reply = JOptionPane.showConfirmDialog(null,
                        "This folder is encrypted by unknown version of File Protector." +
                                "\nContinue restoring if you are sure about the program version." +
                                "\nElse you might lose some / all data in this folder.",
                        "Continue?", JOptionPane.OK_CANCEL_OPTION);
                //if user wants to continue restoring.
                if (reply == JOptionPane.OK_OPTION)
                    startRestoration(rootFolderLoc);
            }
        }
        // if the folder is not encrypted
        else {
            JOptionPane.showMessageDialog(null, "Error: Unable to restore!" +
                    "\n Folder is not encrypted.");
            return;
        }
    }//end of actionPerformed() function.


    private void startRestoration(File rootFolderLoc) {
        int reply = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to restore the folder?",
                "Restore", JOptionPane.YES_NO_OPTION);

        // if the YES button is not clicked
        if (reply != JOptionPane.YES_OPTION)
            return;

        File theFile = new File(rootFolderLoc + "\\" + knownFolderName + "\\" + knownFileName);

        try {
            Loader readingFileLoader = new Loader("Reading files...");

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    String[] tokens = new FileManager().getTokensFromFile(theFile);
                    extractPasswordAndEmail(tokens, theFile, rootFolderLoc);

                    return null;
                }

                @Override
                protected void done() {
                    readingFileLoader.hideLoader();
                }
            };

            worker.execute();
            readingFileLoader.showLoader();

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //extracts password and email form the tokens from 'thefile'.
    private void extractPasswordAndEmail(String[] tokens, File theFile, File rootFolderLoc) throws Exception {

        String correctPassword = tokens[0];
        registeredEmailId = tokens[1];
        String titleMessage = "Password: ", message = "Enter Password: ";

        Object[] customButtons = {
                "OK", "Cancel"
        };

        PasswordManager passwordManager = new PasswordManager();

        //get password from user 'maxAllowedAttempts' times.
        for (int attempt = 1; attempt <= maxAllowedAttempts; attempt++) {
            String enteredPassword = passwordManager.getPassword(titleMessage, message,
                    customButtons, rootFolderLoc, registeredEmailId, tokens);
            if (enteredPassword != null) {
                //if the entered password is correct.
                if (passwordManager.checkPassword(enteredPassword, correctPassword)) {
                    //restore the folder and delete 'thefile'.
                    restore(theFile, rootFolderLoc, tokens);
                    break;
                } else {
                    //adding forgot password button on wrong password entry.
                    customButtons = new Object[]{
                            "OK", "Forgot Password!", "Cancel"
                    };
                    //if the entered password is wrong.
                    titleMessage = "Wrong Password!";
                    message = "Enter Again: ";

                    if (attempt < maxAllowedAttempts)
                        //show message box with number of attempts remaining.
                        JOptionPane.showMessageDialog(null, "Wrong Password!\n" +
                                (maxAllowedAttempts - attempt) +
                                " attempt(s) remaining...");
                    else {
                        //if the maximum attempt limit reached then show this message.
                        JOptionPane.showMessageDialog(null, "Restoration Unsuccessful!");
                    }
                }
            } else {
                break;
            }
        }
    }

    //Restores the folder : renames all the files and folders to the original ones.
    public void restore(File theFile, File rootFolderLoc, String[] tokens) throws Exception {

        Loader restoringLoader = new Loader("Restoring files...");

        //creating a swing worker.(this will run these commands in background)
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {

                ArrayList<FileNameList> fileNameList = new FileManager().setUpNameList(tokens);
                //delete versionInfo.inf file
                new FileManager().deleteVersionInfoFile(rootFolderLoc);
                //set up old(randomly named folders in this case) folders list.
                ArrayList<File> oldFolderList = new FolderManager().setUpOldFolderList(rootFolderLoc);

                //make original folders and rename the files.
                new FolderManager().makeOriginalFolders(fileNameList, rootFolderLoc);
                new FileManager().decryptFileRenamer(rootFolderLoc, fileNameList);

                theFile.delete();

                //delete old folders(in this case randomly named folders).
                new FolderManager().deleteOldFolders(oldFolderList);

                return null;
            }

            //hides/disposes the loader when background tasks are done executing.
            @Override
            protected void done() {
                restoringLoader.hideLoader();
            }
        };

        worker.execute();
        restoringLoader.showLoader();

        JOptionPane.showMessageDialog(null, "Restoration successful.");
    }

}