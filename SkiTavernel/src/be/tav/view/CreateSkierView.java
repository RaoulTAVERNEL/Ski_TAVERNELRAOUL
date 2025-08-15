package be.tav.view;

import be.tav.controller.SkierController;
import be.tav.pojo.Skier;
import be.tav.exception.BusinessException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateSkierView extends JFrame {
    private static final long serialVersionUID = 1L;
	private final SkierController controller;

    public CreateSkierView(SkierController controller) {
        this.controller = controller;
        setTitle("Inscription d'un nouvel élève");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(AccueilNavigationHelper::navigateAccueil, true), BorderLayout.NORTH);
        initForm();
    }

    private void initForm() {
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new GridBagLayout());
        JPanel formPanel = new JPanel();
        formPanel.setPreferredSize(new Dimension(600, 380));
        formPanel.setMaximumSize(new Dimension(600, 380));
        formPanel.setLayout(new GridLayout(7, 2, 14, 16));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        JLabel lastnameLabel = new JLabel("Nom :");
        JTextField lastnameField = new JTextField();
        JLabel firstnameLabel = new JLabel("Prénom :");
        JTextField firstnameField = new JTextField();
        JLabel emailLabel = new JLabel("Email :");
        JTextField emailField = new JTextField();
        JLabel phoneLabel = new JLabel("Téléphone :");
        JTextField phoneField = new JTextField();
        JLabel birthdateLabel = new JLabel("Date de naissance :");
        JTextField birthdateField = new JTextField();
        JLabel birthdateFormatLabel = new JLabel("(AAAA-MM-JJ)");
        birthdateFormatLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        birthdateFormatLabel.setForeground(Color.GRAY);

        formPanel.add(lastnameLabel); formPanel.add(lastnameField);
        formPanel.add(firstnameLabel); formPanel.add(firstnameField);
        formPanel.add(emailLabel); formPanel.add(emailField);
        formPanel.add(phoneLabel); formPanel.add(phoneField);
        formPanel.add(birthdateLabel); formPanel.add(birthdateField);
        formPanel.add(new JLabel()); formPanel.add(birthdateFormatLabel);

        JButton submitButton = new JButton("Inscrire le nouvel élève");
        formPanel.add(new JLabel());
        formPanel.add(submitButton);

        outerPanel.add(formPanel, new GridBagConstraints());
        add(outerPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            String lastname = lastnameField.getText().trim();
            String firstname = firstnameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String birthdateStr = birthdateField.getText().trim();

            if (lastname.isEmpty() || firstname.isEmpty() || email.isEmpty() || phone.isEmpty() || birthdateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Format d'email invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate birthdate;
            try {
                birthdate = LocalDate.parse(birthdateStr);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Date de naissance invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age = java.time.Period.between(birthdate, LocalDate.now()).getYears();
            if (age <= 3) {
                JOptionPane.showMessageDialog(this, "L'élève doit avoir plus de 3 ans.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Skier skier = new Skier(lastname, firstname, email, phone, birthdate);
                boolean success = controller.createSkier(skier);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Élève inscrit avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    new SkierManagementView(controller).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException | BusinessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}