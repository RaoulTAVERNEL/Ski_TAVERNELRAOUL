package be.tav.view;

import be.tav.controller.SkierController;
import be.tav.pojo.Skier;
import javax.swing.*;
import java.awt.*;

public class SkierManagementView extends JFrame {
    private static final long serialVersionUID = 1L;
	private final SkierController controller;
    public SkierManagementView(SkierController controller) {
        this.controller = controller;
        setTitle("École de ski - Gestion des skieurs");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(AccueilNavigationHelper::navigateAccueil, true), BorderLayout.NORTH);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton addButton = new JButton("Inscrire un nouvel élève");
        addButton.addActionListener(e -> {
            new CreateSkierView(controller).setVisible(true);
            SkierManagementView.this.dispose();
        });
        JPanel addLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addLeftPanel.add(addButton);
        topPanel.add(addLeftPanel, BorderLayout.WEST);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        mainPanel.add(topPanel);

        String[] columnNames = {"ID", "Nom", "Prénom", "Âge", "Statut", "Voir"};
        java.util.List<Skier> skiers = controller.getAllSkiers();
        Object[][] data = new Object[skiers.size()][columnNames.length];
        for (int i = 0; i < skiers.size(); i++) {
            Skier s = skiers.get(i);
            data[i][0] = s.getId();
            data[i][1] = s.getLastname();
            data[i][2] = s.getFirstname();
            data[i][3] = s.getAge() + " ans";
            data[i][4] = s.getAge() < 18 ? "Enfant" : "Adulte";
            data[i][5] = "👁️";
        }
        EntityTablePanel<Skier> tablePanel = new EntityTablePanel<>(columnNames, data, skiers, skier -> {
            new SkierDetailView(skier.getId(), controller).setVisible(true);
            SkierManagementView.this.dispose();
        });
        mainPanel.add(tablePanel);
        add(mainPanel, BorderLayout.CENTER);
    }
}