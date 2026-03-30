package dev.denischifer.core.async;

import org.jetbrains.annotations.NotNull;

public interface ScanCallback<T> {
    void onProgress(double progress);

    void onSuccess(@NotNull T result);

    void onFailure(@NotNull Throwable throwable);
}