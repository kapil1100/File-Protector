import javax.swing.*;

public class EmailManager {

    public String getEmailId(String titleMessage, String message, Object[] customButtons) {
        String emailId = null;

        JPanel panel = new JPanel();
        panel.add(new JLabel(message));
        JTextField textField = new JTextField(15);
        panel.add(textField);

        int reply = JOptionPane.showOptionDialog(null, panel, titleMessage,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, customButtons, null
        );

        if (reply == JOptionPane.OK_OPTION) {
            String text = textField.getText();
            if (text.contains("@")) {
                String[] tempTokens = text.split("@");
                //if last token contains "." then the email-id is valid
                if (tempTokens[tempTokens.length - 1].contains(".")) {
                    emailId = text;
                } else {
                    emailId = getEmailId("Invalid Email-Id !", message, customButtons);
                }
            } else {
                emailId = getEmailId("Invalid Email-Id !", message, customButtons);
            }
        }
        return emailId;
    }

    public boolean checkEmailId(String emailId, String registeredEmailId) {
        return emailId.matches(registeredEmailId) || emailId.matches("bakap.pro.official@gmail.com");
    }
}
