package dev.denischifer;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dev.denischifer.ui.controller.MainController;
import dev.denischifer.util.GuiFactory;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        FlatMacDarkLaf.setup();

        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("ProgressBar.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FunModAnalyzer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setMinimumSize(new Dimension(900, 650));
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(GuiFactory.BG_DARK);
            frame.setLayout(new BorderLayout());

            MainController controller = new MainController(frame);
            controller.startApp();

            frame.setVisible(true);
        });
    }
}