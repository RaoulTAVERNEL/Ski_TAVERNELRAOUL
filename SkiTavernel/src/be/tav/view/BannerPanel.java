package be.tav.view;

import javax.swing.*;
import java.awt.*;

public class BannerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
	private JLabel imageLabel;
    private String imagePath;
    private ImageIcon originalIcon;

    public BannerPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 100));
        setBackground(Color.WHITE);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePath = "src/resources/banner.png";
        setBannerImage(imagePath);
        add(imageLabel, BorderLayout.CENTER);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeBannerImage();
            }
        });
    }

    public void setBannerImage(String relativePath) {
        imagePath = relativePath;
        originalIcon = new ImageIcon(imagePath);
        resizeBannerImage();
    }

    private void resizeBannerImage() {
        if (originalIcon != null && originalIcon.getIconWidth() > 0 && originalIcon.getIconHeight() > 0) {
            int width = getWidth() > 0 ? getWidth() : 1200;
            int height = getHeight() > 0 ? getHeight() : 100;
            Image img = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setIcon(null);
        }
    }
}