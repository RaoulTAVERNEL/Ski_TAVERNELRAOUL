package be.tav.view;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractDetailView extends JFrame {
    private static final long serialVersionUID = 1L;
	protected AbstractDetailView(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(e -> {
            AccueilView accueilView = new AccueilView();
            accueilView.setVisible(true);
            this.dispose();
        }, true), BorderLayout.NORTH);
    }
	
    protected abstract void initComponents();
}
