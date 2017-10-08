import javax.swing.*;
import java.util.Random;

public class RestorationCodeManager {

    public void sendRestorationCodeToUserEmail(int restorationCode, String emailId) {
        String subject = "File Protector's restoration code : ";
        String code = String.valueOf(restorationCode);
        new SmtpMail(emailId, subject, code);

        //TODO : check whether the code is sent successfully or not
    }

    public int generateRandomCode() {
        Random r = new Random();
        int rndmNumber = 100000 + (int) (r.nextFloat() * 899900);
        return rndmNumber;
    }

    public boolean verifyRestorationCode(String correctCode, String message, String titleMessage) {

        boolean result = false;
        JPanel panel = new JPanel();
        panel.add(new JLabel(message));
        JTextField textField = new JTextField(6);
        panel.add(textField);

        int reply = JOptionPane.showOptionDialog(null, panel,
                titleMessage, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null
        );

        if (reply == JOptionPane.OK_OPTION) {
            String enteredCode = textField.getText();

            System.out.print(enteredCode + "   " + correctCode);

            if (enteredCode.equals(correctCode)) {
                result = true;
            } else {
                message = "Enter the code again:";
                titleMessage = "Wrong Code: ";
                result = verifyRestorationCode(correctCode, message, titleMessage);
            }
        }
        return result;
    }
}
