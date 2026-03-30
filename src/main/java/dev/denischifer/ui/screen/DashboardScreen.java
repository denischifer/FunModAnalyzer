package dev.denischifer.ui.screen;

import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.util.ByteUtil;
import dev.denischifer.util.GuiFactory;
import dev.denischifer.util.ProcessUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardScreen {
    @Getter private final JPanel view;
    private final List<ModInfo> results;
    private final String targetPath;
    private TableRowSorter<DefaultTableModel> sorter;
    private final Set<String> activeFilters = new HashSet<>();
    private JTextField searchField;

    public DashboardScreen(@NotNull List<ModInfo> results, @NotNull String targetPath) {
        this.results = results;
        this.targetPath = targetPath;
        this.view = GuiFactory.createMainPanel();
        initLayout();
    }

    private void initLayout() {
        view.setLayout(new BorderLayout(0, 20));
        view.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        view.add(createHeader(), BorderLayout.NORTH);
        view.add(createTableContainer(), BorderLayout.CENTER);
        view.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 25));
        header.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(20, 0));
        top.setOpaque(false);

        JLabel title = new JLabel("FunModAnalyzer");
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 32));
        title.setForeground(GuiFactory.TEXT_PRIMARY);

        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.fill = GridBagConstraints.HORIZONTAL;
        gbcInfo.insets = new Insets(0, 15, 0, 15);

        info.add(createInfoLabel("ПРОЦЕСС", ProcessUtil.getMinecraftStartTime(), 150), gbcInfo);
        gbcInfo.weightx = 1.0;
        info.add(createInfoLabel("ДИРЕКТОРИЯ", targetPath, 350), gbcInfo);

        top.add(title, BorderLayout.WEST);
        top.add(info, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(25, 0));
        actions.setOpaque(false);

        searchField = GuiFactory.createStyledField("");
        searchField.setPreferredSize(new Dimension(380, 42));
        searchField.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 14));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filters.setOpaque(false);
        String[] tags = {"Валидный", "Невалидный", "Подозрительный"};
        for (String tag : tags) {
            JButton b = getJButton(tag);
            filters.add(b);
        }

        actions.add(searchField, BorderLayout.WEST);
        actions.add(filters, BorderLayout.EAST);

        header.add(top, BorderLayout.NORTH);
        header.add(actions, BorderLayout.CENTER);
        return header;
    }

    private @NotNull JButton getJButton(String tag) {
        JButton b = new GuiFactory.RoundedButton(tag, false);
        b.setPreferredSize(new Dimension(140, 38));
        b.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 12));
        b.addActionListener(e -> {
            if (activeFilters.contains(tag)) {
                activeFilters.remove(tag);
                b.setForeground(GuiFactory.TEXT_PRIMARY);
            } else {
                activeFilters.add(tag);
                b.setForeground(GuiFactory.ACCENT_BLUE);
            }
            updateFilters();
        });
        return b;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel version = new JLabel("v1.0.0 • denischifer");
        version.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 11));
        version.setForeground(GuiFactory.TEXT_GRAY);
        footer.add(version, BorderLayout.EAST);
        return footer;
    }

    private JPanel createInfoLabel(String title, String value, int maxWidth) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 10));
        t.setForeground(GuiFactory.ACCENT_BLUE);

        JLabel v = new JLabel(value);
        v.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));
        v.setForeground(Color.WHITE);
        v.setPreferredSize(new Dimension(maxWidth, 20));

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JComponent createTableContainer() {
        String[] columns = {"Имя мода", "Путь", "Размер", "Modrinth", "Проверка"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (ModInfo info : results) {
            String checkStatus = info.isModrinthFound() ? "Пропущено" : (info.isSuspicious() ? "Подозрительный" : "ОК");
            model.addRow(new Object[]{
                    info.getName(), info.getPath(), ByteUtil.formatBytes(info.getSizeBytes()),
                    info.isModrinthFound() ? "Валидный" : "Невалидный", checkStatus
            });
        }

        JTable table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setBackground(GuiFactory.PANEL_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(45, 45, 50));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(50, 50, 60));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 14));

        table.getTableHeader().setBackground(GuiFactory.BG_DARK);
        table.getTableHeader().setForeground(GuiFactory.TEXT_GRAY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GuiFactory.BORDER_COLOR));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(220);
        columnModel.getColumn(1).setPreferredWidth(350);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(120);
        columnModel.getColumn(4).setPreferredWidth(150);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, false, row, col);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                int modelRow = t.convertRowIndexToModel(row);
                String modrinthStatus = (String) t.getModel().getValueAt(modelRow, 3);
                String checkStatus = (String) t.getModel().getValueAt(modelRow, 4);

                if (!isSelected) {
                    c.setForeground(new Color(230, 230, 230));
                    if (col == 3) c.setForeground("Валидный".equals(modrinthStatus) ? new Color(74, 222, 128) : new Color(248, 113, 113));
                    if (col == 4) {
                        if ("Подозрительный".equals(checkStatus)) c.setForeground(new Color(248, 113, 113));
                        else if ("ОК".equals(checkStatus)) c.setForeground(new Color(74, 222, 128));
                    }
                    if (col == 0 && "Подозрительный".equals(checkStatus)) c.setForeground(new Color(248, 113, 113));
                }

                if (col == 1 || col == 2) c.setFont(new Font(GuiFactory.MONO_FONT, Font.PLAIN, 12));
                else c.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));

                return c;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    ModInfo info = results.get(table.convertRowIndexToModel(table.getSelectedRow()));
                    new ModDetailsDialog((JFrame) SwingUtilities.getWindowAncestor(view), info).setVisible(true);
                }
            }
        });

        JScrollPane sp = new JScrollPane(table);
        GuiFactory.applyModernScrollBar(sp);
        sp.getViewport().setBackground(GuiFactory.PANEL_BG);
        sp.setBorder(BorderFactory.createLineBorder(GuiFactory.BORDER_COLOR, 1));
        return sp;
    }

    private void updateFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String text = searchField.getText();
        if (!text.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + text));

        if (!activeFilters.isEmpty()) {
            List<RowFilter<Object, Object>> tagFilters = new ArrayList<>();
            for (String tag : activeFilters) tagFilters.add(RowFilter.regexFilter("(?i)" + tag));
            filters.add(RowFilter.orFilter(tagFilters));
        }

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }
}