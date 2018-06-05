package com.qa.file;

import com.qa.file.window.Window;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.EventQueue;

class Launcher {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Window frame = new Window();
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                SwingUtilities.updateComponentTreeUI(frame);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
