import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Home extends JFrame implements ActionListener {
    private JButton compose, breakmsg;

    Home() {
        super("Steganography");
        Container con = getContentPane();
        con.setLayout(null);

        // Compose Button
        compose = new JButton("Compose");
        compose.addActionListener(this);
        compose.setBounds(300, 350, 150, 50);

        // Break Button
        breakmsg = new JButton("Break");
        breakmsg.addActionListener(this);
        breakmsg.setBounds(550, 350, 150, 50);

        // Adding components
        con.add(compose);
        con.add(breakmsg);

        // Window Settings
        setSize(1035, 790);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false); // Prevent resizing
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == compose) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                ComposePage cp = new ComposePage();
                cp.setSize(1035, 790);
                cp.setVisible(true);
            });
        }

        if (ae.getSource() == breakmsg) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                BreakPage bp = new BreakPage();
                bp.setSize(1035, 790);
                bp.setVisible(true);
            });
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            Home h = new Home();
            h.setVisible(true);
        });
    }
}
