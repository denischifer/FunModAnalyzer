package dev.denischifer.ui.controller;

import dev.denischifer.core.async.AsyncScannerExecutor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainController {
    @Getter
    private final AsyncScannerExecutor executor;
    @Getter
    private final NavigationController navigation;

    public MainController(@NotNull JFrame mainFrame) {
        this.executor = new AsyncScannerExecutor();
        this.navigation = new NavigationController(mainFrame, this);
    }

    public void startApp() {
        navigation.showSelection();
    }

    public void shutdown() {
        executor.shutdown();
        System.exit(0);
    }
}