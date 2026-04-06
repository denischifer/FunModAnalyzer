package dev.denischifer.ui.screen;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatWindowResizer;
import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.util.ByteUtil;
import dev.denischifer.util.FileUtil;
import dev.denischifer.util.GuiFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ModDetailsDialog extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private Point mouseDownCompCoords = null;
    private final ModInfo info;

    public ModDetailsDialog(JFrame parent, ModInfo info) {
        super(parent, "Mod Details", true);
        this.info = info;

        setSize(900, 800);
        setMinimumSize(new Dimension(800, 700));
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        GuiFactory.RoundedPanel content = new GuiFactory.RoundedPanel(24, new BorderLayout());
        content.setBackground(GuiFactory.BG_DARK);
        content.putClientProperty(FlatClientProperties.STYLE, "border: 1,1,1,1,#2d2d30,,24");

        JPanel header = createHeader();
        initDragListeners(header);

        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(0, 40, 35, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        int row = 0;
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        main.add(createSectionTitle("ОБЩАЯ ИНФОРМАЦИЯ"), gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy = row++; main.add(createDataRow("Путь", info.getPath(), true), gbc);
        gbc.gridy = row++; main.add(createDataRow("Размер", ByteUtil.formatBytes(info.getSizeBytes()), false), gbc);
        gbc.gridy = row++; main.add(createDataRow("SHA-1 Хеш", getHashSafe(info.getPath()), true), gbc);
        gbc.gridy = row++; main.add(createDataRow("Дата изменения", getModDateSafe(info.getPath()), false), gbc);

        String source = info.getDownloadSource();
        gbc.gridy = row++; main.add(createDataRow("Источник", source != null ? source : "Неизвестно", source != null), gbc);

        gbc.gridy = row++; gbc.insets = new Insets(35, 0, 20, 0);
        main.add(createSectionTitle("АНАЛИЗ БЕЗОПАСНОСТИ"), gbc);

        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridy = row++; main.add(createStatusRow("Modrinth API", info.isModrinthFound() ? "ПОДТВЕРЖДЕНО" : "НЕ НАЙДЕНО", info.isModrinthFound()), gbc);
        gbc.gridy = row++; main.add(createStatusRow("Эвристика", info.isSuspicious() ? "ПОДОЗРИТЕЛЬНО" : "ЧИСТО", !info.isSuspicious()), gbc);

        gbc.gridy = row++; gbc.insets = new Insets(35, 0, 15, 0);
        main.add(createSectionTitle("ЛОГИ ПРОВЕРКИ"), gbc);

        JTextArea logs = new JTextArea(info.getHeuristicLogs().isEmpty() ? "Логи отсутствуют." : String.join("\n", info.getHeuristicLogs()));
        logs.setEditable(false);
        logs.setBackground(new Color(18, 18, 22));
        logs.setForeground(new Color(161, 161, 170));
        logs.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 12));
        logs.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(logs);
        GuiFactory.applyModernScrollBar(scroll);
        scroll.putClientProperty(FlatClientProperties.STYLE, "border: 1,1,1,1,#2d2d30,,12");
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 10, 0);
        main.add(scroll, gbc);

        content.add(header, BorderLayout.NORTH);
        content.add(main, BorderLayout.CENTER);

        setContentPane(content);
        new FlatWindowResizer.WindowResizer(getRootPane());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleName = new JLabel(info.getName());
        titleName.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 26));
        titleName.setForeground(info.isSuspicious() ? new Color(248, 113, 113) : Color.WHITE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton copyName = new GuiFactory.RoundedButton("КОПИРОВАТЬ ИМЯ", false);
        copyName.setPreferredSize(new Dimension(170, 40));
        copyName.addActionListener(e -> copyToClipboard(info.getName()));

        JButton close = new GuiFactory.RoundedButton("ЗАКРЫТЬ", true);
        close.setPreferredSize(new Dimension(110, 40));
        close.addActionListener(e -> dispose());

        actions.add(copyName);
        actions.add(close);

        header.add(titleName, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private void initDragListeners(Component dragArea) {
        dragArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseDownCompCoords = e.getPoint(); }
        });
        dragArea.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });
    }

    private JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 12));
        l.setForeground(GuiFactory.ACCENT_BLUE);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 52)),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)
        ));
        return l;
    }

    private JPanel createDataRow(String k, String v, boolean copyable) {
        JPanel p = new JPanel(new BorderLayout(25, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel kl = new JLabel(k.toUpperCase());
        kl.setForeground(GuiFactory.TEXT_GRAY);
        kl.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 11));
        kl.setPreferredSize(new Dimension(140, 25));

        JTextField vl = new JTextField(v);
        vl.setEditable(false);
        vl.setOpaque(false);
        vl.setBorder(null);
        vl.setForeground(new Color(212, 212, 216));
        vl.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 13));

        p.add(kl, BorderLayout.WEST);
        p.add(vl, BorderLayout.CENTER);

        if (copyable) {
            JButton copyBtn = new GuiFactory.RoundedButton("COPY", false);
            copyBtn.setPreferredSize(new Dimension(75, 28));
            copyBtn.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 9));
            copyBtn.addActionListener(e -> copyToClipboard(v));
            p.add(copyBtn, BorderLayout.EAST);
        }
        return p;
    }

    private JPanel createStatusRow(String k, String v, boolean good) {
        JPanel p = new JPanel(new BorderLayout(25, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel kl = new JLabel(k.toUpperCase());
        kl.setForeground(GuiFactory.TEXT_GRAY);
        kl.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 11));
        kl.setPreferredSize(new Dimension(140, 25));

        JLabel vl = new JLabel(v);
        vl.setForeground(good ? new Color(52, 211, 153) : new Color(248, 113, 113));
        vl.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 15));

        p.add(kl, BorderLayout.WEST);
        p.add(vl, BorderLayout.CENTER);
        return p;
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    private String getHashSafe(String p) { try { return FileUtil.getSha1Hash(Paths.get(p)); } catch (Exception e) { return "Error"; } }
    private String getModDateSafe(String p) {
        try { return DATE_FORMATTER.format(Files.getLastModifiedTime(Paths.get(p)).toInstant()); } catch (Exception e) { return "N/A"; }
    }
}