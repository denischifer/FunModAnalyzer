package dev.denischifer.ui.controller;

import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.core.async.ScanCallback;
import dev.denischifer.core.async.tasks.DiskScanTask;
import dev.denischifer.core.async.tasks.MemoryScanTask;
import dev.denischifer.ui.screen.LoadingScreen;
import dev.denischifer.util.ProcessUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class ScanController {
    @Getter
    private final MainController mainController;

    public void startDiskScan(@NotNull String pathStr) {
        try {
            LoadingScreen loadingScreen = new LoadingScreen();
            mainController.getNavigation().showLoading(loadingScreen);

            DiskScanTask task = new DiskScanTask(Path.of(pathStr), () -> {});

            Thread updateThread = getThread(task, loadingScreen);
            updateThread.start();

            mainController.getExecutor().execute(task, new ScanCallback<>() {
                @Override
                public void onProgress(double progress) {
                }

                @Override
                public void onSuccess(@NotNull List<ModInfo> result) {
                    SwingUtilities.invokeLater(() ->
                            mainController.getNavigation().showDashboard(result, pathStr)
                    );
                }

                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    SwingUtilities.invokeLater(() -> {
                        mainController.getNavigation().showSelection();
                        JOptionPane.showMessageDialog(null, "Ошибка: " + throwable.getMessage());
                    });
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка пути: " + e.getMessage());
        }
    }

    public void startMemoryScan() {
        mainController.getNavigation().showProcessSelection(this);
    }

    public void executeMemoryScan(int pid) {
        String pathStr = ProcessUtil.getModsPathForPid(pid);

        if (pathStr == null) {
            JOptionPane.showMessageDialog(null, "Не удалось определить директорию mods для процесса " + pid);
            return;
        }

        try {
            LoadingScreen loadingScreen = new LoadingScreen();
            mainController.getNavigation().showLoading(loadingScreen);

            MemoryScanTask task = new MemoryScanTask(Path.of(pathStr), () -> {});

            Thread updateThread = getMemoryThread(task, loadingScreen);
            updateThread.start();

            mainController.getExecutor().execute(task, new ScanCallback<>() {
                @Override
                public void onProgress(double progress) {
                }

                @Override
                public void onSuccess(@NotNull List<ModInfo> result) {
                    SwingUtilities.invokeLater(() ->
                            mainController.getNavigation().showDashboard(result, pathStr)
                    );
                }

                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    SwingUtilities.invokeLater(() -> {
                        mainController.getNavigation().showSelection();
                        JOptionPane.showMessageDialog(null, "Ошибка сканирования памяти: " + throwable.getMessage());
                    });
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
        }
    }

    private static @NotNull Thread getThread(DiskScanTask task, LoadingScreen loadingScreen) {
        Thread updateThread = new Thread(() -> {
            while (true) {
                int current = task.getCurrent();
                int total = task.getTotal();

                loadingScreen.updateProgress(current, total, "Анализ файлов: " + current + " / " + total);

                if (total > 0 && current >= total) break;

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        return updateThread;
    }

    private static @NotNull Thread getMemoryThread(MemoryScanTask task, LoadingScreen loadingScreen) {
        Thread updateThread = new Thread(() -> {
            while (true) {
                int current = task.getCurrent();
                int total = task.getTotal();

                loadingScreen.updateProgress(current, total, "Анализ файлов: " + current + " / " + total);

                if (total > 0 && current >= total) break;

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        return updateThread;
    }
}