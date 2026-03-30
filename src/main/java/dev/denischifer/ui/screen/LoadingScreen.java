package dev.denischifer.ui.screen;

import dev.denischifer.util.GuiFactory;
import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoadingScreen {
    @Getter private final JPanel view;
    private final JLabel progressLabel;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public LoadingScreen() {
        this.view = GuiFactory.createMainPanel();
        this.view.setLayout(new GridBagLayout());

        GuiFactory.RoundedPanel panel = new GuiFactory.RoundedPanel(24, new BorderLayout(0, 0));
        panel.setBackground(GuiFactory.PANEL_BG);
        panel.setPreferredSize(new Dimension(500, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        JLabel title = new JLabel("АНАЛИЗ МОДИФИКАЦИЙ");
        title.setFont(new Font("Impact", Font.PLAIN, 28));
        title.setForeground(Color.WHITE);

        progressLabel = new JLabel("0 / 0", SwingConstants.RIGHT);
        progressLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        progressLabel.setForeground(GuiFactory.ACCENT_BLUE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(progressLabel, BorderLayout.EAST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 16));
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = progressBar.getWidth();
                int h = progressBar.getHeight();
                g2.setColor(new Color(30, 30, 35));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
                double percent = progressBar.getPercentComplete();
                if (percent > 0) {
                    int fillW = (int) (w * percent);
                    g2.setColor(GuiFactory.ACCENT_BLUE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, Math.max(fillW, h), h, h, h));
                }
                g2.dispose();
            }
        });
        progressBar.setOpaque(false);
        progressBar.setBorder(null);

        statusLabel = new JLabel("Подготовка к сканированию...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(GuiFactory.TEXT_GRAY);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 12, 0);
        content.add(progressBar, gbc);
        gbc.gridy = 1;
        content.add(statusLabel, gbc);

        panel.add(top, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        view.add(panel);
    }

    public void updateProgress(int current, int total, String status) {
        SwingUtilities.invokeLater(() -> {
            if (total > 0) {
                progressBar.setMaximum(total);
                progressBar.setValue(current);
                progressLabel.setText(current + " / " + total);
            }
            if (status != null) statusLabel.setText(status);
        });
    }
}