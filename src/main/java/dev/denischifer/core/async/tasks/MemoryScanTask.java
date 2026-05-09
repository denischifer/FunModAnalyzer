package dev.denischifer.core.async.tasks;

import dev.denischifer.core.analyzer.ModAnalyzer;
import dev.denischifer.core.analyzer.ModInfo;
import dev.denischifer.core.async.ScanTask;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryScanTask extends ScanTask<List<ModInfo>> {
    private final List<Path> jarFiles;
    private final AtomicInteger current = new AtomicInteger(0);
    @Getter private final int total;

    public MemoryScanTask(@NotNull Path targetDirectory) throws IOException {
        super("MemoryScanTask");
        this.jarFiles = findJarFiles(targetDirectory);
        this.total = jarFiles.size();
    }

    public int getCurrent() {
        return current.get();
    }

    @Override
    public List<ModInfo> call() {
        if (jarFiles.isEmpty()) return Collections.emptyList();

        return jarFiles.parallelStream().map(jar -> {
            boolean modrinth = ModAnalyzer.checkModrinthApi(jar);
            List<ModInfo.AnalysisEntry> analysisResults = modrinth
                    ? Collections.emptyList()
                    : ModAnalyzer.getHeuristicResults(jar);

            ModInfo info = ModInfo.builder()
                    .name(jar.getFileName().toString())
                    .path(jar.toAbsolutePath().toString())
                    .sizeBytes(getFileSizeSafe(jar))
                    .modrinthFound(modrinth)
                    .suspicious(!modrinth && !analysisResults.isEmpty())
                    .downloadSource(ModAnalyzer.getDownloadSource(jar))
                    .analysisResults(analysisResults)
                    .build();

            current.incrementAndGet();
            return info;
        }).collect(Collectors.toList());
    }

    private long getFileSizeSafe(Path p) {
        try { return Files.size(p); } catch (IOException e) { return 0; }
    }

    private List<Path> findJarFiles(Path dir) throws IOException {
        if (dir == null || !Files.exists(dir)) return Collections.emptyList();
        try (Stream<Path> stream = Files.walk(dir, 1)) {
            return stream.filter(p -> p.toString().toLowerCase().endsWith(".jar") && Files.isRegularFile(p))
                    .collect(Collectors.toList());
        }
    }
}