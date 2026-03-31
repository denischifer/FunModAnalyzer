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

        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        GridBagConstraints hGbc = new GridBagConstraints();

        JLabel title = new JLabel("FUN MOD ANALYZER");
        title.setFont(new Font("Impact", Font.PLAIN, 64));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("dev by denischifer");
        sub.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 12));
        sub.setForeground(GuiFactory.ACCENT_BLUE);
        sub.setBorder(new EmptyBorder(10, 0, 0, 0));

        hGbc.gridy = 0;
        header.add(title, hGbc);
        hGbc.gridy = 1;
        header.add(sub, hGbc);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 80, 0);
        view.add(header, gbc);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        cardsPanel.setOpaque(false);

        cardsPanel.add(createMemoryCard());
        cardsPanel.add(createDiskCard());

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        view.add(cardsPanel, gbc);

        JLabel footer = new JLabel("v1.0.0 // denischifer");
        footer.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 11));
        footer.setForeground(GuiFactory.TEXT_GRAY.darker());
        gbc.gridy = 2;
        gbc.insets = new Insets(40, 0, 0, 0);
        view.add(footer, gbc);
    }

    private JPanel createMemoryCard() {
        return createBaseCard(
                "СКАНИРОВАНИЕ ПАМЯТИ",
                "Проверка и анализ файлов в выбранном процессе игры.",
                null,
                "ВЫБРАТЬ ПРОЦЕСС",
                e -> controller.startMemoryScan()
        );
    }

    private JPanel createDiskCard() {
        String defaultPath = "C:/Games/FunTime/mods";
        JTextField pathInput = GuiFactory.createStyledField(defaultPath);
        pathInput.setPreferredSize(new Dimension(320, 42));
        pathInput.setForeground(GuiFactory.TEXT_GRAY);
        pathInput.setHorizontalAlignment(JTextField.CENTER);
        pathInput.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 13));

        pathInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (pathInput.getText().equals(defaultPath)) {
                    pathInput.setText("");
                    pathInput.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (pathInput.getText().isEmpty()) {
                    pathInput.setForeground(GuiFactory.TEXT_GRAY);
                    pathInput.setText(defaultPath);
                }
            }
        });

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setOpaque(false);
        inputWrapper.setBorder(new EmptyBorder(15, 0, 0, 0));
        inputWrapper.add(pathInput, BorderLayout.CENTER);

        return createBaseCard(
                "СКАНИРОВАНИЕ ДИСКА",
                "Анализ файлов в выбранной папке.",
                inputWrapper,
                "НАЧАТЬ ПРОВЕРКУ",
                e -> controller.startDiskScan(pathInput.getText())
        );
    }

    private JPanel createBaseCard(String title, String desc, JComponent extra, String btnText, java.awt.event.ActionListener action) {
        GuiFactory.RoundedPanel card = new GuiFactory.RoundedPanel(24, new BorderLayout(0, 0));
        card.setBackground(GuiFactory.PANEL_BG);
        card.setPreferredSize(new Dimension(420, 280));
        card.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);

        JLabel t = new JLabel(title);
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
        d.setHighlighter(null);

        top.add(t, BorderLayout.NORTH);
        top.add(d, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        if (extra != null) center.add(extra, BorderLayout.NORTH);

        GuiFactory.RoundedButton btn = new GuiFactory.RoundedButton(btnText, true);
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));
        btn.addActionListener(action);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(35, 35, 45));
                card.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(GuiFactory.PANEL_BG);
                card.repaint();
            }
        });

        return card;
    }
}