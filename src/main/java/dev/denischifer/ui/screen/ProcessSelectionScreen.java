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
        view.setLayout(new BorderLayout(0, 0));
        view.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("АКТИВНЫЕ ПРОЦЕССЫ");
        title.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Выберите процесс Minecraft или Java для сканирования модулей");
        subtitle.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 14));
        subtitle.setForeground(GuiFactory.TEXT_GRAY);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        String[] columns = {"PID", "Имя процесса", "Заголовок окна"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        List<ProcessUtil.JavaProcessInfo> processes = ProcessUtil.getJavaProcesses();
        for (ProcessUtil.JavaProcessInfo p : processes) {
            model.addRow(new Object[]{p.pid(), p.name(), p.windowTitle().isEmpty() ? "---" : p.windowTitle()});
        }

        table = new JTable(model);
        table.setBackground(GuiFactory.PANEL_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(40, 40, 45));
        table.setRowHeight(54);
        table.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 15));
        table.setSelectionBackground(new Color(60, 60, 75));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setBackground(GuiFactory.BG_DARK);
        table.getTableHeader().setForeground(GuiFactory.TEXT_GRAY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GuiFactory.ACCENT_BLUE));

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, false, row, col);
                setBorder(new EmptyBorder(0, 20, 0, 20));

                if (isSelected) {
                    c.setForeground(Color.WHITE);
                } else {
                    if (col == 0) c.setForeground(GuiFactory.ACCENT_BLUE);
                    else if (col == 2 && "---".equals(value)) c.setForeground(GuiFactory.TEXT_GRAY);
                    else c.setForeground(new Color(220, 220, 225));
                }

                if (col == 0) c.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 13));
                else c.setFont(new Font(GuiFactory.MAIN_FONT, Font.PLAIN, 14));

                return c;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    handleSelection();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        GuiFactory.applyModernScrollBar(scrollPane);
        scrollPane.getViewport().setBackground(GuiFactory.PANEL_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(GuiFactory.BORDER_COLOR, 1));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        JButton backBtn = new GuiFactory.RoundedButton("ОТМЕНА", false);
        backBtn.setPreferredSize(new Dimension(130, 45));
        backBtn.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));
        backBtn.addActionListener(e -> controller.getMainController().getNavigation().showSelection());

        JButton selectBtn = new GuiFactory.RoundedButton("НАЧАТЬ АНАЛИЗ", true);
        selectBtn.setPreferredSize(new Dimension(180, 45));
        selectBtn.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 13));
        selectBtn.addActionListener(e -> handleSelection());

        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnContainer.setOpaque(false);
        btnContainer.add(backBtn);
        btnContainer.add(selectBtn);

        JLabel countLabel = new JLabel("Найдено процессов: " + processes.size());
        countLabel.setFont(new Font(GuiFactory.MAIN_FONT, Font.ITALIC, 13));
        countLabel.setForeground(GuiFactory.TEXT_GRAY);

        bottomPanel.add(countLabel, BorderLayout.WEST);
        bottomPanel.add(btnContainer, BorderLayout.EAST);

        view.add(headerPanel, BorderLayout.NORTH);
        view.add(scrollPane, BorderLayout.CENTER);
        view.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleSelection() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int pid = (int) table.getValueAt(row, 0);
            controller.executeMemoryScan(pid);
        } else {
            showWarning();
        }
    }

    private void showWarning() {
        UIManager.put("OptionPane.background", GuiFactory.BG_DARK);
        UIManager.put("Panel.background", GuiFactory.BG_DARK);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        JOptionPane.showMessageDialog(view, "Пожалуйста, выберите активный процесс из списка для продолжения.", "Внимание", JOptionPane.WARNING_MESSAGE);
    }
}