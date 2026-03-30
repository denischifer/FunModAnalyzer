package dev.denischifer.ui.controller;

import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.ui.screen.SelectionScreen;
import dev.denischifer.ui.screen.DashboardScreen;
import dev.denischifer.ui.screen.LoadingScreen;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NavigationController {
    @Getter
    private final JFrame frame;
    private final MainController mainController;

    public NavigationController(@NotNull JFrame frame, @NotNull MainController mainController) {
        this.frame = frame;
        this.mainController = mainController;
    }

    public void showSelection() {
        render(new SelectionScreen(new ScanController(mainController)).getView());
    }

    public void showDashboard(List<ModInfo> results, String path) {
        render(new DashboardScreen(results, path).getView());
    }

    public void showLoading(LoadingScreen screen) {
        render(screen.getView());
    }

    private void render(@NotNull JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}