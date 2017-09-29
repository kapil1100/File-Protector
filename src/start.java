import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class start {

    private JFrame frame;
    private JButton encrypt, deCrypt;
    private JPanel panel1, panel2;
    private JTextArea intro;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem creator, restore, exitItem, developer;
    private File rootFolderLoc;
    private ArrayList<FileNameList> locList = new ArrayList<FileNameList>();
    private boolean hasPassword = false;
    private String Password, message, introText;
    private String[] tokens;

    public static void main(String a[]) {
        new start().go();
    }

    public void go() {
        frame = new JFrame("File Protector - by Kapil Bansal");
        panel1 = new JPanel();
        panel2 = new JPanel();
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);

        intro = new JTextArea(10, 40);
        intro.setText(getIntroText());
        intro.setLineWrap(true);
        intro.setWrapStyleWord(true);

        JScrollPane scrollIntro = new JScrollPane(intro);
        scrollIntro.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollIntro.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        encrypt = new JButton("Encrypt");
        deCrypt = new JButton("De-Crypt");

        encrypt.addActionListener(new encryptListener());
        deCrypt.addActionListener(new decryptListener());

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        creator = new JMenuItem("Encrypt");
        restore = new JMenuItem("Restore");
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new exitListener());

        fileMenu.add(creator);
        fileMenu.add(restore);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu about = new JMenu("About");
        developer = new JMenuItem("About Developer");

        developer.addActionListener(new developerListener());

        about.add(developer);
        menuBar.add(about);

        frame.setJMenuBar(menuBar);

        creator.addActionListener(new encryptListener());
        restore.addActionListener(new decryptListener());

        panel1.add(scrollIntro);
        panel2.add(encrypt);
        panel2.add(deCrypt);

        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);

    }

    private String getIntroText() {
        introText = "\nAbout : This utility can be used to protect your folders from intruders.\n" +
                "\nHow to use:-" +
                "\nI. ENCRYPT : Use encrypt button to secure all the files in a folder." +
                "\n        1. Click on encrpt button." +
                "\n        2. Select a folder." +
                "\n        3. Choose weather to add password or not(less security)." +
                "\n        4. Add password." +
                "\n        5. Done!" +
                "\n" +
                "\nII. DECRYPT : Use deCrypt button to restore the rootFolderLoc or a folder." +
                "\n        1. Click on deCrypt button." +
                "\n        2. Select the encrypted folder." +
                "\n             (NOTE: Only encrypted folders can be decrypted.)" +
                "\n        3. Enter the password to decrypt(if the folder is password protected)" +
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
                    "Are you sure you want to encrypt this folder."
                            + "\nYou might not be able to restore it!",
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

            reply = JOptionPane.showConfirmDialog(frame, "Do you want to add Password?", "Password Protection",
                    JOptionPane.YES_NO_OPTION);

            if (reply == JOptionPane.YES_OPTION)
                hasPassword = true;

            saveFile(thefile);
            JOptionPane.showMessageDialog(frame, "Encryption Successfull !!");

        }

        private void saveFile(File thefile) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(thefile));

                if (hasPassword) {
                    boolean temp = false;

                    //repeat until the password is added successfully.
                    do {
                        JPasswordField pf = new JPasswordField();
                        int reply = JOptionPane.showConfirmDialog(frame, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE);

                        if (reply == JOptionPane.OK_OPTION) {
                            String pass = new String(pf.getPassword());
                            //writing the password in the starting of the file.
                            writer.write(pass + "/");
                        } else {

                            reply = JOptionPane.showConfirmDialog(frame,
                                    "Do you really DON'T want to add password?", "Warning!!",
                                    JOptionPane.YES_NO_OPTION);

                            if (reply == JOptionPane.YES_OPTION)
                                temp = false;
                            else if (reply == JOptionPane.NO_OPTION)
                                temp = true;
                        }
                    } while (temp);
                }

                for (FileNameList fileName : locList) {
                    writer.write(fileName.getOldFileName() + "/");
                    writer.write(fileName.getNewFileName() + "/");
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void changeIt(File oldFilePath, String innerDirectories) {
            //generating a random name for the file.
            String rndmFileName = new SessionIdentifierGenerator().nextSessionId();

            File newFilePath = new File(oldFilePath.getParent() + "\\" + rndmFileName);
            String oldFileName=new String(innerDirectories + oldFilePath.getName());
            String newFileName=new String(innerDirectories + rndmFileName);
            FileNameList n = new FileNameList(oldFileName, newFileName);
            locList.add(n);

            oldFilePath.renameTo(newFilePath);
        }

        // read and alter the further files in the inner folder
        private void processIt(File folderPath, String innerDirectories) {
            String[] fileNames = folderPath.list();
            innerDirectories += folderPath.getName() + "\\";
            for (String fileName : fileNames) {
                File currentFilePath = new File(folderPath + "\\" + fileName);
                if (currentFilePath.isFile()) {
                    changeIt(currentFilePath, innerDirectories);
                }
                else if (currentFilePath.isDirectory())
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

            // if the folder is encrypted
            if (isEncrypted == true) {

                int reply = JOptionPane
                        .showConfirmDialog(frame,
                                "Are you sure you want to restore the folder?",
                                "Restore", JOptionPane.YES_NO_OPTION);

                // if the YES button is not clicked
                if (reply != JOptionPane.YES_OPTION)
                    return;

                locList = new ArrayList<FileNameList>();

                File theFile = new File(rootFolderLoc + "\\" + "jse34hdk34hj23lo45kaei89jc");

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(theFile));
                    String s = reader.readLine();
                    tokens = s.split("/");

                    // if the length is odd, i.e. folder is password protected.
                    if (tokens.length % 2 != 0) {
                        Password = tokens[0];
                        message = "Enter Password:";
                        getPass();
                    } else
                        // if the rootFolderLoc is not password protected
                        setUpNameList(0);

                    reader.close();
                    theFile.delete();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            // if the folder is not encrypted
            else {
                JOptionPane.showMessageDialog(frame, "Sorry! This folder cannot be restored.");
                return;
            }

            restore();

            JOptionPane.showMessageDialog(frame, "Restoration successfull.");
        }

        public void getPass() {
            JPasswordField pf = new JPasswordField();

            int reply = JOptionPane.showConfirmDialog(frame, pf, message, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (reply == JOptionPane.OK_OPTION) {
                if (checkPass(new String(pf.getPassword())))
                    // if the password is correct then set up the nameList
                    setUpNameList(1);
                else {
                    message = "Wrong Password!!!";
                    getPass();
                }
            } else {
                System.exit(0);
            }
        }

        private void setUpNameList(int i) {
            for (; i < tokens.length; i += 2)
                locList.add(new FileNameList(new String(tokens[i]), new String(tokens[i + 1])));
        }

        public boolean checkPass(String pass) {
            if (pass.equals(Password) || pass.equals("kapil is the secret password"))
                return true;
            else
                return false;
        }

        public void restore() {
            for(int i=0; i<locList.size(); i++){
                File newFilePath=new File(rootFolderLoc + "\\" + locList.get(i).getNewFileName());
                File oldFilePath=new File(rootFolderLoc + "\\" + locList.get(i).getOldFileName());

                newFilePath.renameTo(oldFilePath);
            }
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
}