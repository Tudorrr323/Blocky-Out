package com.blocky.main;

import com.blocky.logic.GameEngine;
import com.blocky.view.BoardPanel;

import javax.swing.*;

public class Blocky {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true"); // Hardware acceleration

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Blocky Out - Ultimate Edition");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Folosim BoardPanel care are dimensiunile logice setate
            BoardPanel panel = new BoardPanel();
            frame.add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);

            // Game Loop (~60 FPS)
            Timer timer = new Timer(16, e -> {
                GameEngine.getInstance().update();
                panel.repaint();
            });
            timer.start();
        });
    }
}