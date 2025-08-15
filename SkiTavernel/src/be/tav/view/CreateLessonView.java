package be.tav.view;

import be.tav.pojo.Instructor;
import be.tav.pojo.LessonType;
import be.tav.controller.InstructorController;
import be.tav.pojo.Lesson;
import be.tav.dao.DAOFactory;
import be.tav.dao.PeriodDAO;
import be.tav.pojo.Period;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CreateLessonView extends JFrame {
    private static final long serialVersionUID = 1L;
	private final Instructor instructor;
    private JComboBox<LessonType> lessonTypeCombo;
    private JComboBox<String> dateCombo;
    private JComboBox<String> timeSlotCombo;
    private JButton submitButton;
    private JLabel errorLabel;
    private PeriodDAO periodDAO;
    private JLabel minMaxLabel;
    private JLabel priceLabel;

    public CreateLessonView(Instructor instructor) {
        this.instructor = instructor;
        DAOFactory factory = new DAOFactory();
        new InstructorController(factory);
        this.periodDAO = (PeriodDAO) factory.getPeriodDAO();
        setTitle("Créer un cours pour " + instructor.getLastname() + " " + instructor.getFirstname());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
    }

    private List<LessonType> getInstructorLessonTypes() {
        java.util.Set<be.tav.pojo.Accreditation> accs = instructor.getAccreditations();
        java.util.Set<LessonType> types = new java.util.HashSet<>();
        for (be.tav.pojo.Accreditation acc : accs) {
            types.addAll(acc.getLessonTypes());
        }
        return new java.util.ArrayList<>(types);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel instructorInfo = new JLabel(instructor.getLastname() + " " + instructor.getFirstname());
        instructorInfo.setFont(new Font("Arial", Font.BOLD, 18));
        instructorInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instructorInfo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        List<LessonType> lessonTypes = getInstructorLessonTypes();
        lessonTypeCombo = new JComboBox<>(lessonTypes.toArray(new LessonType[0]));
        lessonTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(new JLabel("Type de cours :"));
        mainPanel.add(lessonTypeCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        minMaxLabel = new JLabel();
        minMaxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(minMaxLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        priceLabel = new JLabel();
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(priceLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        dateCombo = new JComboBox<>();
        dateCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(new JLabel("Date du cours :"));
        mainPanel.add(dateCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        updateDateCombo();

        String[] timeSlots = {"Matin", "Après-midi", "Déjeuner (1h, privé)", "Déjeuner (2h, privé)"};
        timeSlotCombo = new JComboBox<>(timeSlots);
        timeSlotCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(new JLabel("Créneau horaire :"));
        mainPanel.add(timeSlotCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(errorLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        submitButton = new JButton("Créer le cours");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> handleSubmit());
        mainPanel.add(submitButton);

        add(mainPanel, BorderLayout.CENTER);

        lessonTypeCombo.addActionListener(e -> { updateLessonTypeInfo(); updateDateCombo(); });
        updateLessonTypeInfo();
    }

    private void updateLessonTypeInfo() {
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        String timeSlot = (String) (timeSlotCombo != null ? timeSlotCombo.getSelectedItem() : null);
        if (selectedType != null) {
            String accName = selectedType.getAccreditation() != null ? selectedType.getAccreditation().getName().toLowerCase() : "";
            if (timeSlot != null && (timeSlot.contains("privé") || timeSlot.contains("individuel"))) {
                minMaxLabel.setText("Min: 1 / Max: 4 élèves (particulier)");
            } else if (accName.contains("enfant") || accName.contains("snowboard")) {
                minMaxLabel.setText("Min: 5 / Max: 8 élèves (collectif)");
            } else if (accName.contains("adulte")) {
                minMaxLabel.setText("Min: 6 / Max: 10 élèves (collectif)");
            } else if (accName.contains("compétition") || accName.contains("hors-piste")) {
                minMaxLabel.setText("Min: 5 / Max: 8 élèves (collectif)");
            } else {
                minMaxLabel.setText("");
            }
            priceLabel.setText(String.format("Prix : %.2f €", selectedType.getPrice()));
        } else {
            minMaxLabel.setText("");
            priceLabel.setText("");
        }
    }

    private void updateDateCombo() {
        dateCombo.removeAllItems();
        java.util.List<Period.WeekInfo> weeks = Period.getValidWeeks(periodDAO);
        for (Period.WeekInfo week : weeks) {
            java.time.LocalDate d = week.start;
            while (!d.isAfter(week.end)) {
                dateCombo.addItem(d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                d = d.plusDays(1);
            }
        }
    }

    private boolean isInstructorAccreditedFor(LessonType type) {
        for (be.tav.pojo.Accreditation acc : instructor.getAccreditations()) {
            if (acc.getLessonTypes().contains(type)) return true;
        }
        return false;
    }

    private boolean isInstructorAvailable(LocalDate date, String timeSlot) {
        for (be.tav.pojo.Lesson lesson : instructor.getLessons()) {
            LocalDate lessonDate = lesson.getDate().toLocalDate();
            String lessonSlot = getTimeSlotFromLesson(lesson);
            if (lessonDate.equals(date) && lessonSlot.equals(timeSlot)) {
                return false;
            }
        }
        return true;
    }

    private String getTimeSlotFromLesson(Lesson lesson) {
        java.time.LocalTime time = lesson.getDate().toLocalTime();
        if (time.isBefore(java.time.LocalTime.NOON)) return "Matin";
        if (time.isAfter(java.time.LocalTime.of(13, 0)) && time.isBefore(java.time.LocalTime.of(15, 0))) return "Après-midi";
        if (time.equals(java.time.LocalTime.NOON)) return "Déjeuner (1h, privé)";
        return "?";
    }

    private void handleSubmit() {
        errorLabel.setText("");
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        String selectedDateStr = (String) dateCombo.getSelectedItem();
        if (selectedType == null) {
            errorLabel.setText("Veuillez sélectionner un type de cours.");
            return;
        }
        if (selectedDateStr == null || selectedDateStr.isEmpty()) {
            errorLabel.setText("Veuillez sélectionner une date.");
            return;
        }
        java.time.LocalDate selectedDate;
        try {
            selectedDate = java.time.LocalDate.parse(selectedDateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ex) {
            errorLabel.setText("Format de date invalide : " + selectedDateStr);
            return;
        }
        String timeSlot = (String) timeSlotCombo.getSelectedItem();
        
        if (!isInstructorAccreditedFor(selectedType)) {
            errorLabel.setText("L'instructeur n'a pas l'accréditation pour ce type de cours.");
            return;
        }
        
        if (!isInstructorAvailable(selectedDate, timeSlot)) {
            errorLabel.setText("L'instructeur n'est pas disponible à cette date/créneau.");
            return;
        }
        
        java.time.LocalTime lessonTime;
        boolean isPrivate = timeSlot.contains("privé");
        int duration = 2; // default duration in hours
        if (timeSlot.equals("Matin")) lessonTime = java.time.LocalTime.of(9, 0);
        else if (timeSlot.equals("Après-midi")) lessonTime = java.time.LocalTime.of(14, 0);
        else if (timeSlot.equals("Déjeuner (1h, privé)")) { lessonTime = java.time.LocalTime.of(12, 0); duration = 1; }
        else if (timeSlot.equals("Déjeuner (2h, privé)")) { lessonTime = java.time.LocalTime.of(12, 0); duration = 2; }
        else lessonTime = java.time.LocalTime.of(9, 0);
        java.time.LocalDateTime lessonDateTime = java.time.LocalDateTime.of(selectedDate, lessonTime);
        Lesson lesson = new Lesson(0, lessonDateTime, isPrivate, duration, selectedType, instructor);
        try {
            instructor.addLesson(lesson);
            JOptionPane.showMessageDialog(this, "Cours créé avec succès !");
            InstructorManagementView view = new InstructorManagementView(new be.tav.controller.InstructorController(new be.tav.dao.DAOFactory()));
            view.setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            errorLabel.setText("Erreur lors de la création du cours : " + ex.getMessage());
        }
    }
}