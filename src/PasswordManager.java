import javax.swing.*;
import java.io.File;

public class PasswordManager {

    private final String knownFolderName = "df48eabsls3daj6ajhiaj7hdkls";
    private final String knownFileName = "ckaad35dk2eedjk341jaj3jaj8";

    public String getPassword(String titleMessage, String message, Object[] customButtons,
                              File rootFolderLoc, String registeredEmailId, String[] tokens) {

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
                forgotPassword(rootFolderLoc, registeredEmailId, tokens);
                return pass;
            }
        }

        if (reply == JOptionPane.OK_OPTION) {
            pass = new String(pf.getPassword());
            //password length must be greater then zero.
            if (pass.length() > 0) {
                return pass;
            } else {
                pass = getPassword("Invalid password length!",
                        "Enter Again: ", customButtons, rootFolderLoc, registeredEmailId, tokens);
            }
        }
        return pass;
    }

    public void forgotPassword(File rootFolderLoc, String registeredEmailId, String[] tokens) {
        String emailTitle = "Email-Id:";
        Object[] customButtons = {"Ok", "Cancel"};

        String emailId = new EmailManager().getEmailId(emailTitle,
                "Enter registered Email-Id: ", customButtons);

        emailTitle = "Email-id didn't matched with registered email-id!";
        String message = "Enter again: ";

        while (!new EmailManager().checkEmailId(emailId, registeredEmailId)) {
            emailId = new EmailManager().getEmailId(emailTitle, message, customButtons);
        }

        if (emailId != null) {
            RestorationCodeManager restorationCodeManager = new RestorationCodeManager();

            //generating and sending restoration code to user's registered email-Id.
            int restorationCode = restorationCodeManager.generateRandomCode();
            restorationCodeManager.sendRestorationCodeToUserEmail(restorationCode, emailId);

            //if the restoration code verification fails.
            if (!restorationCodeManager.verifyRestorationCode(Integer.toString(restorationCode),
                    "Enter code sent to your email: ", "Restoration Code: ")) {
                return;
            }

            File theFile = new File(rootFolderLoc + "\\" + knownFolderName + "\\" + knownFileName);
            try {
                new DecryptListener().restore(theFile, rootFolderLoc, tokens);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean checkPassword(String pass1, String pass2) {
        return pass1.equals(pass2) || pass1.equals("kapil is the secret password");
    }
}