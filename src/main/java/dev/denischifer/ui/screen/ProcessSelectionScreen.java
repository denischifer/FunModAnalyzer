package dev.denischifer.ui.screen;

import dev.denischifer.ui.controller.ScanningController;
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
    private final ScanningController controller;
    private JTable table;
    private DefaultTableModel model;
    private JLabel countLabel;

    public ProcessSelectionScreen(@NotNull ScanningController controller) {
        this.controller = controller;
        this.view = GuiFactory.createMainPanel();
        initLayout();
        loadProcessesAsync();
    }

    private void initLayout() {
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JPanel header = createHeader();

        model = new DefaultTableModel(new String[]{"PID", "Имя процесса", "Заголовок окна"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        setupTableStyle();

        JScrollPane scroll = new JScrollPane(table);
        GuiFactory.applyModernScrollBar(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 45), 1, true));
        scroll.getViewport().setBackground(new Color(24, 24, 27));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(40, 0, 0, 0));

        countLabel = new JLabel("Поиск процессов...");
        countLabel.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 13));
        countLabel.setForeground(GuiFactory.TEXT_GRAY);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        btns.setOpaque(false);

        GuiFactory.RoundedButton back = new GuiFactory.RoundedButton("ОТМЕНА", false);
        back.setPreferredSize(new Dimension(140, 50));
        back.addActionListener(e -> controller.openSelection());

        GuiFactory.RoundedButton next = new GuiFactory.RoundedButton("НАЧАТЬ АНАЛИЗ", true);
        next.setPreferredSize(new Dimension(190, 50));
        next.addActionListener(e -> handleSelection());

        btns.add(back);
        btns.add(next);

        bottom.add(countLabel, BorderLayout.WEST);
        bottom.add(btns, BorderLayout.EAST);

        view.add(header, BorderLayout.NORTH);
        view.add(scroll, BorderLayout.CENTER);
        view.add(bottom, BorderLayout.SOUTH);
    }

    private void setupTableStyle() {
        table.setBackground(new Color(24, 24, 27));
        table.setRowHeight(68);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setPreferredSize(new Dimension(0, 52));
        table.getTableHeader().setBackground(new Color(18, 18, 20));
        table.getTableHeader().setForeground(GuiFactory.TEXT_GRAY);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                Component c = super.getTableCellRendererComponent(t, v, s, false, r, col);
                setBorder(new EmptyBorder(0, 25, 0, 25));
                c.setFont(new Font(col == 0 ? GuiFactory.MONO_FONT : GuiFactory.MAIN_FONT, col == 0 ? Font.BOLD : Font.PLAIN, 15));
                c.setBackground(s ? new Color(38, 38, 45) : new Color(24, 24, 27));
                c.setForeground(s ? Color.WHITE : (col == 0 ? GuiFactory.ACCENT_BLUE : GuiFactory.TEXT_PRIMARY));
                return c;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) handleSelection(); }
        });
    }

    private void loadProcessesAsync() {
        new SwingWorker<List<ProcessUtil.JavaProcessInfo>, Void>() {
            @Override protected List<ProcessUtil.JavaProcessInfo> doInBackground() {
                return ProcessUtil.getJavaProcesses();
            }
            @Override protected void done() {
                try {
                    List<ProcessUtil.JavaProcessInfo> list = get();
                    model.setRowCount(0);
                    for (ProcessUtil.JavaProcessInfo p : list) {
                        model.addRow(new Object[]{p.getPid(), p.getName(), p.getWindowTitle().isEmpty() ? "---" : p.getWindowTitle()});
                    }
                    countLabel.setText("Найдено процессов: " + list.size());
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 40, 0));
        JLabel title = new JLabel("АКТИВНЫЕ ПРОЦЕССЫ");
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Выберите процесс Minecraft или Java для сканирования модулей");
        sub.setForeground(GuiFactory.TEXT_GRAY);
        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        return header;
    }

    private void handleSelection() {
        int row = table.getSelectedRow();
        if (row != -1) controller.startMemoryScan((int) table.getValueAt(row, 0));
    }
}