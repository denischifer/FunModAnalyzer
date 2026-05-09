package dev.denischifer.ui.controller;

import dev.denischifer.core.async.AsyncScannerExecutor;
import dev.denischifer.ui.screen.SelectionScreen;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AppController {
    @Getter private final AsyncScannerExecutor executor;
    @Getter private final ScreenRouter router;
    @Getter private final JFrame mainFrame;

    public AppController(@NotNull JFrame frame) {
        this.mainFrame = frame;
        this.executor = new AsyncScannerExecutor();

        JPanel root = new JPanel(new CardLayout());
        this.router = new ScreenRouter(root);

        frame.getContentPane().add(root, BorderLayout.CENTER);
    }

    public void init() {
        ScanningController scanningController = new ScanningController(this);
        router.register("selection", new SelectionScreen(scanningController).getView());
        router.navigate("selection");
    }

    public void fatalError(String msg) {
        JOptionPane.showMessageDialog(mainFrame, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}