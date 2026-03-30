package dev.denischifer.ui.screen;

import dev.denischifer.ui.controller.ScanController;
import dev.denischifer.util.GuiFactory;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SelectionScreen {
    @Getter private final JPanel view;
    private final ScanController controller;

    public SelectionScreen(@NotNull ScanController controller) {
        this.controller = controller;
        this.view = GuiFactory.createMainPanel();
        initLayout();
    }

    private void initLayout() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(GuiFactory.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel header = getJPanel();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 60, 0);
        wrapper.add(header, gbc);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        cardsPanel.setOpaque(false);

        cardsPanel.add(createCard("СКАНИРОВАНИЕ ПАМЯТИ",
                "Анализ активных Java-процессов и модов в реальном времени.",
                null, "ЗАПУСТИТЬ АНАЛИЗ", e -> controller.startMemoryScan()));

        String defaultPath = "C:/Games/FunTime/mods";
        JTextField pathInput = GuiFactory.createStyledField(defaultPath);
        pathInput.setPreferredSize(new Dimension(300, 40));
        pathInput.setForeground(GuiFactory.TEXT_GRAY);

        ((AbstractDocument) pathInput.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                fb.insertString(offset, string.replaceAll("\\n", ""), attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                fb.replace(offset, length, text.replaceAll("\\n", ""), attrs);
            }
        });

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

        JPanel inputContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        inputContainer.setOpaque(false);
        inputContainer.add(pathInput);

        cardsPanel.add(createCard("СКАНИРОВАНИЕ ДИСКА",
                "Статический анализ JAR-файлов в выбранной директории игры.",
                inputContainer, "НАЧАТЬ ПРОВЕРКУ", e -> controller.startDiskScan(pathInput.getText())));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        wrapper.add(cardsPanel, gbc);

        view.add(wrapper, BorderLayout.CENTER);
    }

    private static @NotNull JPanel getJPanel() {
        JPanel header = new JPanel(new BorderLayout(0, 5));
        header.setOpaque(false);

        JLabel title = new JLabel("FUN MOD ANALYZER");
        title.setFont(new Font("Impact", Font.PLAIN, 48));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("V 1.0.0 // dev by denischifer", SwingConstants.CENTER);
        sub.setFont(new Font("Monospaced", Font.BOLD, 14));
        sub.setForeground(GuiFactory.ACCENT_BLUE);

        header.add(title, BorderLayout.CENTER);
        header.add(sub, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createCard(String title, String desc, JComponent extra, String btnText, java.awt.event.ActionListener action) {
        GuiFactory.RoundedPanel card = new GuiFactory.RoundedPanel(16, new BorderLayout(0, 15));
        card.setBackground(GuiFactory.PANEL_BG);
        card.setPreferredSize(new Dimension(380, 260));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(Color.WHITE);

        JTextArea d = new JTextArea(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        d.setForeground(GuiFactory.TEXT_GRAY);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        d.setEditable(false);
        d.setOpaque(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(d, BorderLayout.NORTH);
        if (extra != null) {
            content.add(extra, BorderLayout.CENTER);
        }

        GuiFactory.RoundedButton btn = new GuiFactory.RoundedButton(btnText, true);
        btn.setPreferredSize(new Dimension(0, 45));
        btn.addActionListener(action);

        card.add(t, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        return card;
    }
}