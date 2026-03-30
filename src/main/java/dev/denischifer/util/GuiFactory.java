package dev.denischifer.util;

import com.formdev.flatlaf.FlatClientProperties;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class GuiFactory {
    public static final Color BG_DARK = new Color(24, 24, 27);
    public static final Color PANEL_BG = new Color(32, 32, 35);
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);
    public static final Color TEXT_PRIMARY = new Color(244, 244, 245);
    public static final Color TEXT_GRAY = new Color(113, 113, 122);
    public static final Color BORDER_COLOR = new Color(39, 39, 42);

    public static final String MAIN_FONT = "Segoe UI Variable Text";
    public static final String MONO_FONT = "Consolas";

    public static @NotNull JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        return panel;
    }

    public static @NotNull JTextField createStyledField(@NotNull String text) {
        JTextField field = new JTextField(text);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Путь");
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #18181b; focusColor: #3b82f6");

        field.setFont(new Font(MAIN_FONT, Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    public static void applyModernScrollBar(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc: 999; thumbArc: 999; width: 8");
    }

    public static class RoundedPanel extends JPanel {
        public RoundedPanel(int radius, LayoutManager layout) {
            super(layout);
            putClientProperty(FlatClientProperties.STYLE, "arc: " + radius);
            setBackground(PANEL_BG);
        }
    }

    public static class RoundedButton extends JButton {
        public RoundedButton(String text, boolean primary) {
            super(text);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);

            String style = primary
                    ? "arc: 10; background: #3b82f6; foreground: #ffffff; hoverBackground: #2563eb; borderWidth: 0"
                    : "arc: 10; background: #27272a; foreground: #f4f4f5; hoverBackground: #3f3f46; borderWidth: 1; borderColor: #3f3f46";

            putClientProperty(FlatClientProperties.STYLE, style);
            setFont(new Font(MAIN_FONT, Font.BOLD, 13));
        }
    }
}