package dev.denischifer.util;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class FileUtil {

    public static @NotNull String getSha1Hash(@NotNull Path path) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(path);
        byte[] hash = MessageDigest.getInstance("SHA-1").digest(data);
        return HexFormat.of().formatHex(hash);
    }
}