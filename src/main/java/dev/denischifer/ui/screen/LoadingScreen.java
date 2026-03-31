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

        JPanel root = new JPanel(new BorderLayout(0, 35));
        root.setOpaque(false);
        root.setPreferredSize(new Dimension(680, 180));

        JLabel title = new JLabel("АНАЛИЗ МОДИФИКАЦИЙ", SwingConstants.LEFT);
        title.setFont(new Font("Impact", Font.PLAIN, 54));
        title.setForeground(Color.WHITE);

        progressLabel = new JLabel("0 %", SwingConstants.RIGHT);
        progressLabel.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 28));
        progressLabel.setForeground(GuiFactory.ACCENT_BLUE);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(progressLabel, BorderLayout.EAST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 12));
        progressBar.setUI(new BasicProgressBarUI() {
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = progressBar.getWidth();
                int h = progressBar.getHeight();
                g2.setColor(new Color(30, 30, 35));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
                double p = progressBar.getPercentComplete();
                if (p > 0) {
                    GradientPaint grad = new GradientPaint(0, 0, GuiFactory.ACCENT_BLUE, w, 0, new Color(130, 190, 255));
                    g2.setPaint(grad);
                    g2.fill(new RoundRectangle2D.Double(0, 0, (int)(w * p), h, h, h));
                }
                g2.dispose();
            }
        });

        statusLabel = new JLabel("ПОДГОТОВКА К СКАНИРОВАНИЮ...");
        statusLabel.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 14));
        statusLabel.setForeground(GuiFactory.TEXT_GRAY);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);
        body.add(progressBar, BorderLayout.NORTH);
        body.add(statusLabel, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);

        view.add(root);
    }

    public void updateProgress(int current, int total, String status) {
        SwingUtilities.invokeLater(() -> {
            if (total > 0) {
                progressBar.setMaximum(total);
                progressBar.setValue(current);
                progressLabel.setText((int)((double)current/total * 100) + " %");
            }
            if (status != null) statusLabel.setText(status.toUpperCase());
        });
    }
}