package dev.denischifer.core.async;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public abstract class ScanTask<T> implements Callable<T> {
    @NotNull
    protected final String taskName;

    @Override
    public abstract T call() throws Exception;
}