package dev.denischifer;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dev.denischifer.ui.controller.AppController;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        configureEnvironment();

        FlatMacDarkLaf.setup();
        setupFonts();
        applyGlobalUIStyles();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FunModAnalyzer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1180, 800);
            frame.setMinimumSize(new Dimension(1000, 700));
            frame.setLocationRelativeTo(null);

            frame.getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICON, false);
            frame.getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);

            AppController app = new AppController(frame);
            app.init();

            frame.setVisible(true);
        });
    }

    private static void configureEnvironment() {
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("apple.awt.fullWindowContent", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");
    }

    private static void applyGlobalUIStyles() {
        UIManager.put("Button.arc", 16);
        UIManager.put("Component.arc", 16);
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("TextComponent.arc", 16);

        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", new Color(0, 0, 0, 0));

        UIManager.put("Table.selectionBackground", new Color(38, 38, 45));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(40, 40, 45));
        UIManager.put("TableHeader.bottomSeparatorColor", new Color(40, 40, 45));

        UIManager.put("Separator.foreground", new Color(40, 40, 45));
    }

    private static void setupFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            InputStream interStream = Main.class.getResourceAsStream("/fonts/Inter-VariableFont_opsz,wght.ttf");
            if (interStream != null) {
                Font inter = Font.createFont(Font.TRUETYPE_FONT, interStream);
                ge.registerFont(inter);

                Font defaultFont = inter.deriveFont(14f);
                UIManager.put("defaultFont", defaultFont);
                UIManager.put("Label.font", defaultFont);
                UIManager.put("Button.font", inter.deriveFont(Font.BOLD, 13f));
            }

            InputStream jbStream = Main.class.getResourceAsStream("/fonts/JetBrainsMono-VariableFont_wght.ttf");
            if (jbStream != null) {
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, jbStream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}