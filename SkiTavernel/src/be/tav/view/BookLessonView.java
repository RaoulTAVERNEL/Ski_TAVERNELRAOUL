package be.tav.view;

import be.tav.pojo.Skier;
import be.tav.pojo.LessonType;
import be.tav.pojo.Lesson;
import be.tav.controller.SkierController;
import be.tav.controller.BookLessonController;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import be.tav.dao.DAOFactory;
import be.tav.pojo.Period;

public class BookLessonView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Skier skier;
    private JComboBox<LessonType> lessonTypeCombo;
    private JComboBox<String> dateCombo;
    private JComboBox<String> timeSlotCombo;
    private JCheckBox insuranceCheckBox;
    private JButton submitButton;
    private JLabel errorLabel;
    private JRadioButton collectifRadio;
    private JRadioButton individuelRadio;
    private ButtonGroup typeGroup;
    private JLabel totalPriceLabel;
    private JLabel priceLabel;
    private JLabel minMaxLabel;
    private final BookLessonController controller;
    private List<Period.WeekInfo> validWeeks;

    public BookLessonView(BookLessonController controller) {
        this.controller = controller;
        this.skier = controller.getSkier();
        setTitle("Réserver un cours pour " + skier.getLastname() + " " + skier.getFirstname());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(AccueilNavigationHelper::navigateAccueil, true), BorderLayout.NORTH);
        this.validWeeks = Period.getValidWeeks((be.tav.dao.PeriodDAO) new be.tav.dao.DAOFactory().getPeriodDAO());
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        mainPanel.add(new JLabel("Type de réservation :"), gbc);
        gbc.gridx = 1;
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        collectifRadio = new JRadioButton("Cours collectif");
        individuelRadio = new JRadioButton("Cours individuel");
        typeGroup = new ButtonGroup();
        typeGroup.add(collectifRadio);
        typeGroup.add(individuelRadio);
        typePanel.add(collectifRadio);
        typePanel.add(individuelRadio);
        mainPanel.add(typePanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel skierInfo = new JLabel(skier.getLastname() + " " + skier.getFirstname() + " (" + skier.getBirthdate() + ")");
        skierInfo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        mainPanel.add(skierInfo, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        mainPanel.add(new JLabel("Type de cours :"), gbc);
        gbc.gridx = 1;
        List<LessonType> lessonTypes = controller.getEligibleLessonTypes();
        lessonTypeCombo = new JComboBox<>(lessonTypes.toArray(new LessonType[0]));
        lessonTypeCombo.setMaximumSize(new Dimension(350, 32));
        lessonTypeCombo.setPreferredSize(new Dimension(350, 32));
        mainPanel.add(lessonTypeCombo, gbc);
        gbc.gridx = 2;
        priceLabel = new JLabel();
        priceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        priceLabel.setForeground(new Color(0, 120, 0));
        mainPanel.add(priceLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        minMaxLabel = new JLabel();
        minMaxLabel.setFont(new Font("Arial", Font.BOLD, 15));
        minMaxLabel.setForeground(new Color(30, 60, 150));
        minMaxLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridwidth = 3;
        mainPanel.add(minMaxLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        dateCombo = new JComboBox<>();
        dateCombo.setMaximumSize(new Dimension(350, 32));
        dateCombo.setPreferredSize(new Dimension(350, 32));
        mainPanel.add(new JLabel("Date du cours :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        mainPanel.add(new JLabel("Créneau horaire :"), gbc);
        gbc.gridx = 1;
        timeSlotCombo = new JComboBox<>();
        timeSlotCombo.setMaximumSize(new Dimension(350, 32));
        timeSlotCombo.setPreferredSize(new Dimension(350, 32));
        mainPanel.add(timeSlotCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        insuranceCheckBox = new JCheckBox("Assurance");
        gbc.gridwidth = 2;
        mainPanel.add(insuranceCheckBox, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        totalPriceLabel = new JLabel();
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        totalPriceLabel.setForeground(new Color(0, 120, 0));
        gbc.gridwidth = 2;
        mainPanel.add(totalPriceLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        gbc.gridwidth = 3;
        mainPanel.add(errorLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        submitButton = new JButton("Réserver");
        gbc.gridwidth = 2;
        mainPanel.add(submitButton, gbc);
        gbc.gridwidth = 1;

        add(mainPanel, BorderLayout.CENTER);
        
        collectifRadio.addActionListener(e -> {
            updateDateCombo();
            updateTimeSlots();
            updateTotalPrice();
        });
        individuelRadio.addActionListener(e -> {
            updateDateCombo();
            updateTimeSlots();
            updateTotalPrice();
        });
        lessonTypeCombo.addActionListener(e -> {
            updatePriceAndMinMax();
            updateTimeSlots();
            updateTotalPrice();
        });
        insuranceCheckBox.addActionListener(e -> updateTotalPrice());
        dateCombo.addActionListener(e -> {
            updateTimeSlots();
            updateTotalPrice();
        });
        submitButton.addActionListener(e -> handleSubmit());
        updatePriceAndMinMax();
        updateDateCombo();
        updateTimeSlots();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        boolean insurance = insuranceCheckBox.isSelected();
        boolean isIndiv = individuelRadio.isSelected();
        String timeSlot = (String) timeSlotCombo.getSelectedItem();
        float price;
        if (selectedType == null) {
            totalPriceLabel.setText("");
            return;
        }
        if (isIndiv) {
            if ("Déjeuner (1h, privé)".equals(timeSlot)) {
                price = 60f;
            } else if ("Déjeuner (2h, privé)".equals(timeSlot)) {
                price = 90f;
            } else {
                price = 60f;
            }
        } else {
            price = selectedType.getPrice();
            if (insurance) price += 10.0f;
        }
        totalPriceLabel.setText(String.format("Prix total : %.2f €", price));
    }

    private void handleSubmit() {
        errorLabel.setText("");
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        String selectedDateStr = (String) dateCombo.getSelectedItem();
        String timeSlot = (String) timeSlotCombo.getSelectedItem();
        boolean insurance = insuranceCheckBox.isSelected();
        boolean isCollectif = collectifRadio.isSelected();
        String validationError = controller.validateBooking(selectedType, selectedDateStr, timeSlot, insurance, isCollectif);
        if (validationError != null) {
            errorLabel.setText(validationError);
            return;
        }
        
        java.time.LocalDate localDate;
        try {
            if (isCollectif) {
                String startDateStr = selectedDateStr.split(" - ")[0];
                localDate = java.time.LocalDate.parse(startDateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else {
                localDate = java.time.LocalDate.parse(selectedDateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        } catch (Exception ex) {
            errorLabel.setText("Format de date invalide : " + selectedDateStr);
            return;
        }
        boolean isIndiv = individuelRadio.isSelected();
        java.util.List<Lesson> availableLessons = controller.getAvailableLessons(selectedType, localDate, timeSlot, isIndiv);
        if (availableLessons.isEmpty()) {
            errorLabel.setText("Aucun cours disponible pour ce créneau.");
            return;
        }
        String[] lessonLabels = availableLessons.stream().map(this::lessonDisplayString).toArray(String[]::new);
        int selectedIdx = JOptionPane.showOptionDialog(
                this,
                "Sélectionnez un cours disponible :",
                "Cours disponibles",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                lessonLabels,
                lessonLabels[0]);
        Lesson selectedLesson = (selectedIdx >= 0) ? availableLessons.get(selectedIdx) : null;
        if (selectedLesson == null) {
            errorLabel.setText("Aucun cours sélectionné.");
            return;
        }
        
        if (skier.hasBookingForLesson(selectedLesson)) {
            errorLabel.setText("Vous avez déjà une réservation pour ce cours.");
            return;
        }
        if (skier.hasBookingAtSlot(selectedLesson.getDate(), selectedType)) {
            errorLabel.setText("Vous avez déjà une réservation à ce créneau horaire pour ce type de cours.");
            return;
        }
        
        if (isIndiv && insurance) {
            errorLabel.setText("L'assurance n'est pas disponible pour les cours individuels.");
            return;
        }
        
        float totalPrice;
        if (isIndiv) {
            if ("Déjeuner (1h, privé)".equals(timeSlot)) {
                totalPrice = 60f;
            } else if ("Déjeuner (2h, privé)".equals(timeSlot)) {
                totalPrice = 90f;
            } else {
                totalPrice = 60f;
            }
        } else {
            totalPrice = selectedType.getPrice();
            if (insurance) totalPrice += 10.0f;
        }
        String summary = "Skieur : " + skier.getLastname() + " " + skier.getFirstname() +
                "\nCours : " + selectedType.getAccreditation().getName() + " - " + selectedType.getLevel() +
                "\nDate : " + localDate +
                "\nCréneau : " + timeSlot +
                "\nInstructeur : " + (selectedLesson.getInstructor() != null ? selectedLesson.getInstructor().getLastname() : "?") +
                "\nAssurance : " + (insurance ? "Oui" : "Non") +
                String.format("\nPrix total : %.2f €", totalPrice);
        int confirm = JOptionPane.showConfirmDialog(this, summary, "Confirmer la réservation", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) {
            errorLabel.setText("Réservation annulée.");
            return;
        }
        
        boolean bookingOk = controller.getSkierController().bookLesson(skier, selectedLesson, isCollectif, insurance);
        if (bookingOk) {
            JOptionPane.showMessageDialog(this, "Réservation confirmée !");
            
            DAOFactory factory = new DAOFactory();
            SkierController controller = new SkierController(factory);
            SkierManagementView skierManagementView = new SkierManagementView(controller);
            skierManagementView.setVisible(true);
            this.dispose();
        } else {
            errorLabel.setText("Erreur lors de la réservation. Veuillez réessayer.");
        }
    }

    private String lessonDisplayString(Lesson lesson) {
        String type = lesson.getLessonType() != null ? lesson.getLessonType().toString() : "?";
        String date = lesson.getDate() != null ? lesson.getDate().toLocalDate().toString() : "?";
        String heure = lesson.getDate() != null ? lesson.getDate().toLocalTime().toString() : "?";
        String instructeur = lesson.getInstructor() != null ? lesson.getInstructor().getLastname() : "?";
        return "#" + lesson.getId() + " - " + type + " - " + date + " " + heure + " - " + instructeur;
    }

    private void updateDateCombo() {
        dateCombo.removeAllItems();
        if (collectifRadio.isSelected()) {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Period.WeekInfo week : validWeeks) {
                String label = week.start.format(fmt) + " - " + week.end.format(fmt);
                dateCombo.addItem(label);
            }
        } else {
            for (Period.WeekInfo week : validWeeks) {
                java.time.LocalDate d = week.start;
                while (!d.isAfter(week.end)) {
                    dateCombo.addItem(d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    d = d.plusDays(1);
                }
            }
        }
    }

    private void updatePriceAndMinMax() {
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        boolean isIndiv = individuelRadio.isSelected();
        String timeSlot = (String) (timeSlotCombo != null ? timeSlotCombo.getSelectedItem() : null);
        if (selectedType != null) {
            String accName = selectedType.getAccreditation() != null ? selectedType.getAccreditation().getName().toLowerCase() : "";
            if (isIndiv || (timeSlot != null && (timeSlot.contains("privé") || timeSlot.contains("individuel")))) {
                minMaxLabel.setText("Cours individuel : 1 à 4 participants");
            } else if (accName.contains("enfant") || accName.contains("snowboard")) {
                minMaxLabel.setText("Cours collectif : 5 à 8 participants");
            } else if (accName.contains("adulte")) {
                minMaxLabel.setText("Cours collectif : 6 à 10 participants");
            } else if (accName.contains("compétition") || accName.contains("hors-piste")) {
                minMaxLabel.setText("Cours collectif : 5 à 8 participants");
            } else {
                minMaxLabel.setText("");
            }
            if (isIndiv) {
                int price = 0;
                if ("Déjeuner (1h, privé)".equals(timeSlot)) {
                    price = 60;
                } else if ("Déjeuner (2h, privé)".equals(timeSlot)) {
                    price = 90;
                } else {
                    price = 60;
                }
                priceLabel.setText(String.format("%d €", price));
                insuranceCheckBox.setVisible(false);
                insuranceCheckBox.setSelected(false);
            } else {
                priceLabel.setText(String.format("%.2f €", selectedType.getPrice()));
                insuranceCheckBox.setVisible(true);
            }
        } else {
            priceLabel.setText("");
            minMaxLabel.setText("");
            insuranceCheckBox.setVisible(false);
            insuranceCheckBox.setSelected(false);
        }
    }

    private void updateTimeSlots() {
        timeSlotCombo.removeAllItems();
        boolean isIndiv = individuelRadio.isSelected();
        if (isIndiv) {
            timeSlotCombo.addItem("Déjeuner (1h, privé)");
            timeSlotCombo.addItem("Déjeuner (2h, privé)");
        } else {
            timeSlotCombo.addItem("Matin");
            timeSlotCombo.addItem("Après-midi");
        }
        
        updatePriceAndMinMax();
    }
}