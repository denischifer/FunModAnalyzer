package dev.denischifer.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessUtil {
    private static final DateTimeFormatter UI_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            .withZone(ZoneId.systemDefault());

    public record JavaProcessInfo(int pid, String name, String windowTitle) {}

    public static @NotNull List<JavaProcessInfo> getJavaProcesses() {
        List<JavaProcessInfo> processes = new ArrayList<>();
        try {
            String script = "Get-Process | Where-Object { $_.Name -match 'java' } | Select-Object Id, ProcessName, MainWindowTitle | ConvertTo-Csv -NoTypeInformation";
            Process process = new ProcessBuilder("powershell", "-NoProfile", "-Command", script).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean headerSkipped = false;
                while ((line = reader.readLine()) != null) {
                    if (!headerSkipped) {
                        headerSkipped = true;
                        continue;
                    }
                    String[] parts = line.replace("\"", "").split(",");
                    if (parts.length >= 2) {
                        int pid = Integer.parseInt(parts[0]);
                        String title = parts.length > 2 ? parts[2] : "";
                        processes.add(new JavaProcessInfo(pid, parts[1], title));
                    }
                }
            }
        } catch (Exception ignored) {}
        return processes;
    }

    public static @Nullable String getModsPathForPid(int pid) {
        try {
            String script = "(Get-Process -Id " + pid + " -Module -ErrorAction SilentlyContinue | Where-Object FileName -like '*\\mods\\*.jar' | Select-Object -First 1).FileName";
            Process process = new ProcessBuilder("powershell", "-NoProfile", "-Command", script).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return Path.of(line.trim()).getParent().toString();
                }
            }

            String fallbackScript = "Get-WmiObject Win32_Process -Filter \"ProcessId=" + pid + "\" | Select-Object -ExpandProperty CommandLine";
            Process processFallback = new ProcessBuilder("powershell", "-NoProfile", "-Command", fallbackScript).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(processFallback.getInputStream()))) {
                String cmd = reader.readLine();
                if (cmd != null && !cmd.isEmpty()) {
                    Matcher m = Pattern.compile("--gameDir\\s+\"?([^\"]+)\"?").matcher(cmd);
                    if (m.find()) return Path.of(m.group(1), "mods").toString();

                    m = Pattern.compile("-Djava\\.library\\.path=\"?([^\"]+)\"?").matcher(cmd);
                    if (m.find()) return Path.of(m.group(1)).getParent().resolve("mods").toString();
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static @NotNull String getMinecraftStartTime() {
        try {
            String script = "Get-Process | Where-Object { " +
                    "($_.Name -match 'java') -and " +
                    "($_.MainWindowTitle -match 'Minecraft' -or $_.Path -match 'Minecraft' -or $_.Path -match 'Modrinth') " +
                    "} | Select-Object -ExpandProperty StartTime | Get-Date -Format 'yyyy-MM-ddTHH:mm:ssK'";

            Process process = new ProcessBuilder("powershell", "-NoProfile", "-Command", script).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    OffsetDateTime odt = OffsetDateTime.parse(line.trim());
                    return odt.atZoneSameInstant(ZoneId.systemDefault()).format(UI_FORMATTER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Не запущен";
    }
}