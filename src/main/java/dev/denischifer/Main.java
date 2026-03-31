package dev.denischifer;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dev.denischifer.ui.controller.MainController;
import dev.denischifer.util.GuiFactory;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("apple.awt.fullWindowContent", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        FlatMacDarkLaf.setup();
        setupFonts();

        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FunModAnalyzer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 750);
            frame.setMinimumSize(new Dimension(950, 680));
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(GuiFactory.BG_DARK);

            MainController controller = new MainController(frame);
            controller.startApp();

            frame.setVisible(true);
        });
    }

    private static void setupFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            InputStream interStream = Main.class.getResourceAsStream("/fonts/Inter-VariableFont_opsz,wght.ttf");
            if (interStream != null) {
                Font inter = Font.createFont(Font.TRUETYPE_FONT, interStream);
                ge.registerFont(inter);
                UIManager.put("defaultFont", inter.deriveFont(14f));
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