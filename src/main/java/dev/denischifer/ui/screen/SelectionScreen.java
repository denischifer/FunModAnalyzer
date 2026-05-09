package dev.denischifer.ui.screen;

import dev.denischifer.ui.controller.ScanningController;
import dev.denischifer.util.GuiFactory;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectionScreen {
    @Getter private final JPanel view;
    private final ScanningController controller;

    public SelectionScreen(@NotNull ScanningController controller) {
        this.controller = controller;
        this.view = GuiFactory.createMainPanel();
        initLayout();
    }

    private void initLayout() {
        view.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("FUN MOD ANALYZER", SwingConstants.CENTER);
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 46));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("DEVELOPED BY DENISCHIFER", SwingConstants.CENTER);
        sub.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 13));
        sub.setForeground(GuiFactory.ACCENT_BLUE);

        header.add(title, BorderLayout.CENTER);
        header.add(sub, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 80, 0);
        view.add(header, gbc);

        JPanel cards = new JPanel(new GridLayout(1, 2, 45, 0));
        cards.setOpaque(false);
        cards.add(createMemoryCard());
        cards.add(createDiskCard());

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 50, 0);
        view.add(cards, gbc);

        JLabel footer = new JLabel("v1.0.0 // 2026", SwingConstants.CENTER);
        footer.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 11));
        footer.setForeground(new Color(100, 100, 110));
        gbc.gridy = 2;
        gbc.insets = new Insets(40, 0, 0, 0);
        view.add(footer, gbc);
    }

    private JPanel createMemoryCard() {
        return createBaseCard(
                "ПАМЯТЬ",
                "Анализ запущенного процесса игры и активных модификаций.",
                null,
                "ВЫБРАТЬ ПРОЦЕСС",
                e -> controller.prepareMemoryScan()
        );
    }

    private JPanel createDiskCard() {
        String defaultPath = "C:/Games/FunTime/mods";
        JTextField pathInput = new JTextField(defaultPath);
        pathInput.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 13));
        pathInput.setForeground(GuiFactory.TEXT_GRAY);
        pathInput.setCaretColor(GuiFactory.ACCENT_BLUE);
        pathInput.setHorizontalAlignment(JTextField.CENTER);
        pathInput.setOpaque(false);
        pathInput.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(50, 50, 55)));

        pathInput.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (pathInput.getText().equals(defaultPath)) pathInput.setText("");
                pathInput.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GuiFactory.ACCENT_BLUE));
                pathInput.setForeground(Color.WHITE);
            }
            @Override public void focusLost(FocusEvent e) {
                if (pathInput.getText().isEmpty()) {
                    pathInput.setText(defaultPath);
                    pathInput.setForeground(GuiFactory.TEXT_GRAY);
                }
                pathInput.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(50, 50, 55)));
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(20, 0, 20, 0));
        wrap.add(pathInput);

        return createBaseCard(
                "ДИСК",
                "Сканирование локальных модификаций в указанной директории.",
                wrap,
                "НАЧАТЬ ПРОВЕРКУ",
                e -> controller.startDiskScan(pathInput.getText())
        );
    }

    private JPanel createBaseCard(String title, String desc, JComponent extra, String btnText, java.awt.event.ActionListener action) {
        GuiFactory.RoundedPanel card = new GuiFactory.RoundedPanel(24, new BorderLayout(0, 20));
        card.setPreferredSize(new Dimension(390, 300));
        card.setBackground(new Color(24, 24, 27));
        card.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 24));
        t.setForeground(Color.WHITE);

        JTextArea d = new JTextArea(desc);
        d.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 15));
        d.setForeground(GuiFactory.TEXT_GRAY);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        d.setEditable(false);
        d.setFocusable(false);
        d.setOpaque(false);
        d.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(d, BorderLayout.CENTER);
        if (extra != null) center.add(extra, BorderLayout.SOUTH);

        JButton btn = new GuiFactory.RoundedButton(btnText, true);
        btn.setPreferredSize(new Dimension(0, 52));
        btn.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));
        btn.addActionListener(action);

        card.add(t, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(32, 32, 38));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(24, 24, 27));
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return card;
    }
}