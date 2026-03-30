package dev.denischifer.util;

import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ProcessUtil {
    private static final DateTimeFormatter UI_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            .withZone(ZoneId.systemDefault());

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