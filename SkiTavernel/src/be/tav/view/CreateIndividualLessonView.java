package be.tav.view;

import be.tav.pojo.Instructor;
import be.tav.pojo.LessonType;
import be.tav.pojo.Lesson;
import be.tav.dao.DAOFactory;
import be.tav.dao.PeriodDAO;
import be.tav.pojo.Period;
import be.tav.connection.SkiConnection;
import be.tav.dao.LessonDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CreateIndividualLessonView extends JFrame {
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

    public CreateIndividualLessonView(Instructor instructor) {
        this.instructor = instructor;
        DAOFactory factory = new DAOFactory();
        this.periodDAO = (PeriodDAO) factory.getPeriodDAO();
        setTitle("Créer un cours individuel pour " + instructor.getLastname() + " " + instructor.getFirstname());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(AccueilNavigationHelper::navigateAccueil, true), BorderLayout.NORTH);
        initComponents();
    }

    private List<LessonType> getIndividualLessonTypes() {
        java.util.Set<be.tav.pojo.Accreditation> accs = instructor.getAccreditations();
        java.util.Set<LessonType> types = new java.util.HashSet<>();
        for (be.tav.pojo.Accreditation acc : accs) {
            for (LessonType lt : acc.getLessonTypes()) {
                types.add(lt);
            }
        }
        System.out.println("DEBUG: Found " + types.size() + " lesson types for instructor " + instructor.getLastname());
        return new java.util.ArrayList<>(types);
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

        JLabel instructorInfo = new JLabel(instructor.getLastname() + " " + instructor.getFirstname());
        instructorInfo.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(instructorInfo, gbc);
        gbc.gridy++;

        mainPanel.add(new JLabel("Type de cours :"), gbc);
        gbc.gridx = 1;
        List<LessonType> lessonTypes = getIndividualLessonTypes();
        lessonTypeCombo = new JComboBox<>(lessonTypes.toArray(new LessonType[0]));
        lessonTypeCombo.setMaximumSize(new Dimension(350, 32));
        lessonTypeCombo.setPreferredSize(new Dimension(350, 32));
        lessonTypeCombo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LessonType) {
                    LessonType lt = (LessonType) value;
                    label.setText(lt.getAccreditation().getName() + " - " + lt.getLevel());
                }
                return label;
            }
        });
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

        mainPanel.add(new JLabel("Date du cours :"), gbc);
        gbc.gridx = 1;
        dateCombo = new JComboBox<>();
        dateCombo.setMaximumSize(new Dimension(350, 32));
        dateCombo.setPreferredSize(new Dimension(350, 32));
        mainPanel.add(dateCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        updateDateCombo();

        mainPanel.add(new JLabel("Créneau horaire :"), gbc);
        gbc.gridx = 1;
        timeSlotCombo = new JComboBox<>(new String[]{"Temps de midi (1h, privé)", "Temps de midi (2h, privé)"});
        timeSlotCombo.setMaximumSize(new Dimension(350, 32));
        timeSlotCombo.setPreferredSize(new Dimension(350, 32));
        mainPanel.add(timeSlotCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.gridwidth = 3;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        mainPanel.add(errorLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        submitButton = new JButton("Créer le cours individuel");
        submitButton.addActionListener(e -> handleSubmit());
        mainPanel.add(submitButton, gbc);
        gbc.gridwidth = 1;

        add(mainPanel, BorderLayout.CENTER);

        lessonTypeCombo.addActionListener(e -> updateLessonTypeInfo());
        timeSlotCombo.addActionListener(e -> updateLessonTypeInfo());
        updateLessonTypeInfo();
    }

    private void updateLessonTypeInfo() {
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        String timeSlot = (String) (timeSlotCombo != null ? timeSlotCombo.getSelectedItem() : null);
        if (selectedType != null) {
            minMaxLabel.setText("Min: 1 / Max: 4 élèves (particulier)");
            float prix = 0;
            if (timeSlot != null && timeSlot.equals("Temps de midi (1h, privé)")) prix = 60f;
            else if (timeSlot != null && timeSlot.equals("Temps de midi (2h, privé)")) prix = 90f;
            else prix = selectedType.getPrice();
            priceLabel.setText(String.format("Prix : %.2f €", prix));
        } else {
            minMaxLabel.setText("");
            priceLabel.setText("");
        }
    }

    private void updateDateCombo() {
        dateCombo.removeAllItems();
        java.util.List<Period.WeekInfo> weeks = Period.getValidWeeks(periodDAO);
        for (Period.WeekInfo week : weeks) {
            LocalDate d = week.start;
            while (!d.isAfter(week.end)) {
                dateCombo.addItem(d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                d = d.plusDays(1);
            }
        }
    }

    private void handleSubmit() {
        errorLabel.setText("");
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        String selectedDateStr = (String) dateCombo.getSelectedItem();
        String timeSlot = (String) timeSlotCombo.getSelectedItem();
        if (selectedType == null) {
            errorLabel.setText("Veuillez sélectionner un type de cours.");
            return;
        }
        if (selectedDateStr == null || selectedDateStr.isEmpty()) {
            errorLabel.setText("Veuillez sélectionner une date.");
            return;
        }
        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(selectedDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ex) {
            errorLabel.setText("Format de date invalide : " + selectedDateStr);
            return;
        }
        float prix = 0;
        if (timeSlot.equals("Temps de midi (1h, privé)")) prix = 60f;
        else if (timeSlot.equals("Temps de midi (2h, privé)")) prix = 90f;
        else prix = selectedType.getPrice();
        java.time.LocalTime lessonTime;
        boolean isPrivate = true;
        int duration = 2;
        if (timeSlot.equals("Temps de midi (1h, privé)")) { lessonTime = java.time.LocalTime.of(12, 0); duration = 1; }
        else if (timeSlot.equals("Temps de midi (2h, privé)")) { lessonTime = java.time.LocalTime.of(12, 0); duration = 2; }
        else {
            errorLabel.setText("Créneau horaire invalide pour un cours individuel.");
            return;
        }
        java.time.LocalDateTime lessonDateTime = java.time.LocalDateTime.of(selectedDate, lessonTime);
        be.tav.dao.InstructorDAO instructorDAO = new be.tav.dao.InstructorDAO(SkiConnection.getInstance());
        be.tav.pojo.Instructor freshInstructor = instructorDAO.find(instructor.getId());
        if (freshInstructor != null && freshInstructor.getLessons() != null) {
            for (Lesson l : freshInstructor.getLessons()) {
                if (l.getDate() != null && l.getDate().toLocalDate().equals(selectedDate)
                    && l.getDate().toLocalTime().equals(lessonTime)) {
                    errorLabel.setText("Ce moniteur a déjà un cours à ce créneau ce jour-là.");
                    return;
                }
            }
        }
        Lesson lesson = new Lesson(0, lessonDateTime, isPrivate, duration, selectedType, instructor);
        try {
            LessonDAO lessonDAO = new LessonDAO(SkiConnection.getInstance());
            boolean created = lessonDAO.create(lesson);
            System.out.println("DEBUG: lessonDAO.create(lesson) returned: " + created);
            if (created) {
                instructor.addLesson(lesson);
                JOptionPane.showMessageDialog(this, String.format("Cours individuel créé avec succès !\nPrix : %.2f €", prix));
                new AccueilView().setVisible(true);
                this.dispose();
            } else {
                errorLabel.setText("Erreur lors de la création du cours.");
            }
        } catch (Exception ex) {
            errorLabel.setText("Erreur lors de la création du cours : " + ex.getMessage());
        }
    }
}