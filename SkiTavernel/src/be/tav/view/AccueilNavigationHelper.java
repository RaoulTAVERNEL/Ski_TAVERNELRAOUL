package be.tav.view;

import java.awt.event.ActionEvent;

public class AccueilNavigationHelper {
    public static void navigateAccueil(ActionEvent e) {
        AccueilView accueilView = new AccueilView();
        accueilView.setVisible(true);
        Object source = e.getSource();
        if (source instanceof java.awt.Component) {
            java.awt.Component comp = (java.awt.Component) source;
            javax.swing.SwingUtilities.getWindowAncestor(comp).dispose();
        }
    }
}
