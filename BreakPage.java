// import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class BreakPage extends JFrame implements ActionListener {
    private JLabel codeLabel, pictureLabel;
    private JTextField codeText, pictureText;
    private JButton pictureLoadButton, breakButton, homeButton;
    private BufferedImage image;
    private String filePath = "";

    BreakPage() {
        super("Break");
        setLayout(null);

        codeLabel = new JLabel("Security Code:");
        codeLabel.setBounds(230, 200, 150, 50);
        codeText = new JTextField();
        codeText.setBounds(400, 200, 250, 40);

        pictureLabel = new JLabel("Picture:");
        pictureLabel.setBounds(230, 300, 250, 40);
        pictureText = new JTextField();
        pictureText.setBounds(400, 300, 250, 50);
        pictureText.setEditable(false);

        pictureLoadButton = new JButton("Load");
        pictureLoadButton.setBounds(700, 300, 150, 30);
        pictureLoadButton.addActionListener(this);

        breakButton = new JButton("Break");
        breakButton.setBounds(400, 400, 150, 30);
        breakButton.addActionListener(this);

        homeButton = new JButton("Home");
        homeButton.setBounds(700, 400, 150, 30);
        homeButton.addActionListener(this);

        add(codeLabel);
        add(codeText);
        add(pictureLabel);
        add(pictureText);
        add(pictureLoadButton);
        add(breakButton);
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
        } else if (ae.getSource() == breakButton) {
            String securityCode = codeText.getText();

            if (filePath.isEmpty() || securityCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an image and enter the security code.");
                return;
            }

            String extractedMessage = extractHiddenMessage(image, securityCode);

            if (extractedMessage != null) {
                JOptionPane.showMessageDialog(this, "Secret Information: " + extractedMessage);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid security code or no hidden message found.");
            }
        } else if (ae.getSource() == homeButton) {
            dispose();
            new Home().setVisible(true);
        }
    }

    private String extractHiddenMessage(BufferedImage img, String securityCode) {
		int width = img.getWidth();
		int height = img.getHeight();
		int totalPixels = width * height;
	
		byte[] extractedData = new byte[totalPixels / 8];
		int byteIndex = 0;
		int bitIndex = 0;
		int currentByte = 0;
	
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = img.getRGB(x, y);
				int blue = pixel & 0xFF;
	
				// Extract least significant bit (LSB)
				currentByte = (currentByte << 1) | (blue & 1);
				bitIndex++;
	
				if (bitIndex == 8) {
					extractedData[byteIndex] = (byte) currentByte;
					
					// ✅ Stop reading when we encounter '*' twice (end of message)
					if (byteIndex > 0 && extractedData[byteIndex] == '*' && extractedData[byteIndex - 1] == '*') {
						break;
					}
	
					byteIndex++;
					bitIndex = 0;
					currentByte = 0;
				}
			}
		}
	
		// ✅ Convert byte array to string and remove extra '*'
		String hiddenMessage = new String(extractedData, 0, byteIndex).replace("*", "");
	
		// ✅ Check if the extracted message starts with the security code
		if (hiddenMessage.startsWith(securityCode)) {
			return hiddenMessage.substring(securityCode.length()); // Return only the secret message
		}
	
		return null; // Security code does not match
	}
	

    public static void main(String[] args) {
        new BreakPage();
    }
}
