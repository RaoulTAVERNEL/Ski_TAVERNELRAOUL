package be.tav.view;

import javax.swing.*;
import java.awt.*;

public class AccueilView extends JFrame {
    private static final long serialVersionUID = 1L;

	public AccueilView() {
        setTitle("École de ski - Accueil");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        TopBannerPanel topBanner = new TopBannerPanel(null, false);
        add(topBanner, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Bienvenue sur l'application de l'école de ski du domaine de Châtelet !");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        centerPanel.add(welcomeLabel);

        JButton skierButton = new JButton("Gérer les skieurs (réserver un cours, inscrire un élève, consulter la liste des élèves)");
        skierButton.setFont(new Font("Arial", Font.BOLD, 20));
        skierButton.setBackground(new Color(66, 133, 244));
        skierButton.setForeground(Color.WHITE);
        skierButton.setFocusPainted(false);
        skierButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        skierButton.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        skierButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        skierButton.addActionListener(e -> {
            be.tav.dao.DAOFactory factory = new be.tav.dao.DAOFactory();
            be.tav.controller.SkierController controller = new be.tav.controller.SkierController(factory);
            SkierManagementView skierManagementView = new SkierManagementView(controller);
            skierManagementView.setVisible(true);
            this.dispose();
        });
        centerPanel.add(skierButton);

        JButton instructorButton = new JButton("Gérer les moniteurs (ajouter un cours, consulter la liste des moniteurs)");
        instructorButton.setFont(new Font("Arial", Font.BOLD, 20));
        instructorButton.setBackground(new Color(52, 168, 83));
        instructorButton.setForeground(Color.WHITE);
        instructorButton.setFocusPainted(false);
        instructorButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        instructorButton.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        instructorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructorButton.addActionListener(e -> {
            be.tav.dao.DAOFactory factory = new be.tav.dao.DAOFactory();
            be.tav.controller.InstructorController controller = new be.tav.controller.InstructorController(factory);
            InstructorManagementView instructorManagementView = new InstructorManagementView(controller);
            instructorManagementView.setVisible(true);
            this.dispose();
        });
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(instructorButton);

        JButton bookingButton = new JButton("Consulter les réservations");
        bookingButton.setFont(new Font("Arial", Font.BOLD, 20));
        bookingButton.setBackground(new Color(244, 180, 0));
        bookingButton.setForeground(Color.WHITE);
        bookingButton.setFocusPainted(false);
        bookingButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookingButton.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        bookingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookingButton.addActionListener(e -> {
            be.tav.dao.DAOFactory factory = new be.tav.dao.DAOFactory();
            BookingManagementView bookingManagementView = new BookingManagementView(factory);
            bookingManagementView.setVisible(true);
            this.dispose();
        });
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(bookingButton);

        add(centerPanel, BorderLayout.CENTER);
    }
}