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

public class CreateCollectiveLessonView extends JFrame {
    private static final long serialVersionUID = 1L;
	private final Instructor instructor;
    private JComboBox<LessonType> lessonTypeCombo;
    private JComboBox<String> weekCombo;
    private JComboBox<String> timeSlotCombo;
    private JButton submitButton;
    private JLabel errorLabel;
    private PeriodDAO periodDAO;
    private JLabel minMaxLabel;
    private JLabel priceLabel;

    public CreateCollectiveLessonView(Instructor instructor) {
        this.instructor = instructor;
        DAOFactory factory = new DAOFactory();
        this.periodDAO = (PeriodDAO) factory.getPeriodDAO();
        setTitle("Créer un cours collectif pour " + instructor.getLastname() + " " + instructor.getFirstname());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        TopBannerPanel topBanner = new TopBannerPanel(e -> {
            new AccueilView().setVisible(true);
            this.dispose();
        }, true);
        add(topBanner, BorderLayout.NORTH);
    }

    private List<LessonType> getCollectiveLessonTypes() {
        java.util.Set<be.tav.pojo.Accreditation> accs = instructor.getAccreditations();
        java.util.Set<LessonType> types = new java.util.HashSet<>();
        for (be.tav.pojo.Accreditation acc : accs) {
            for (LessonType lt : acc.getLessonTypes()) {
                if (!lt.toString().toLowerCase().contains("privé") && !lt.toString().toLowerCase().contains("individuel")) {
                    types.add(lt);
                }
            }
        }
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
        List<LessonType> lessonTypes = getCollectiveLessonTypes();
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

        mainPanel.add(new JLabel("Semaine du cours :"), gbc);
        gbc.gridx = 1;
        weekCombo = new JComboBox<>();
        weekCombo.setMaximumSize(new Dimension(350, 32));
        weekCombo.setPreferredSize(new Dimension(350, 32));
        mainPanel.add(weekCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        mainPanel.add(new JLabel("Créneau horaire :"), gbc);
        gbc.gridx = 1;
        String[] timeSlots = {"Matin", "Après-midi"};
        timeSlotCombo = new JComboBox<>(timeSlots);
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
        submitButton = new JButton("Créer le cours collectif");
        submitButton.addActionListener(e -> handleSubmit());
        mainPanel.add(submitButton, gbc);
        gbc.gridwidth = 1;

        add(mainPanel, BorderLayout.CENTER);

        lessonTypeCombo.addActionListener(e -> {
            updateMinMaxLabel();
            updatePriceLabel();
        });
        updateMinMaxLabel();
        updatePriceLabel();
        updateWeekCombo();
    }

    private void updateMinMaxLabel() {
        LessonType selected = (LessonType) lessonTypeCombo.getSelectedItem();
        if (selected != null) {
            String accName = selected.getAccreditation() != null ? selected.getAccreditation().getName().toLowerCase() : "";
            String html;
            if (accName.contains("enfant") || accName.contains("snowboard")) {
                html = "<html>Nombre minimum d'élèves pour que le cours ait lieu : <b>5</b><br>Nombre maximum d'élèves autorisés : <b>8</b></html>";
            } else if (accName.contains("adulte")) {
                html = "<html>Nombre minimum d'élèves pour que le cours ait lieu : <b>6</b><br>Nombre maximum d'élèves autorisés : <b>10</b></html>";
            } else if (accName.contains("compétition") || accName.contains("hors-piste")) {
                html = "<html>Nombre minimum d'élèves pour que le cours ait lieu : <b>5</b><br>Nombre maximum d'élèves autorisés : <b>8</b></html>";
            } else {
                html = "";
            }
            minMaxLabel.setText(html);
        } else {
            minMaxLabel.setText("");
        }
    }

    private void updatePriceLabel() {
        LessonType selected = (LessonType) lessonTypeCombo.getSelectedItem();
        if (selected != null) {
            priceLabel.setText("Prix : " + String.format("%.2f €", selected.getPrice()));
        } else {
            priceLabel.setText("");
        }
    }

    private void updateWeekCombo() {
        weekCombo.removeAllItems();
        java.util.List<Period.WeekInfo> weeks = Period.getValidWeeks(periodDAO);
        for (Period.WeekInfo week : weeks) {
            weekCombo.addItem(week.toString());
        }
    }

    private void handleSubmit() {
        errorLabel.setText("");
        LessonType selectedType = (LessonType) lessonTypeCombo.getSelectedItem();
        String selectedWeekStr = (String) weekCombo.getSelectedItem();
        String timeSlot = (String) timeSlotCombo.getSelectedItem();
        if (selectedType == null) {
            errorLabel.setText("Veuillez sélectionner un type de cours.");
            return;
        }
        if (selectedWeekStr == null || selectedWeekStr.isEmpty()) {
            errorLabel.setText("Veuillez sélectionner une semaine.");
            return;
        }
        LocalDate startDate;
        try {
            String selectedWeekStrClean = selectedWeekStr;
            int idx1 = selectedWeekStrClean.indexOf('(');
            int idx2 = selectedWeekStrClean.indexOf(" au ");
            if (idx1 != -1 && idx2 != -1) {
                String dateStr = selectedWeekStrClean.substring(idx1 + 1, idx2).trim();
                startDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                throw new Exception("Format de semaine inattendu : " + selectedWeekStr);
            }
        } catch (Exception ex) {
            errorLabel.setText("Format de semaine invalide : " + selectedWeekStr);
            return;
        }
        java.time.LocalTime lessonTime = timeSlot.equals("Matin") ? java.time.LocalTime.of(9, 0) : java.time.LocalTime.of(14, 0);
        java.time.LocalDateTime lessonDateTime = java.time.LocalDateTime.of(startDate, lessonTime);
        
        be.tav.dao.InstructorDAO instructorDAO = new be.tav.dao.InstructorDAO(SkiConnection.getInstance());
        be.tav.pojo.Instructor freshInstructor = instructorDAO.find(instructor.getId());
        if (freshInstructor != null && freshInstructor.getLessons() != null) {
            for (Lesson l : freshInstructor.getLessons()) {
                if (l.getDate() != null && l.getDate().toLocalDate().equals(startDate)
                    && ((timeSlot.equals("Matin") && l.getDate().toLocalTime().equals(java.time.LocalTime.of(9, 0)))
                        || (timeSlot.equals("Après-midi") && l.getDate().toLocalTime().equals(java.time.LocalTime.of(14, 0))))) {
                    errorLabel.setText("Ce moniteur a déjà un cours à ce créneau ce jour-là.");
                    return;
                }
            }
        }
        Lesson lesson = new Lesson(0, lessonDateTime, false, 18, selectedType, instructor);
        try {
            LessonDAO lessonDAO = new LessonDAO(SkiConnection.getInstance());
            boolean created = lessonDAO.create(lesson);
            System.out.println("DEBUG: lessonDAO.create(lesson) returned: " + created);
            if (created) {
                instructor.addLesson(lesson);
                JOptionPane.showMessageDialog(this, "Cours collectif créé avec succès !");
                new AccueilView().setVisible(true);
                this.dispose();
            } else {
                errorLabel.setText("Erreur lors de l'insertion du cours en base de données.");
            }
        } catch (Exception ex) {
            errorLabel.setText("Erreur lors de la création du cours : " + ex.getMessage());
        }
    }
}