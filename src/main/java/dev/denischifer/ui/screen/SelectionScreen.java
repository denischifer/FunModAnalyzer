package dev.denischifer.ui.screen;

import dev.denischifer.ui.controller.ScanController;
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
    private final ScanController controller;

    public SelectionScreen(@NotNull ScanController controller) {
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
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 42));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("DEVELOPED BY DENISCHIFER", SwingConstants.CENTER);
        sub.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 12));
        sub.setForeground(GuiFactory.ACCENT_BLUE);

        header.add(title, BorderLayout.CENTER);
        header.add(sub, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 70, 0);
        view.add(header, gbc);

        JPanel cards = new JPanel(new GridLayout(1, 2, 40, 0));
        cards.setOpaque(false);
        cards.add(createMemoryCard());
        cards.add(createDiskCard());

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        view.add(cards, gbc);

        JLabel footer = new JLabel("v1.0.0 // 2026", SwingConstants.CENTER);
        footer.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 10));
        footer.setForeground(GuiFactory.TEXT_GRAY.darker());
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        view.add(footer, gbc);
    }

    private JPanel createMemoryCard() {
        return createBaseCard(
                "ПАМЯТЬ",
                "Анализ запущенного процесса игры и модификаций.",
                null,
                "ВЫБРАТЬ ПРОЦЕСС",
                e -> controller.startMemoryScan()
        );
    }

    private JPanel createDiskCard() {
        String defaultPath = "C:/Games/FunTime/mods";
        JTextField pathInput = new JTextField(defaultPath);
        pathInput.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 12));
        pathInput.setForeground(GuiFactory.TEXT_GRAY);
        pathInput.setCaretColor(GuiFactory.ACCENT_BLUE);
        pathInput.setHorizontalAlignment(JTextField.CENTER);
        pathInput.setOpaque(false);
        pathInput.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(63, 63, 70)));

        pathInput.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (pathInput.getText().equals(defaultPath)) pathInput.setText("");
                pathInput.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GuiFactory.ACCENT_BLUE));
            }
            @Override public void focusLost(FocusEvent e) {
                if (pathInput.getText().isEmpty()) pathInput.setText(defaultPath);
                pathInput.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(63, 63, 70)));
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(15, 0, 15, 0));
        wrap.add(pathInput);

        return createBaseCard(
                "ДИСК",
                "Сканирование модификаций в указанной директории.",
                wrap,
                "НАЧАТЬ ПРОВЕРКУ",
                e -> controller.startDiskScan(pathInput.getText())
        );
    }

    private JPanel createBaseCard(String title, String desc, JComponent extra, String btnText, java.awt.event.ActionListener action) {
        GuiFactory.RoundedPanel card = new GuiFactory.RoundedPanel(20, new BorderLayout(0, 15));
        card.setPreferredSize(new Dimension(380, 280));
        card.setBackground(new Color(24, 24, 27));
        card.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 22));
        t.setForeground(Color.WHITE);

        JTextArea d = new JTextArea(desc);
        d.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 14));
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
        btn.setPreferredSize(new Dimension(0, 48));
        btn.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 12));
        btn.addActionListener(action);

        card.add(t, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(32, 32, 38));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(24, 24, 27));
            }
        });

        return card;
    }
}