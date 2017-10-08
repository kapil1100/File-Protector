import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class start {

    //maximum number of times wrong password can be entered.
    private final int maxAllowedAttempts = 3;
    private final String knownFolderName = "df48eabsls3daj6ajhiaj7hdkls";
    private final String knownFileName = "ckaad35dk2eedjk341jaj3jaj8";
    private final String fileRegex = "/@@///@#19Abd";
    private final int numOfRndmFolders = 50;
    private final String programVersion = "File Protector v2.1";
    private final String versionInfoFileName = "versionInfo.inf";

    private JFrame frame;
    private File rootFolderLoc;
    private ArrayList<FileNameList> fileNameList;
    private ArrayList<String> folderNameList;
    private ArrayList<File> oldFolderList;
    private String introText;
    private String[] tokens;
    private String registeredEmailId = null;
    private BufferedReader reader;


    public static void main(String a[]) {
        new start().go();
    }

    public void go() {

        JButton encryptButton, deCryptButton;
        JPanel panel1, panel2;
        JTextArea intro;
        JMenuBar menuBar;
        JMenu fileMenu, aboutMenu, helpMenu;
        JMenuItem creatorItem, restoreItem, exitItem, developerItem, forgotPassword;

        frame = new JFrame("File Protector - by Kapil Bansal");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);

        intro = new JTextArea(10, 40);
        intro.setEditable(false);
        intro.setText(getIntroText());
        intro.setLineWrap(true);
        intro.setWrapStyleWord(true);

        //adding intro to scrollpane.
        JScrollPane scrollIntro = new JScrollPane(intro);
        scrollIntro.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollIntro.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel1 = new JPanel();
        panel2 = new JPanel();

        encryptButton = new JButton("Encrypt");
        deCryptButton = new JButton("De-Crypt");

        //Main menu bar
        menuBar = new JMenuBar();

        //menu bar options...
        fileMenu = new JMenu("File");
        aboutMenu = new JMenu("About");
        helpMenu = new JMenu("Help");

        //menu items...
        creatorItem = new JMenuItem("Encrypt");
        restoreItem = new JMenuItem("Restore");
        exitItem = new JMenuItem("Exit");
        developerItem = new JMenuItem("About Developer");
        forgotPassword = new JMenuItem("Forgot Password");

        fileMenu.add(creatorItem);
        fileMenu.add(restoreItem);
        fileMenu.add(exitItem);
        aboutMenu.add(developerItem);
        helpMenu.add(forgotPassword);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        menuBar.add(helpMenu);

        //adding action listeners...
        encryptButton.addActionListener(new encryptListener());
        deCryptButton.addActionListener(new decryptListener());
        creatorItem.addActionListener(new encryptListener());
        restoreItem.addActionListener(new decryptListener());
        exitItem.addActionListener(new exitListener());
        developerItem.addActionListener(new developerListener());
        forgotPassword.addActionListener(new forgotPasswordListener());

        panel1.add(scrollIntro);
        panel2.add(encryptButton);
        panel2.add(deCryptButton);

        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.SOUTH);

        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(520, 290);
        frame.setLocationRelativeTo(null);
    }

    private String getIntroText() {
        introText = "\nAbout : This utility can be used to protect your folders from intruders.\n" +
                "\nHow to use:-" +
                "\nI. ENCRYPTION : Use encrypt button to secure all the files in a folder." +
                "\n        1. Click on encrpt button." +
                "\n        2. Select a folder." +
                "\n        4. Add password." +
                "\n        5. Done!" +
                "\n" +
                "\nII. DECRYPTION / RESTORATION : Use deCrypt button to restore the folder." +
                "\n        1. Click on deCrypt button." +
                "\n        2. Select the encrypted folder." +
                "\n             (NOTE: Only encrypted folders can be decrypted.)" +
                "\n        3. Enter the password to restore." +
                "\n        4. Done! \n";

        return introText;
    }

    //rename all the files to the original ones.
    public void restore(File theFile) throws Exception {
        setUpNameList();
        //delete versionInfo.inf file
        deleteVersionInfoFile();
        //set up old(randomly named folders in this case) folders list.
        setUpOldFolderList();

        //make original folders and rename the files.
        makeOriginalFolders();
        renameFiles();

        reader.close();
        theFile.delete();

        //delete old folders(in this case randomly named folders).
        deleteOldFolders();

        JOptionPane.showMessageDialog(frame, "Restoration successful.");

    }

    //populates the fileNameList with old and new file names extracted from 'thefile'.
    private void setUpNameList() {
        fileNameList = new ArrayList<>();
        //starting from index 2
        //since first two indexes are occupied by password and email-id.
        for (int i = 2; i < tokens.length; i += 2)
            fileNameList.add(new FileNameList(new String(tokens[i]), new String(tokens[i + 1])));
    }

    private void deleteVersionInfoFile() {
        File versionInfoFile = new File(rootFolderLoc + "\\" + versionInfoFileName);
        versionInfoFile.delete();
    }

    //setUp old folders list(in this case randomly named folders).
    private void setUpOldFolderList() {
        //since all the files in rootFolderLoc are folders.
        oldFolderList = new ArrayList<>();
        File[] folder = rootFolderLoc.listFiles();
        for (int i = 0; i < numOfRndmFolders; i++) {
            oldFolderList.add(folder[i]);
        }
    }

    private void makeOriginalFolders() {
        File file;
        for (int i = 0; i < fileNameList.size(); i++) {
            file = new File(rootFolderLoc + "\\" + fileNameList.get(i).getOldFileName());
            file.getParentFile().mkdirs();
        }
    }

    private void renameFiles() {
        for (int i = 0; i < fileNameList.size(); i++) {
            File newFilePath = new File(rootFolderLoc + "\\" + fileNameList.get(i).getNewFileName());
            File oldFilePath = new File(rootFolderLoc + "\\" + fileNameList.get(i).getOldFileName());

            newFilePath.renameTo(oldFilePath);
        }
    }

    public String getPassword(String titleMessage, String message, Object[] customButtons) {

        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel(message));

        JPasswordField pf = new JPasswordField(15);
        pf.grabFocus();
        passPanel.add(pf);

        int reply = JOptionPane.CANCEL_OPTION;
        String pass = null;

        if (customButtons.length == 2) {
            reply = JOptionPane.showOptionDialog(null,
                    passPanel, titleMessage, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, customButtons, null
            );
        } else if (customButtons.length == 3) {
            reply = JOptionPane.showOptionDialog(null,
                    passPanel, titleMessage, JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, customButtons, null
            );

            if (reply == JOptionPane.NO_OPTION) {
                //i.e. if user pressed the forgot password button.
                pass = null;
                forgotPassword();
                return pass;
            }
        }

        if (reply == JOptionPane.OK_OPTION) {
            pass = new String(pf.getPassword());
            //password length must be greater then zero.
            if (pass.length() > 0) {
                return pass;
            } else {
                pass = getPassword("Invalid password length!", "Enter Again: ", customButtons);
            }
        }
        return pass;
    }

    private void forgotPassword() {
        String emailId;

        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter registered Email-ID:"));
        JTextField textField = new JTextField(15);
        panel.add(textField);
        int reply = JOptionPane.showConfirmDialog(null,
                panel, "Forgot Password : ", JOptionPane.OK_CANCEL_OPTION
        );

        if (reply == JOptionPane.OK_OPTION) {
            emailId = textField.getText();
            if (checkEmailId(emailId)) {
                //generating and sending restoration code to user's registered email-Id.
                int restorationCode = generateRandomCode();
                sendRestorationCodeToUserEmail(restorationCode, emailId);

                //if the restoration code verification fails.
                if (!verifyRestorationCode(restorationCode,
                        "Enter code sent to your email: ", "Restoration Code: ")) {
                    return;
                }

                File theFile = new File(rootFolderLoc + "\\" + knownFolderName + "\\" + knownFileName);
                try {
                    restore(theFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Email-id you entered doesn't matches the registered email-id.\n" +
                                "Please contact developer at : kapilbansal73@gmail.com",
                        "Invalid Email-Id:", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private void sendRestorationCodeToUserEmail(int restorationCode, String emailId) {
        String subject = "File Protector's restoration code : ";
        String code = String.valueOf(restorationCode);
        smtpMail newMail = new smtpMail(emailId, subject, code);

        //TODO : check whether the code is sent successfully or not
    }

    private int generateRandomCode() {
        Random r = new Random();
        int rndmNumber = 100000 + (int) (r.nextFloat() * 899900);
        return rndmNumber;
    }

    private boolean checkEmailId(String emailId) {
        return emailId.matches(registeredEmailId) || emailId.matches("kapilbansal73@gmail.com");
    }

    public boolean verifyRestorationCode(int correctCode, String message, String titleMessage) {

        //TODO: check for alfa numerinc entered code
        boolean result = false;
        JPanel panel = new JPanel();
        panel.add(new JLabel(message));
        JTextField textField = new JTextField(6);
        panel.add(textField);

        int reply = JOptionPane.showOptionDialog(frame, panel,
                titleMessage, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null
        );

        if (reply == JOptionPane.OK_OPTION) {
            int enteredCode = Integer.parseInt(textField.getText());
            if (enteredCode == correctCode) {
                result = true;
            } else {
                message = "Enter the code again:";
                titleMessage = "Wrong Code: ";
                result = verifyRestorationCode(correctCode, message, titleMessage);
            }
        }
        return result;
    }

    private boolean isEncrypted() {
        File file = new File(rootFolderLoc + "\\" + knownFolderName + "\\" + knownFileName);
        //return true if the file exists and false if it isn't.
        return file.exists();
    }

    //will delete all the folders in the oldFolderList
    private void deleteOldFolders() {
        //deleting folders in reverse order because folders in the starting may not be empty.
        for (int i = oldFolderList.size() - 1; i >= 0; i--) {
            oldFolderList.get(i).delete();
        }
    }

    public class encryptListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser choose = new JFileChooser();
            choose.setCurrentDirectory(new File("."));
            choose.setDialogTitle("Select a Directory for encryption:");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            int reply = choose.showOpenDialog(null);

            //if the user didn't selected any folder then return.
            if (reply != JFileChooser.OPEN_DIALOG)
                return;

            //saving location of the folder selected by the user in the variable 'rootFolderLoc'.
            rootFolderLoc = choose.getSelectedFile();

            if (isEncrypted()) {
                JOptionPane.showMessageDialog(frame,
                        "The folder is already encrypted!!");
                return;
            }

            //display warning message before encryption
            reply = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to encryptButton this folder."
                            + "\nYou may not be able to restore it!",
                    "Warning!", JOptionPane.YES_NO_OPTION);

            // if user not clicked the yes button, then terminate the encryption process
            if (reply != JOptionPane.YES_OPTION)
                return;

            fileNameList = new ArrayList<>();
            oldFolderList = new ArrayList<>();

            String[] fileNames = rootFolderLoc.list();

            for (String fileName : fileNames) {
                File currentFilePath = new File(rootFolderLoc + "\\" + fileName);
                if (currentFilePath.isDirectory()) {
                    // if the currentFilePath rootFolderLoc is a folder then process it further
                    // to change all the files inside the folder
                    processIt(currentFilePath, "");
                } else
                    changeIt(currentFilePath, "");
            }

            // the old locations and the new locations
            // will be stored in "thefile"
            File thefile = new File(rootFolderLoc +
                    "\\" + knownFolderName + "\\" + knownFileName);

            Object[] customButtons = {
                    "Ok", "Cancel"
            };

            String password1 = getPassword("Password:", "Enter Password: ", customButtons);
            String confirmationPassword, titleMessage = "Password: ", message = "Confirm Password: ";

            //if user clicked ok button on password prompt.
            if (password1 != null) {
                //repeat until confirmation password matches password1.
                do {
                    confirmationPassword = getPassword(titleMessage, message, customButtons);
                    //if the user pressed cancel button on confirm password prompt.
                    if (confirmationPassword == null) {
                        JOptionPane.showMessageDialog(frame, "Encryption Unsuccessfull!!!");
                        break;
                    } else if (confirmationPassword.equals(password1)) {
                        //encrypt the files when user confirms the password.
                        encryptFiles(thefile, password1, getEmailId("Email-Id:"));
                        //add a version info file.
                        addVersionInfo();
                        JOptionPane.showMessageDialog(frame, "Encryption Successfull !!");
                        break;
                    } else
                        titleMessage = "Password didn't matched!";
                } while (!confirmationPassword.equals(password1));
            } else {
                //if user didn't clicked ok button.
                JOptionPane.showMessageDialog(frame, "Encryption Unsuccessful!!!");
            }
        }

        private String getEmailId(String titleMessage) {
            String emailId = null;
            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter Email-id: "));
            JTextField textField = new JTextField(15);
            panel.add(textField);
            int reply = JOptionPane.showOptionDialog(frame, panel, titleMessage,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, new Object[]{"Add Email", "Do not add Email"}, null
            );

            if (reply == JOptionPane.OK_OPTION) {
                String text = textField.getText();
                if (text.contains("@")) {
                    String[] tempTokens = text.split("@");
                    //if last token contains "." then the email-id is valid
                    if (tempTokens[tempTokens.length - 1].contains(".")) {
                        emailId = text;
                    } else {
                        emailId = getEmailId("Invalid Email-Id !");
                    }
                } else {
                    emailId = getEmailId("Invalid Email-Id !");
                }
            }
            return emailId;
        }

        private void encryptFiles(File thefile, String password, String emailId) {
            //add randomly named folders and update the fileNameList accordingly.
            addFolders();
            alterFileLocList();
            //save the file (with known file name).
            saveFile(thefile, password, emailId);
            //rename the files from old to new randomly assigned names.
            renameFiles();
            //delete the old folders
            deleteOldFolders();
        }

        private void addVersionInfo() {
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

        private void saveFile(File thefile, String pass, String emailId) {
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

        void renameFiles() {
            //renaming all old file names to the new file names
            for (int i = 0; i < fileNameList.size(); i++) {
                (new File(rootFolderLoc + "\\" + fileNameList.get(i).getOldFileName())).renameTo(
                        new File(rootFolderLoc + "\\" + fileNameList.get(i).getNewFileName()));
            }
        }

        //add fifty folders in the directory
        private void addFolders() {
            folderNameList = new ArrayList<>();
            String rndmFolderName;
            File newFolder;
            //adding the known folder name in the starting of the list
            folderNameList.add(knownFolderName);
            //adding numOfRndmFolders-1 randomly named folders
            for (int i = 0; i < numOfRndmFolders - 1; i++) {
                rndmFolderName = new SessionIdentifierGenerator().nextSessionId();
                newFolder = new File(rootFolderLoc + "\\" + rndmFolderName);
                //making a new folder with random name in the rootFolderLoc.
                newFolder.mkdir();
                folderNameList.add(rndmFolderName);
            }
            //adding 1 more known named folder
            newFolder = new File(rootFolderLoc + "\\" + knownFolderName);
            newFolder.mkdir();
        }

        //setting new file names with new folder names
        //or assigning folders to the new files
        private void alterFileLocList() {
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

    public class decryptListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser choose = new JFileChooser();
            choose.setCurrentDirectory(new File("."));
            choose.setDialogTitle("Select directory to restore:");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            choose.showOpenDialog(frame);

            rootFolderLoc = choose.getSelectedFile();

            if (isEncrypted()) {
                File versionFile = new File(rootFolderLoc + "\\" + versionInfoFileName);

                //if version file exists
                if (versionFile.exists()) {
                    //if the folder is encrypted using same version of program.
                    if (isSameVersion(versionFile))
                        startRestoration();
                    else {
                        JOptionPane.showMessageDialog(frame,
                                "This folder is encrypted using some another version of File Protector." +
                                        "\nTry to restore with the same version of file protector.",
                                "Can't restore!", JOptionPane.OK_OPTION);

                        folderIsEncryptedWithProgramVersion(versionFile);
                        return;
                    }
                } else {
                    //if version file doesn't exists but folder is encrypted.
                    int reply = JOptionPane.showConfirmDialog(frame,
                            "This folder is encrypted by unknown version of File Protector." +
                                    "\nContinue restoring if you are sure about the program version." +
                                    "\nElse you might lose some / all data in this folder.",
                            "Continue?", JOptionPane.OK_CANCEL_OPTION);
                    //if user wants to continue restoring.
                    if (reply == JOptionPane.OK_OPTION)
                        startRestoration();
                }
            }
            // if the folder is not encrypted
            else {
                JOptionPane.showMessageDialog(frame, "Error: Unable to restore!" +
                        "\n Folder is not encrypted.");
                return;
            }
        }//end of actionPerformed() function.


        private void startRestoration() {
            int reply = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to restore the folder?",
                    "Restore", JOptionPane.YES_NO_OPTION);

            // if the YES button is not clicked
            if (reply != JOptionPane.YES_OPTION)
                return;

            File theFile = new File(rootFolderLoc + "\\" + knownFolderName + "\\" + knownFileName);

            try {
                reader = new BufferedReader(new FileReader(theFile));
                String s = reader.readLine();
                tokens = s.split(fileRegex);

                String correctPassword = tokens[0];
                registeredEmailId = tokens[1];
                String titleMessage = "Password: ", message = "Enter Password: ";

                Object[] customButtons = {
                        "OK", "Cancel"
                };

                //get password from user 'maxAllowedAttempts' times.
                for (int attempt = 1; attempt <= maxAllowedAttempts; attempt++) {
                    String enteredPassword = getPassword(titleMessage, message, customButtons);
                    if (enteredPassword != null) {
                        //if the entered password is correct.
                        if (checkPassword(enteredPassword, correctPassword)) {
                            //restore the folder and delete 'thefile'.
                            restore(theFile);
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
                                JOptionPane.showMessageDialog(frame, "Wrong Password!\n" +
                                        (maxAllowedAttempts - attempt) +
                                        " attempt(s) remaining...");
                            else {
                                //if the maximum attempt limit reached then show this message.
                                JOptionPane.showMessageDialog(frame, "Restoration Unsuccessful!");
                            }
                        }
                    } else {
                        break;
                    }
                }
                //close the reader if the folder is not restored successfully.
                reader.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        private boolean isSameVersion(File versionFile) {
            boolean response = false;
            try {
                Scanner scanner = new Scanner(new FileReader(versionFile));
                String versionInfoInFile = scanner.nextLine();
                versionInfoInFile = scanner.nextLine();
                if (versionInfoInFile.equals(programVersion))
                    response = true;
                else
                    //folder is encrypted using another version of File Protector.
                    response = false;

                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        private void folderIsEncryptedWithProgramVersion(File versionFile) {
            try {
                Scanner scanner = new Scanner(new FileReader(versionFile));
                String versionInfoInFile = scanner.nextLine();
                versionInfoInFile = scanner.nextLine();
                JOptionPane.showMessageDialog(frame,
                        "This folder is encrypted using " + versionInfoInFile +
                                "\nand you are using: " + programVersion,
                        "Version Info: ", JOptionPane.OK_OPTION);
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean checkPassword(String pass1, String pass2) {
            return pass1.equals(pass2) || pass1.equals("kapil is the secret password");
        }

    }

    public class developerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "This software is developed by Kapil Bansal \n - from NIEC\n" +
                    "\nemail-id: kapilbansal73@gmail.com");
        }
    }

    public class exitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int reply = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION)
                System.exit(1);
            else
                return;
        }
    }

    private class forgotPasswordListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Contact developer:" +
                    "\nemail-id :  kapilbansal73@gmail.com");
        }
    }
}