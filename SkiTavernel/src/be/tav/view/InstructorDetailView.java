package be.tav.view;

import be.tav.pojo.Instructor;
import be.tav.pojo.Accreditation;
import be.tav.pojo.LessonType;
import be.tav.pojo.Lesson;
import be.tav.controller.InstructorController;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InstructorDetailView extends AbstractDetailView {
    private static final long serialVersionUID = 1L;
    private final Instructor instructor;
    public InstructorDetailView(int instructorId, InstructorController controller) {
        super("Détails du moniteur", 1200, 800);
        this.instructor = controller.getInstructorById(instructorId);
        initComponents();
    }

    @Override
    protected void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel nameLabel = new JLabel(instructor.getLastname() + " " + instructor.getFirstname());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        JLabel emailLabel = new JLabel("Email : " + instructor.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailLabel);

        JLabel phoneLabel = new JLabel("Téléphone : " + instructor.getPhone());
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(phoneLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel accLabel = new JLabel("Accréditations :");
        accLabel.setFont(new Font("Arial", Font.BOLD, 18));
        accLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(accLabel);
        for (Accreditation a : instructor.getAccreditations()) {
            JLabel aLabel = new JLabel("- " + a.getName());
            aLabel.setFont(new Font("Arial", Font.PLAIN, 15));
            aLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(aLabel);
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel coursesLabel = new JLabel("Cours que le moniteur peut enseigner :");
        coursesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        coursesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(coursesLabel);
        
        List<String> lessonTypesList = new ArrayList<>();
        for (Accreditation a : instructor.getAccreditations()) {
            for (LessonType lt : a.getLessonTypes()) {
                lessonTypesList.add(a.getName() + " (" + lt.getLevel() + ")");
            }
        }
        StringBuilder htmlLessonTypes = new StringBuilder("<html><div style='width:600px; text-align:center;'>");
        htmlLessonTypes.append(String.join(", ", lessonTypesList));
        htmlLessonTypes.append("</div></html>");
        JLabel lessonTypesLabel = new JLabel(htmlLessonTypes.toString());
        lessonTypesLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        lessonTypesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lessonTypesLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel planningLabel = new JLabel("Planning :");
        planningLabel.setFont(new Font("Arial", Font.BOLD, 18));
        planningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(planningLabel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addCollective = new JButton("Créer un cours collectif");
        JButton addPrivate = new JButton("Créer un cours individuel");
        buttonPanel.add(addCollective);
        buttonPanel.add(addPrivate);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        List<Lesson> lessons = new ArrayList<>(instructor.getLessons());
        lessons.sort((l1, l2) -> l1.getDate().compareTo(l2.getDate()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        if (lessons.isEmpty()) {
            JLabel noLesson = new JLabel("Aucun cours donné ou prévu.");
            noLesson.setFont(new Font("Arial", Font.ITALIC, 15));
            noLesson.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noLesson);
        } else {
            String[] columns = {"Date", "Cours", "Niveau", "Type de cours"};
            Object[][] data = new Object[lessons.size()][4];
            int i = 0;
            for (Lesson l : lessons) {
                String date;
                if (!l.getIsPrivate()) {
                	
                    java.time.format.DateTimeFormatter frFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    java.time.LocalDateTime start = l.getDate();
                    java.time.LocalDate end = start.toLocalDate().plusDays(7);
                    date = start.format(frFormatter) + " - " + end.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } else {
                    date = l.getDate().format(formatter);
                }
                data[i][0] = date;
                data[i][1] = l.getLessonType().getAccreditation().getName();
                data[i][2] = l.getLessonType().getLevel();
                if (l.getIsPrivate()) {
                    data[i][3] = "Cours particulier (" + l.getDuration() + "h)";
                } else {
                    data[i][3] = "Cours collectif";
                }
                i++;
            }
            JTable planningTable = new JTable(data, columns);
            planningTable.setFont(new Font("Arial", Font.PLAIN, 14));
            planningTable.setRowHeight(26);
            planningTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            planningTable.setEnabled(false);
            planningTable.setShowGrid(true);
            planningTable.setFillsViewportHeight(true);
            JScrollPane planningScroll = new JScrollPane(planningTable);
            planningScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
            planningScroll.setPreferredSize(new Dimension(500, Math.min(200, 26 * (lessons.size() + 1))));
            mainPanel.add(planningScroll);
        }

        addCollective.addActionListener(e -> {
            new CreateCollectiveLessonView(instructor).setVisible(true);
            SwingUtilities.getWindowAncestor(addCollective).dispose();
        });
        addPrivate.addActionListener(e -> {
            new CreateIndividualLessonView(instructor).setVisible(true);
            SwingUtilities.getWindowAncestor(addPrivate).dispose();
        });

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}