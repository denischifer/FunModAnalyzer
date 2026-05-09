package dev.denischifer.core.analyzer;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class ModInfo {
    @NotNull private String name;
    @NotNull private String path;
    private long sizeBytes;
    private boolean modrinthFound;
    private boolean suspicious;
    @Nullable private String downloadSource;
    private List<AnalysisEntry> analysisResults;

    public List<String> getHeuristicLogs() {
        if (analysisResults == null || analysisResults.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> logs = new ArrayList<>();
        for (AnalysisEntry entry : analysisResults) {
            logs.add(String.format("[%s] %s -> %s",
                    entry.getType().toUpperCase(),
                    entry.getClassName(),
                    entry.getDetail()));
        }
        return logs;
    }

    @Data
    @Builder
    public static class AnalysisEntry {
        private String className;
        private String type;
        private String detail;
    }
}