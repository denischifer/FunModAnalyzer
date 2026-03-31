package dev.denischifer.ui.screen;

import dev.denischifer.ui.controller.ScanController;
import dev.denischifer.util.GuiFactory;
import dev.denischifer.util.ProcessUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProcessSelectionScreen {
    @Getter private final JPanel view;
    private final ScanController controller;
    private JTable table;

    public ProcessSelectionScreen(@NotNull ScanController controller) {
        this.controller = controller;
        this.view = GuiFactory.createMainPanel();
        initLayout();
    }

    private void initLayout() {
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 40, 0));

        JLabel title = new JLabel("АКТИВНЫЕ ПРОЦЕССЫ");
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Выберите процесс Minecraft или Java для сканирования модулей");
        sub.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 16));
        sub.setForeground(GuiFactory.TEXT_GRAY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"PID", "Имя процесса", "Заголовок окна"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<ProcessUtil.JavaProcessInfo> processes = ProcessUtil.getJavaProcesses();
        for (ProcessUtil.JavaProcessInfo p : processes) {
            model.addRow(new Object[]{p.pid(), p.name(), p.windowTitle().isEmpty() ? "---" : p.windowTitle()});
        }

        table = new JTable(model);
        table.setBackground(GuiFactory.PANEL_BG);
        table.setRowHeight(62);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setPreferredSize(new Dimension(0, 52));
        table.getTableHeader().setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));
        table.getTableHeader().setBackground(GuiFactory.BG_DARK);
        table.getTableHeader().setForeground(GuiFactory.TEXT_GRAY);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                Component c = super.getTableCellRendererComponent(t, v, s, false, r, col);
                setBorder(new EmptyBorder(0, 25, 0, 25));
                c.setFont(new Font(col == 0 ? GuiFactory.MONO_FONT : GuiFactory.MAIN_FONT,
                        col == 0 ? Font.BOLD : Font.PLAIN, 15));

                if (s) {
                    c.setForeground(Color.WHITE);
                } else {
                    if (col == 0) c.setForeground(GuiFactory.ACCENT_BLUE);
                    else if (col == 2 && "---".equals(v)) c.setForeground(GuiFactory.TEXT_GRAY.darker());
                    else c.setForeground(GuiFactory.TEXT_PRIMARY);
                }
                return c;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) handleSelection(); }
        });

        JScrollPane scroll = new JScrollPane(table);
        GuiFactory.applyModernScrollBar(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(GuiFactory.BORDER_COLOR));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(40, 0, 0, 0));

        JLabel count = new JLabel("Найдено процессов: " + processes.size());
        count.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 13));
        count.setForeground(GuiFactory.TEXT_GRAY);

        GuiFactory.RoundedButton back = new GuiFactory.RoundedButton("ОТМЕНА", false);
        back.setPreferredSize(new Dimension(140, 50));
        back.addActionListener(e -> controller.getMainController().getNavigation().showSelection());

        GuiFactory.RoundedButton next = new GuiFactory.RoundedButton("НАЧАТЬ АНАЛИЗ", true);
        next.setPreferredSize(new Dimension(190, 50));
        next.addActionListener(e -> handleSelection());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        btns.setOpaque(false);
        btns.add(back);
        btns.add(next);

        bottom.add(count, BorderLayout.WEST);
        bottom.add(btns, BorderLayout.EAST);

        view.add(header, BorderLayout.NORTH);
        view.add(scroll, BorderLayout.CENTER);
        view.add(bottom, BorderLayout.SOUTH);
    }

    private void handleSelection() {
        int row = table.getSelectedRow();
        if (row != -1) controller.executeMemoryScan((int) table.getValueAt(row, 0));
    }
}