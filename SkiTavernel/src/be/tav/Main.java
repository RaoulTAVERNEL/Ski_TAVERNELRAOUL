package be.tav;

import be.tav.view.AccueilView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AccueilView().setVisible(true);
        });
    }
}