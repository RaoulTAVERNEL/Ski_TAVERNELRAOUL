package be.tav.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TopBannerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public TopBannerPanel(ActionListener homeAction, boolean homeEnabled) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel fixedButtonPanel = new JPanel(new BorderLayout());
        JButton homeButton = new JButton("Accueil");
        homeButton.setFocusPainted(false);
        homeButton.setBackground(new Color(66, 133, 244));
        homeButton.setForeground(Color.WHITE);
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeButton.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        homeButton.setIcon(UIManager.getIcon("FileView.homeIcon"));
        homeButton.setEnabled(homeEnabled);
        if (homeEnabled && homeAction != null) {
            homeButton.addActionListener(homeAction);
        }

        JButton closeButton = new JButton("Fermer l'application");
        closeButton.setFocusPainted(false);
        closeButton.setBackground(new Color(220, 53, 69));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
        closeButton.addActionListener(e -> System.exit(0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(homeButton);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(closeButton);
        fixedButtonPanel.add(leftPanel, BorderLayout.WEST);
        fixedButtonPanel.add(rightPanel, BorderLayout.EAST);
        fixedButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        add(fixedButtonPanel);

        BannerPanel bannerPanel = new BannerPanel();
        add(bannerPanel);
    }
}
