package be.tav.view;

import be.tav.controller.InstructorController;
import be.tav.pojo.Instructor;
import be.tav.pojo.Accreditation;
import javax.swing.*;
import java.awt.*;

public class InstructorManagementView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final InstructorController controller;

    public InstructorManagementView(InstructorController controller) {
        this.controller = controller;
        setTitle("École de ski - Gestion des moniteurs");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(AccueilNavigationHelper::navigateAccueil, true), BorderLayout.NORTH);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Nom", "Prénom", "Accréditations", "Détail"};
        java.util.List<Instructor> instructors = controller.getAllInstructors();
        Object[][] data = new Object[instructors.size()][columnNames.length];
        for (int i = 0; i < instructors.size(); i++) {
            Instructor inst = instructors.get(i);
            String accreditations = inst.getAccreditations().stream()
                .map(Accreditation::getName)
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));
            data[i][0] = inst.getId();
            data[i][1] = inst.getLastname();
            data[i][2] = inst.getFirstname();
            data[i][3] = accreditations;
            data[i][4] = "Détail";
        }
        EntityTablePanel<Instructor> tablePanel = new EntityTablePanel<>(columnNames, data, instructors, instructor -> {
            new InstructorDetailView(instructor.getId(), controller).setVisible(true);
            InstructorManagementView.this.dispose();
        });
        mainPanel.add(tablePanel);
        add(mainPanel, BorderLayout.CENTER);
    }
}