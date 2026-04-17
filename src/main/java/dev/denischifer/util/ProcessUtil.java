package dev.denischifer.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessUtil {
    private static final DateTimeFormatter UI_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            .withZone(ZoneId.systemDefault());

    @Data
    @AllArgsConstructor
    public static class JavaProcessInfo {
        private final int pid;
        private final String name;
        private final String windowTitle;
    }

    public static @NotNull List<JavaProcessInfo> getJavaProcesses() {
        List<JavaProcessInfo> processes = new ArrayList<>();
        String script = "Get-Process | Where-Object { $_.Name -match 'java' } | Select-Object Id, ProcessName, MainWindowTitle | ConvertTo-Csv -NoTypeInformation";
        List<String> output = executePowerShell(script);
        for (int i = 1; i < output.size(); i++) {
            String[] parts = output.get(i).replace("\"", "").split(",");
            if (parts.length >= 2) {
                try {
                    processes.add(new JavaProcessInfo(Integer.parseInt(parts[0]), parts[1], parts.length > 2 ? parts[2] : ""));
                } catch (Exception ignored) {}
            }
        }
        return processes;
    }

    public static @Nullable String getModsPathForPid(int pid) {
        Set<String> potentialRoots = new HashSet<>();

        String cmdLine = getCommandLine(pid);
        if (cmdLine != null) {
            Matcher m = Pattern.compile("[\"']?([A-Z]:\\\\[^\"']+)[\"']?").matcher(cmdLine);
            while (m.find()) {
                potentialRoots.add(m.group(1));
            }
        }

        List<String> modulePaths = executePowerShell("Get-Process -Id " + pid + " -Module -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FileName");
        potentialRoots.addAll(modulePaths);

        List<String> exePath = executePowerShell("(Get-Process -Id " + pid + ").Path");
        potentialRoots.addAll(exePath);

        for (String rawPath : potentialRoots) {
            try {
                Path p = Paths.get(rawPath);
                while (p != null) {
                    if (Files.exists(p.resolve("mods"))) {
                        return p.resolve("mods").toAbsolutePath().toString();
                    }
                    if (p.getFileName() != null && p.getFileName().toString().equalsIgnoreCase("mods")) {
                        return p.toAbsolutePath().toString();
                    }
                    p = p.getParent();
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    private static @Nullable String getCommandLine(int pid) {
        List<String> r = executePowerShell("(Get-WmiObject Win32_Process -Filter \"ProcessId = " + pid + "\").CommandLine");
        return r.isEmpty() ? null : r.get(0);
    }

    private static @NotNull List<String> executePowerShell(String command) {
        List<String> lines = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-Command", command).start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String l;
                while ((l = r.readLine()) != null) {
                    String trimmed = l.trim();
                    if (!trimmed.isEmpty()) lines.add(trimmed);
                }
            }
        } catch (Exception ignored) {}
        return lines;
    }

    public static @NotNull String getMinecraftStartTime() {
        String s = "Get-Process | Where-Object { $_.MainWindowTitle -match 'Minecraft' } | Select-Object -ExpandProperty StartTime | Get-Date -Format 'yyyy-MM-ddTHH:mm:ssK'";
        List<String> r = executePowerShell(s);
        if (!r.isEmpty()) {
            try {
                return OffsetDateTime.parse(r.get(0)).atZoneSameInstant(ZoneId.systemDefault()).format(UI_FORMATTER);
            } catch (Exception ignored) {}
        }
        return "Не запущен";
    }
}