package dev.denischifer.ui.controller;

import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.core.async.ScanCallback;
import dev.denischifer.core.async.ScanTask;
import dev.denischifer.core.async.tasks.DiskScanTask;
import dev.denischifer.core.async.tasks.MemoryScanTask;
import dev.denischifer.ui.screen.DashboardScreen;
import dev.denischifer.ui.screen.LoadingScreen;
import dev.denischifer.ui.screen.ProcessSelectionScreen;
import dev.denischifer.util.ProcessUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
public class ScanningController {
    private final AppController app;

    public void openSelection() {
        app.getRouter().navigate("selection");
    }

    public void prepareMemoryScan() {
        app.getRouter().navigate("process_selection", new ProcessSelectionScreen(this).getView());
    }

    public void startDiskScan(@NotNull String pathStr) {
        try {
            executeScan(new DiskScanTask(Paths.get(pathStr)), pathStr);
        } catch (IOException e) {
            app.fatalError("Ошибка доступа к диску: " + e.getMessage());
        }
    }

    public void startMemoryScan(int pid) {
        String path = ProcessUtil.getModsPathForPid(pid);
        if (path == null) {
            app.fatalError("Не удалось найти директорию mods для PID: " + pid);
            return;
        }
        try {
            executeScan(new MemoryScanTask(Paths.get(path)), path);
        } catch (IOException e) {
            app.fatalError("Ошибка доступа к памяти: " + e.getMessage());
        }
    }

    private void executeScan(ScanTask<List<ModInfo>> task, String displayPath) {
        LoadingScreen loadingScreen = new LoadingScreen();
        app.getRouter().navigate("loading", loadingScreen.getView());

        Timer progressTimer = new Timer(30, e -> {
            int current = 0;
            int total = 0;
            if (task instanceof DiskScanTask) {
                current = ((DiskScanTask) task).getCurrent();
                total = ((DiskScanTask) task).getTotal();
            } else if (task instanceof MemoryScanTask) {
                current = ((MemoryScanTask) task).getCurrent();
                total = ((MemoryScanTask) task).getTotal();
            }
            loadingScreen.updateProgress(current, total, "Анализ: " + current + " / " + total);
        });
        progressTimer.start();

        app.getExecutor().execute(task, new ScanCallback<List<ModInfo>>() {
            @Override public void onProgress(double progress) {}

            @Override
            public void onSuccess(@NotNull List<ModInfo> result) {
                SwingUtilities.invokeLater(() -> {
                    progressTimer.stop();
                    app.getRouter().navigate("dashboard", new DashboardScreen(result, displayPath).getView());
                });
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    progressTimer.stop();
                    openSelection();
                    app.fatalError("Ошибка: " + t.getMessage());
                });
            }
        });
    }
}