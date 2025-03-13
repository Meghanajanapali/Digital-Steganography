// import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class ComposePage extends JFrame implements ActionListener {
    private JLabel codeLabel, secretLabel, pictureLabel;
    private JTextField codeText, secretText, pictureText;
    private JButton pictureLoadButton, hideButton, homeButton;
    private BufferedImage image;
    private String filePath = "";

    ComposePage() {
        super("Compose");
        setLayout(null);

        codeLabel = new JLabel("Security Code:");
        codeLabel.setBounds(230, 100, 150, 50);
        codeText = new JTextField();
        codeText.setBounds(400, 100, 250, 40);

        secretLabel = new JLabel("Secret Information:");
        secretLabel.setBounds(230, 200, 150, 50);
        secretText = new JTextField();
        secretText.setBounds(400, 200, 250, 40);

        pictureLabel = new JLabel("Picture:");
        pictureLabel.setBounds(230, 300, 250, 40);
        pictureText = new JTextField();
        pictureText.setBounds(400, 300, 250, 50);
        pictureText.setEditable(false);

        pictureLoadButton = new JButton("Load");
        pictureLoadButton.setBounds(700, 300, 150, 30);
        pictureLoadButton.addActionListener(this);

        hideButton = new JButton("Hide");
        hideButton.setBounds(400, 400, 150, 30);
        hideButton.addActionListener(this);

        homeButton = new JButton("Home");
        homeButton.setBounds(700, 400, 150, 30);
        homeButton.addActionListener(this);

        add(codeLabel);
        add(codeText);
        add(secretLabel);
        add(secretText);
        add(pictureLabel);
        add(pictureText);
        add(pictureLoadButton);
        add(hideButton);
        add(homeButton);
        
        setSize(1035, 740);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == pictureLoadButton) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePath = selectedFile.getAbsolutePath();
                pictureText.setText(filePath);

                try {
                    image = ImageIO.read(selectedFile);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error loading image.");
                }
            }
        } else if (ae.getSource() == hideButton) {
            String securityCode = codeText.getText();
            String secretMessage = secretText.getText();

            if (filePath.isEmpty() || secretMessage.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an image and enter secret information.");
                return;
            }

            if (securityCode.contains("*")) {
                JOptionPane.showMessageDialog(this, "Security code cannot contain '*'.");
                return;
            }

            String secretData = securityCode + "*" + secretMessage.length() + "*" + secretMessage + "*";
            hideSecretMessage(image, secretData);

            JOptionPane.showMessageDialog(this, "Secret message hidden successfully!");
        } else if (ae.getSource() == homeButton) {
            dispose();
            new Home().setVisible(true);
        }
    }

    private void hideSecretMessage(BufferedImage img, String message) {
        byte[] messageBytes = message.getBytes();
        int msgIndex = 0;
        int totalPixels = img.getWidth() * img.getHeight();

        if (messageBytes.length * 8 > totalPixels) {
            JOptionPane.showMessageDialog(this, "Message too large for this image.");
            return;
        }

        int messageBitIndex = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int pixel = img.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                if (msgIndex < messageBytes.length) {
                    int bit = (messageBytes[msgIndex] >> (7 - messageBitIndex)) & 1;
                    blue = (blue & 0xFE) | bit;
                    messageBitIndex++;

                    if (messageBitIndex == 8) {
                        messageBitIndex = 0;
                        msgIndex++;
                    }
                }

                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                img.setRGB(x, y, newPixel);
            }
        }

        try {
            File output = new File("hidden_message.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving image.");
        }
    }

    public static void main(String[] args) {
        new ComposePage();
    }
}
