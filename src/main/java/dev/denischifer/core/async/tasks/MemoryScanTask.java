package dev.denischifer.core.async.tasks;

import dev.denischifer.core.analyzer.ModAnalyzer;
import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.core.async.ScanTask;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MemoryScanTask extends ScanTask<List<ModInfo>> {
    private final Runnable onProgressTick;
    private final List<Path> jarFiles;
    @Getter
    private int current;
    @Getter
    private final int total;

    public MemoryScanTask(@NotNull Path targetDirectory, @NotNull Runnable onProgressTick) throws IOException {
        super("MemoryScanTask");
        this.onProgressTick = onProgressTick;
        this.jarFiles = findJarFiles(targetDirectory);
        this.total = jarFiles.size();
    }

    @Override
    public List<ModInfo> call() throws Exception {
        List<ModInfo> results = new ArrayList<>();

        for (Path jar : jarFiles) {
            boolean modrinth = ModAnalyzer.checkModrinthApi(jar);
            List<String> logs;

            if (modrinth) {
                logs = List.of("Проверка пропущена");
            } else {
                logs = ModAnalyzer.getHeuristicLogs(jar);
            }

            results.add(ModInfo.builder()
                    .name(jar.getFileName().toString())
                    .path(jar.toAbsolutePath().toString())
                    .sizeBytes(Files.size(jar))
                    .modrinthFound(modrinth)
                    .suspicious(!modrinth && !logs.isEmpty())
                    .downloadSource(ModAnalyzer.getDownloadSource(jar))
                    .heuristicLogs(logs)
                    .build());

            current++;
            onProgressTick.run();
        }

        return results;
    }

    private List<Path> findJarFiles(Path dir) throws IOException {
        if (!Files.exists(dir)) return List.of();
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(p -> p.toString().toLowerCase().endsWith(".jar") && Files.isRegularFile(p))
                    .toList();
        }
    }
}