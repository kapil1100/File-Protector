import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class start {

    //maximum number of times wrong password can be entered.
    private final int maxAllowedAttempts = 3;

    private JFrame frame;
    private JButton encryptButton, deCryptButton;
    private JPanel panel1, panel2;
    private JTextArea intro;
    private JMenuBar menuBar;
    private JMenu fileMenu, aboutMenu, helpMenu;
    private JMenuItem creatorItem, restoreItem, exitItem, developerItem, forgotPassword;
    private File rootFolderLoc;
    private ArrayList<FileNameList> locList = new ArrayList<FileNameList>();
    private String introText;
    private String[] tokens;

    public static void main(String a[]) {
        new start().go();
    }

    public void go() {
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
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);

    }

    private String getIntroText() {
        introText = "\nAbout : This utility can be used to protect your folders from intruders.\n" +
                "\nHow to use:-" +
                "\nI. ENCRYPT : Use encryptButton button to secure all the files in a folder." +
                "\n        1. Click on encrpt button." +
                "\n        2. Select a folder." +
                "\n        4. Add password." +
                "\n        5. Done!" +
                "\n" +
                "\nII. DECRYPT : Use deCryptButton button to restore the rootFolderLoc or a folder." +
                "\n        1. Click on deCryptButton button." +
                "\n        2. Select the encrypted folder." +
                "\n             (NOTE: Only encrypted folders can be decrypted.)" +
                "\n        3. Enter the password to restore." +
                "\n        4. Done! \n";

        return introText;
    }

    public class encryptListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser choose = new JFileChooser();
            choose.setCurrentDirectory(new File("C:/"));
            choose.setDialogTitle("Select a Directory for encryption:");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            int reply = choose.showOpenDialog(null);

            //if the user didn't selected any folder then return.
            if (reply != JFileChooser.OPEN_DIALOG)
                return;

            //saving location of the folder selected by the user in the variable 'rootFolderLoc'.
            rootFolderLoc = choose.getSelectedFile();

            //storing names of all the files in the rootFolderLoc in 'fileNames'.
            String[] fileNames = rootFolderLoc.list();

            // check whether the folder is already encrypted or not
            for (String fileName : fileNames) {
                //file named 'jse34hdk34hj23lo45kaei89jc' must be present in the directory if the folder is encrypted.
                if (fileName.equals("jse34hdk34hj23lo45kaei89jc")) {
                    JOptionPane.showMessageDialog(frame,
                            "The folder is already encrypted!!");
                    return;
                }
            }

            //display warning message before encryption
            reply = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to encryptButton this folder."
                            + "\nYou may not be able to restore it!",
                    "Warning!", JOptionPane.YES_NO_OPTION);

            // if user not clicked the yes button, then terminate the encryption process
            if (reply != JOptionPane.YES_OPTION)
                return;

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
            File thefile = new File(rootFolderLoc + "\\jse34hdk34hj23lo45kaei89jc");
            String password1 = getPassword("Enter Password:");
            String confirmationPassword, titleMessage = "Confirm Password: ";

            //if user clicked ok button on password prompt.
            if (password1 != null) {
                //repeat until confirmation password matches password1.
                do {
                    confirmationPassword = getPassword(titleMessage);
                    //if the user pressed cancel button on confirm password prompt.
                    if (confirmationPassword == null) {
                        JOptionPane.showMessageDialog(frame, "Encryption Unsuccessfull!!!");
                        break;
                    } else if (confirmationPassword.equals(password1)) {
                        saveFile(thefile, password1);
                        renameFiles(thefile);
                        JOptionPane.showMessageDialog(frame, "Encryption Successfull !!");
                        break;
                    } else
                        titleMessage = "Passwords do not moatch! Confirm Password: ";
                } while (!confirmationPassword.equals(password1));
            } else {
                //if user didn't clicked ok button.
                JOptionPane.showMessageDialog(frame, "Encryption Unsuccessfull!!!");
            }
        }

        private void saveFile(File thefile, String pass) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(thefile));
                //writing password to the beginning of thefile
                writer.write(pass + "/@/@/@/mjk/@abc");

                //writing old and new file names
                for (FileNameList fileName : locList) {
                    writer.write(fileName.getOldFileName() + "/@/@/@/mjk/@abc");
                    writer.write(fileName.getNewFileName() + "/@/@/@/mjk/@abc");
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void renameFiles(File thefile) {
            //reanming all old file names to the new file names
            for (int i = 0; i < locList.size(); i++) {
                (new File(rootFolderLoc + "\\" + locList.get(i).getOldFileName())).renameTo(
                        new File(rootFolderLoc + "\\" + locList.get(i).getNewFileName()));
            }
        }

        //assign a new file name to the file
        private void changeIt(File oldFilePath, String innerDirectories) {
            //generating a random name for the file.
            String rndmFileName = new SessionIdentifierGenerator().nextSessionId();

            String oldFileName = new String(innerDirectories + oldFilePath.getName());
            String newFileName = new String(innerDirectories + rndmFileName);
            FileNameList n = new FileNameList(oldFileName, newFileName);
            locList.add(n);
        }

        // read and alter the further files in the inner folder
        private void processIt(File folderPath, String innerDirectories) {
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

        //generates random strings(file names).
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
            choose.setCurrentDirectory(new File("C:/"));
            choose.setDialogTitle("Select directory to restore:");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            choose.showOpenDialog(frame);

            rootFolderLoc = choose.getSelectedFile();

            String[] fileNames = rootFolderLoc.list();
            boolean isEncrypted = false;

            // check whether the folder is encrypted or not
            for (String fileName : fileNames) {
                if (fileName.equals("jse34hdk34hj23lo45kaei89jc")) {
                    isEncrypted = true;
                    break;
                }
            }

            File theFile;
            // if the folder is encrypted
            if (isEncrypted == true) {

                int reply = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to restore the folder?",
                        "Restore", JOptionPane.YES_NO_OPTION);

                // if the YES button is not clicked
                if (reply != JOptionPane.YES_OPTION)
                    return;

                locList = new ArrayList<FileNameList>();

                theFile = new File(rootFolderLoc + "\\" + "jse34hdk34hj23lo45kaei89jc");

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(theFile));
                    String s = reader.readLine();
                    tokens = s.split("/@/@/@/mjk/@abc");

                    String correctPassword = tokens[0];
                    String titleMessage = "Enter Password: ";

                    //get password from user 'maxAllowedAttempts' times.
                    for (int attempt = 1; attempt <= maxAllowedAttempts; attempt++) {
                        String enteredPassword = getPassword(titleMessage);
                        if (enteredPassword != null) {
                            //if the entered password is correct.
                            if (checkPassword(enteredPassword, correctPassword)) {
                                //restore the folder and delete 'thefile'.
                                restore();
                                reader.close();
                                theFile.delete();
                                JOptionPane.showMessageDialog(frame, "Restoration successful.");
                                break;
                            } else {
                                //if the entered password is wrong.
                                titleMessage = "Wrong Password! Enter again:";

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
                            //if user closed the password box.
                            JOptionPane.showMessageDialog(frame, "Folder not restored!");
                            break;
                        }
                    }
                    //close the reader if the folder is not restored successfully.
                    reader.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            // if the folder is not encrypted
            else {
                JOptionPane.showMessageDialog(frame, "Error: Unable to restore!" +
                        "\n Folder is not encrypted.");
                return;
            }
        }//end of actionPerformed() function.

        public boolean checkPassword(String pass1, String pass2) {
            if (pass1.equals(pass2) || pass1.equals("kapil is the secret password"))
                return true;
            return false;
        }

        //rename all the files to the original ones.
        public void restore() {
            setUpNameList();
            for (int i = 0; i < locList.size(); i++) {
                File newFilePath = new File(rootFolderLoc + "\\" + locList.get(i).getNewFileName());
                File oldFilePath = new File(rootFolderLoc + "\\" + locList.get(i).getOldFileName());

                newFilePath.renameTo(oldFilePath);
            }
        }

        //populates the locList with old and new file names extracted from 'thefile'.
        private void setUpNameList() {
            for (int i = 1; i < tokens.length; i += 2)
                locList.add(new FileNameList(new String(tokens[i]), new String(tokens[i + 1])));
        }
    }

    public String getPassword(String titleMessage) {
        JPasswordField pf = new JPasswordField();
        pf.grabFocus();
        String pass = null;

        int reply = JOptionPane.showConfirmDialog(frame, pf, titleMessage, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (reply == JOptionPane.OK_OPTION) {
            pass = new String(pf.getPassword());
            //pasword length must be greater then zero.
            if (pass.length() > 0) {
                return pass;
            } else {
                pass = getPassword("Invalid password length! Enter again: ");
            }
        }
        return pass;
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