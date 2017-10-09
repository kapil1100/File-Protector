import javax.swing.*;
import java.awt.*;

//creates a loader that will be displayed while background tasks are processing.
public class Loader extends JPanel {

    final JDialog loading = new JDialog();

    public Loader(String message) {

        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(new JLabel(message), BorderLayout.CENTER);
        loading.setUndecorated(true);
        loading.getContentPane().add(p1);
        loading.pack();
        loading.setLocationRelativeTo(null);
        loading.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loading.setModal(true);
    }

    public void showLoader() {
        loading.setVisible(true);
    }

    public void hideLoader() {
        loading.dispose();
    }
}