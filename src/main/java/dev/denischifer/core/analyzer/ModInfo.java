package dev.denischifer.core.analyzer;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private List<String> heuristicLogs;
}