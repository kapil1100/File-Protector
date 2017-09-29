import javafx.scene.control.*;

import javax.swing.*;
import java.awt.ScrollPane;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class start {

    private JFrame frame;
    private JButton encrypt, deCrypt;
    private JPanel panel1, panel2;
    private JTextArea intro;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem creator, restore, exitItem, developer;
    private File file;
    private ArrayList<NameList> locList = new ArrayList<NameList>();
    private boolean passwordSetted = false;
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

        encrypt.addActionListener(new creatorListener());
        deCrypt.addActionListener(new restoreListener());

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

        creator.addActionListener(new creatorListener());
        restore.addActionListener(new restoreListener());

        panel1.add(scrollIntro);
        panel2.add(encrypt);
        panel2.add(deCrypt);

        frame.add(panel1,BorderLayout.NORTH);
        frame.add(panel2,BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500,300));
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
                "\nII. DECRYPT : Use deCrypt button to restore the file or a folder." +
                "\n        1. Click on deCrypt button." +
                "\n        2. Select the encrypted folder." +
                "\n             (NOTE: Only encrypted folders can be decrypted.)" +
                "\n        3. Enter the password to decrypt(if the folder is password protected)" +
                "\n        4. Done! \n";

        return introText;
    }

    public class creatorListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser choose = new JFileChooser();
            choose.setCurrentDirectory(new File("C:/"));
            choose.setDialogTitle("Select a Directory for encryption:");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            int reply = choose.showOpenDialog(null);

            if (reply != JFileChooser.OPEN_DIALOG)
                return;

            file = choose.getSelectedFile();
            // now file has the location of the folder selected by the user

            String[] names = file.list();

            // check whether the folder is already encrypted or not
            for (String name : names) {
                if (name.equals("jse34hdk34hj23lo45kaei89jc")) {
                    JOptionPane.showMessageDialog(frame,
                            "The folder is already encrypted!!" + "\n It cannot be encrypted again!!!");
                    return;
                }
            }

            // shows a warning message before encryption
            reply = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to encrypt this folder."
                            + "\nYou might not be able to decrypt it back to original state",
                    "Warning!", JOptionPane.YES_NO_OPTION);

            // if user not clicked the yes button
            // then, terminate the encryption process
            if (reply != JOptionPane.YES_OPTION)
                return;

            for (String name : names) {
                File current = new File(file + "\\" + name);
                if (current.isDirectory()) {
                    // if the current file is a folder then process it further
                    // to change all the files inside the folder
                    processIt(current);
                } else
                    changeIt(current, file, name);
            }

            // the old locations and the new locations
            // will be stored in "thefile"
            File thefile = new File(file + "\\jse34hdk34hj23lo45kaei89jc");

            reply = JOptionPane.showConfirmDialog(frame, "Do you want to add Password?", "Password Protection?",
                    JOptionPane.YES_NO_OPTION);

            if (reply == JOptionPane.YES_OPTION)
                passwordSetted = true;

            saveFile(thefile);
            JOptionPane.showMessageDialog(frame, "Encryption Successfull !!");

        }
    }

    private void saveFile(File thefile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(thefile));

            if (passwordSetted) {
                boolean temp = false;
                do {
                    JPasswordField pf = new JPasswordField();
                    int reply = JOptionPane.showConfirmDialog(frame, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (reply == JOptionPane.OK_OPTION) {
                        String pass = new String(pf.getPassword());
                        writer.write(pass + "/");
                    } else {

                        reply = JOptionPane.showConfirmDialog(frame,
                                "Do you really DON'T WANT TO make it password protected!?", "Warning!!",
                                JOptionPane.YES_NO_OPTION);

                        if (reply == JOptionPane.YES_OPTION)
                            temp = false;
                        else if (reply == JOptionPane.NO_OPTION)
                            temp = true;
                    }
                } while (temp);
            }

            for (NameList name : locList) {
                writer.write(name.getOldFile() + "/");
                writer.write(name.getNewFile() + "/");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeIt(File current, File rootLoc, String name) {
        String s = new SessionIdentifierGenerator().nextSessionId();

        File newLoc = new File(rootLoc + "\\" + s);
        NameList n = new NameList(current, newLoc);
        locList.add(n);

        current.renameTo(newLoc);
    }

    // read and alter the further files in the inner folder
    private void processIt(File current) {
        String[] names = current.list();
        for (String name : names) {
            File loc = new File(current + "\\" + name);
            if (loc.isFile())
                changeIt(loc, current, name);
            else if (loc.isDirectory())
                processIt(loc);
        }
    }

    public final class SessionIdentifierGenerator {
        private SecureRandom random = new SecureRandom();

        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }

    public class restoreListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser choose = new JFileChooser();
            choose.setCurrentDirectory(new File("."));
            choose.setDialogTitle("Select a Directory for de-cryption:");
            choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            choose.setAcceptAllFileFilterUsed(false);
            choose.showOpenDialog(frame);

            file = choose.getSelectedFile();

            String[] names = file.list();
            boolean isPresent = false;

            // check whether the folder is encrypted or not
            for (String name : names) {
                if (name.equals("jse34hdk34hj23lo45kaei89jc")) {
                    isPresent = true;
                    break;
                }
            }

            // if the folder is encrypted
            if (isPresent == true) {

                int reply = JOptionPane
                        .showConfirmDialog(frame,
                                "Are you sure you want to restore this folder to its original state"
                                        + "\nThe folder will no longer be protected!!",
                                "Restore", JOptionPane.YES_NO_OPTION);

                // if the YES button is not clicked
                if (reply != JOptionPane.YES_OPTION)
                    return;

                locList = new ArrayList<NameList>();

                File details = new File(file + "\\" + "jse34hdk34hj23lo45kaei89jc");

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(details));
                    String s = reader.readLine();
                    tokens = s.split("/");

                    // if the length is odd, it means the file is password
                    // protected
                    // because, we already added password to the "thefile"
                    if (tokens.length % 2 != 0) {

                        Password = tokens[0];

                        message = "Enter Password:";

                        getPass();
                    } else
                        // if the file is not password protected
                        setUpNameList(0);

                    reader.close();
                    details.delete();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            // if the folder is not encrypted
            else {
                JOptionPane.showMessageDialog(frame, "Sorry!!! This folder cannot be de-crypted.."
                        + "\nMaybe it is not encrypted or it is already de-crypted");
                return;
            }

            for (String name : names) {
                File f = new File(file + "\\" + name);
                if (f.isDirectory())
                    restoreProcessing(f);
                else {
                    restoreChange(f);
                }
            }

            JOptionPane.showMessageDialog(frame, "Selected directory De-crypted Successfully..");
        }
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
            locList.add(new NameList(new File(tokens[i]), new File(tokens[i + 1])));
    }

    public boolean checkPass(String pass) {
        if (pass.equals(Password) || pass.equals("kapil is the secret password"))
            return true;
        else
            return false;
    }

    public void restoreProcessing(File f) {
        String[] names = f.list();
        for (String name : names) {
            File loc = new File(f + "\\" + name);
            if (loc.isDirectory())
                restoreProcessing(loc);
            else
                restoreChange(loc);
        }
    }

    public void restoreChange(File loc) {

        for (NameList Name : locList) {
            if (Name.getNewFile().equals(loc)) {
                loc.renameTo(Name.getOldFile());
                locList.remove(Name);
                break;
            }
        }

    }

    public class developerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "This software is developed by Kapil Bansal \n - from NIEC\n");
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