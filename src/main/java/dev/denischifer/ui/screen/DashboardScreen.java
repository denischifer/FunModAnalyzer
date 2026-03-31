package dev.denischifer.ui.screen;

import com.formdev.flatlaf.FlatClientProperties;
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
        view.setLayout(new BorderLayout(0, 25));
        view.setBorder(BorderFactory.createEmptyBorder(35, 40, 25, 40));
        view.add(createHeader(), BorderLayout.NORTH);
        view.add(createTableContainer(), BorderLayout.CENTER);
        view.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 30));
        header.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(30, 0));
        top.setOpaque(false);

        JLabel title = new JLabel("FunModAnalyzer");
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 38));
        title.setForeground(Color.WHITE);

        JPanel infoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
        infoWrapper.setOpaque(false);
        infoWrapper.add(createInfoLabel("ПРОЦЕСС", ProcessUtil.getMinecraftStartTime()));
        infoWrapper.add(createInfoLabel("ДИРЕКТОРИЯ", targetPath));

        top.add(title, BorderLayout.WEST);
        top.add(infoWrapper, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(30, 0));
        actions.setOpaque(false);

        searchField = GuiFactory.createStyledField("");
        searchField.setPreferredSize(new Dimension(420, 44));
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Поиск по названию или пути...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filters.setOpaque(false);
        String[] tags = {"Валидный", "Невалидный", "Подозрительный"};
        for (String tag : tags) {
            filters.add(createFilterButton(tag));
        }

        actions.add(searchField, BorderLayout.WEST);
        actions.add(filters, BorderLayout.EAST);

        header.add(top, BorderLayout.NORTH);
        header.add(actions, BorderLayout.SOUTH);
        return header;
    }

    private JButton createFilterButton(String tag) {
        JButton b = new GuiFactory.RoundedButton(tag.toUpperCase(), false);
        b.setPreferredSize(new Dimension(150, 40));
        b.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 11));
        b.addActionListener(e -> {
            if (activeFilters.contains(tag)) {
                activeFilters.remove(tag);
                b.putClientProperty(FlatClientProperties.STYLE, "borderColor: #3f3f46; foreground: #f4f4f5");
            } else {
                activeFilters.add(tag);
                b.putClientProperty(FlatClientProperties.STYLE, "borderColor: #3b82f6; foreground: #3b82f6");
            }
            updateFilters();
        });
        return b;
    }

    private JPanel createInfoLabel(String title, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 11));
        t.setForeground(GuiFactory.ACCENT_BLUE);

        JLabel v = new JLabel(value);
        v.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 14));
        v.setForeground(GuiFactory.TEXT_PRIMARY);

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JComponent createTableContainer() {
        String[] columns = {"ИМЯ МОДА", "ПУТЬ", "РАЗМЕР", "Modrinth API", "РЕЗУЛЬТАТ"};
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
        table.setRowHeight(56);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setBackground(GuiFactory.BG_DARK);
        table.getTableHeader().setForeground(GuiFactory.TEXT_GRAY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 48));
        table.getTableHeader().setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 12));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(250);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(4).setPreferredWidth(160);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, false, row, col);
                setBorder(new EmptyBorder(0, 20, 0, 20));

                int modelRow = t.convertRowIndexToModel(row);
                String modrinthStatus = (String) t.getModel().getValueAt(modelRow, 3);
                String checkStatus = (String) t.getModel().getValueAt(modelRow, 4);

                if (!isSelected) {
                    c.setForeground(GuiFactory.TEXT_PRIMARY);
                    if (col == 3) c.setForeground("Валидный".equals(modrinthStatus) ? new Color(52, 211, 153) : new Color(248, 113, 113));
                    if (col == 4) {
                        if ("Подозрительный".equals(checkStatus)) c.setForeground(new Color(248, 113, 113));
                        else if ("ОК".equals(checkStatus)) c.setForeground(new Color(52, 211, 153));
                    }
                }

                c.setFont(new Font(col == 1 || col == 2 ? GuiFactory.MONO_FONT : GuiFactory.MAIN_FONT,
                        col == 0 ? Font.BOLD : Font.PLAIN, 14));

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
        sp.setBorder(BorderFactory.createLineBorder(GuiFactory.BORDER_COLOR));
        return sp;
    }

    private void updateFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String text = searchField.getText();

        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }

        if (!activeFilters.isEmpty()) {
            List<RowFilter<Object, Object>> tagFilters = new ArrayList<>();
            for (String tag : activeFilters) {
                tagFilters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(tag)));
            }
            filters.add(RowFilter.orFilter(tagFilters));
        }

        if (sorter != null) {
            sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        }
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 5, 0, 5));

        JLabel version = new JLabel("FunModAnalyzer v1.0.0 // build by denischifer");
        version.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 11));
        version.setForeground(GuiFactory.TEXT_GRAY.darker());

        footer.add(version, BorderLayout.WEST);
        return footer;
    }
}