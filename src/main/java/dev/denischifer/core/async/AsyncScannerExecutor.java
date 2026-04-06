package dev.denischifer.core.async;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncScannerExecutor {
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors())
    );

    public <T> void execute(@NotNull ScanTask<T> task, @NotNull ScanCallback<T> callback) {
        CompletableFuture.supplyAsync(() -> {
                    try {
                        return task.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        callback.onFailure(throwable);
                    } else {
                        callback.onSuccess(result);
                    }
                });
    }

    public void shutdown() {
        executor.shutdown();
    }
}