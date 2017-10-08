import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.util.Properties;

public class SmtpMail {

    private final String em = "file.protector.restoration@gmail.com";
    private final String p = "unhackable password";
    private final String host = "smtp.gmail.com";
    private final int port = 587;
    private String mRecipient = null;
    private boolean isSent = false;

    public SmtpMail(String recipient, String subject, String body) {
        mRecipient = recipient;
        Properties properties = setProperties();

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);

        Loader sendMailLoader = new Loader("Sending 6-digit code to your email-id...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                sendMail(message, session, subject, body);

                return null;
            }

            @Override
            protected void done() {
                sendMailLoader.hideLoader();
            }
        };

        worker.execute();
        sendMailLoader.showLoader();
    }

    private Properties setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.starttls.enable", true);
        //setting host and port of the email provider.
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        //overrides security checks (windows firewall).
        props.put("mail.smtp.ssl.trust", host);
        //setting em and p.
        props.put("mail.smtp.user", em);
        props.put("mail.smtp.p", p);

        props.put("mail.smtp.auth", true);

        return props;
    }

    private void sendMail(MimeMessage message, Session session, String subject, String body) {
        try {
            message.setFrom(new InternetAddress(em));
            InternetAddress recipientAddress = new InternetAddress(mRecipient);

            message.addRecipient(Message.RecipientType.TO, recipientAddress);
            message.setSubject(subject);
            message.setText(body);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, em, p);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            isSent = true;
            System.out.println("Mail sent successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Unable to send email!", "Something went wrong!", JOptionPane.PLAIN_MESSAGE);
            isSent = false;
            ex.printStackTrace();
        }
    }

    public boolean isMailSent() {
        return isSent;
    }
}
