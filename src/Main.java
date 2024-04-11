import javax.swing.;
import java.awt.;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startGUI();
            }
        });
    }
    private static void startGUI() {
        JFrame frame = new JFrame("Sprint0");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        panel.setBackground(new Color(0,0,0));
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500); // change later depending on what we need to do

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Fully close the operation.
    }
}