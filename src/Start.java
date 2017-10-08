import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class Start {

    private JFrame frame;
    private File rootFolderLoc;
    private ArrayList<FileNameList> fileNameList;

    private String introText;
    private String[] tokens;
    private String registeredEmailId = null;
    private BufferedReader reader;


    public static void main(String a[]) {
        new Start().go();
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

        //listeners
        EncryptListener encryptListener = new EncryptListener();
        DecryptListener decryptListener = new DecryptListener();

        //adding action listeners...
        encryptButton.addActionListener(encryptListener);
        deCryptButton.addActionListener(decryptListener);
        creatorItem.addActionListener(encryptListener);
        restoreItem.addActionListener(decryptListener);
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