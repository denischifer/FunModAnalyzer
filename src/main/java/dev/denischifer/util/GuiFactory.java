package dev.denischifer.util;

import com.formdev.flatlaf.FlatClientProperties;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class GuiFactory {
    public static final Color BG_DARK = new Color(15, 15, 18);
    public static final Color PANEL_BG = new Color(22, 22, 26);
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);
    public static final Color TEXT_PRIMARY = new Color(244, 244, 245);
    public static final Color TEXT_GRAY = new Color(113, 113, 122);
    public static final Color BORDER_COLOR = new Color(39, 39, 42);

    public static final String MAIN_FONT = "Inter";
    public static final String MONO_FONT = "JetBrains Mono";

    public static @NotNull JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        return panel;
    }

    public static @NotNull JTextField createStyledField(@NotNull String text) {
        JTextField field = new JTextField(text);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Путь...");
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        field.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #121214; focusColor: #3b82f6; margin: 6,12,6,12; borderWidth: 1; borderColor: #27272a");

        field.setFont(new Font(MONO_FONT, Font.PLAIN, 13));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_BLUE);
        return field;
    }

    public static void applyModernScrollBar(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc: 999; thumbArc: 999; width: 6; track: #0f0f12; thumbInsets: 0,0,0,0; hoverThumbColor: #3b82f6");
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
                    ? "arc: 12; background: #3b82f6; foreground: #ffffff; hoverBackground: #2563eb; borderWidth: 0"
                    : "arc: 12; background: #27272a; foreground: #f4f4f5; hoverBackground: #3f3f46; borderWidth: 1; borderColor: #3f3f46";

            putClientProperty(FlatClientProperties.STYLE, style);

            Font base = UIManager.getFont("defaultFont");
            setFont(base != null ? base.deriveFont(Font.BOLD, 13f) : new Font(MAIN_FONT, Font.BOLD, 13));
        }
    }
}