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

        JPanel container = new JPanel(new BorderLayout(0, 25));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(600, 150));

        JLabel title = new JLabel("АНАЛИЗ МОДИФИКАЦИЙ", SwingConstants.LEFT);
        title.setFont(new Font("Impact", Font.PLAIN, 42));
        title.setForeground(Color.WHITE);

        progressLabel = new JLabel("0 / 0", SwingConstants.RIGHT);
        progressLabel.setFont(new Font(GuiFactory.MONO_FONT, Font.BOLD, 20));
        progressLabel.setForeground(GuiFactory.ACCENT_BLUE);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(progressLabel, BorderLayout.EAST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 12));
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = progressBar.getWidth();
                int h = progressBar.getHeight();

                g2.setColor(new Color(40, 40, 45));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));

                double percent = progressBar.getPercentComplete();
                if (percent > 0) {
                    int fillW = (int) (w * percent);
                    g2.setPaint(new GradientPaint(0, 0, GuiFactory.ACCENT_BLUE, w, 0, new Color(100, 180, 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, Math.max(fillW, h), h, h, h));
                }
                g2.dispose();
            }
        });
        progressBar.setOpaque(false);
        progressBar.setBorder(null);

        statusLabel = new JLabel("Подготока к сканированию...");
        statusLabel.setFont(new Font(GuiFactory.MAIN_FONT, Font.BOLD, 14));
        statusLabel.setForeground(GuiFactory.TEXT_GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel body = new JPanel(new BorderLayout(0, 15));
        body.setOpaque(false);
        body.add(progressBar, BorderLayout.NORTH);
        body.add(statusLabel, BorderLayout.SOUTH);

        container.add(header, BorderLayout.NORTH);
        container.add(body, BorderLayout.CENTER);

        view.add(container);
    }

    public void updateProgress(int current, int total, String status) {
        SwingUtilities.invokeLater(() -> {
            if (total > 0) {
                progressBar.setMaximum(total);
                progressBar.setValue(current);
                progressLabel.setText(current + " / " + total);
            }
            if (status != null) {
                statusLabel.setText(status.toUpperCase());
            }
        });
    }
}