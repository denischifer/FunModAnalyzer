package dev.denischifer.core.analyzer;

import dev.denischifer.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModAnalyzer {

    private static final Set<String> SUSPICIOUS_PATTERNS = new HashSet<>(Arrays.asList(
            "AimAssist", "AnchorTweaks", "AutoAnchor", "AutoCrystal", "AutoDoubleHand",
            "AutoHitCrystal", "AutoPot", "AutoTotem", "AutoArmor", "InventoryTotem",
            "JumpReset", "LegitTotem", "PingSpoof", "SelfDestruct", "ShieldBreaker",
            "TriggerBot", "AxeSpam", "WebMacro", "FastPlace", "WalskyOptimizer",
            "WalksyOptimizer", "walsky.optimizer", "WalksyCrystalOptimizerMod", "Donut",
            "Replace Mod", "ShieldDisabler", "SilentAim", "Totem Hit", "Wtap", "FakeLag",
            "BlockESP", "dev.krypton", "Virgin", "AntiMissClick", "LagReach", "PopSwitch",
            "SprintReset", "ChestSteal", "AntiBot", "ElytraSwap", "FastXP", "FastExp",
            "Refill", "NoJumpDelay", "AirAnchor", "jnativehook", "FakeInv", "HoverTotem",
            "AutoClicker", "AutoFirework", "PackSpoof", "Antiknockback", "catlean",
            "Argon", "AuthBypass", "Asteria", "Prestige", "AutoEat", "AutoMine",
            "MaceSwap", "DoubleAnchor", "AutoTPA", "BaseFinder", "Xenon", "gypsy",
            "Grim", "grim", "org.chainlibs.module.impl.modules.Crystal",
            "org.chainlibs.module.impl.modules.Blatant", "imgui", "imgui.gl3",
            "imgui.glfw", "BowAim", "Criticals", "Fakenick", "FakeItem", "invsee",
            "ItemExploit", "Hellion", "hellion", "LicenseCheckMixin",
            "ClientPlayerInteractionManagerAccessor", "ClientPlayerEntityMixim",
            "dev.gambleclient", "obfuscatedAuth", "phantom-refmap.json", "xyz.greaj",
            "じ.class", "ふ.class", "ぶ.class", "ぷ.class", "た.class", "ね.class",
            "そ.class", "な.class", "ど.class", "ぐ.class", "ず.class", "де.class",
            "つ.class", "be.class", "se.class", "to.class", "mi.class", "bi.class",
            "su.class", "no.class"
    ));

    private static final Set<String> CHEAT_STRINGS = new HashSet<>(Arrays.asList(
            "AutoCrystal", "autocrystal", "auto crystal", "cw crystal", "dontPlaceCrystal",
            "dontBreakCrystal", "AutoHitCrystal", "autohitcrystal", "canPlaceCrystalServer",
            "healPotSlot", "AutoAnchor", "autoanchor", "auto anchor", "DoubleAnchor",
            "hasGlowstone", "HasAnchor", "anchortweaks", "anchor macro", "safe anchor",
            "safeanchor", "AutoTotem", "autototem", "auto totem", "InventoryTotem",
            "inventorytotem", "HoverTotem", "hover totem", "legittotem", "AutoPot",
            "autopot", "auto pot", "speedPotSlot", "strengthPotSlot", "AutoArmor",
            "autoarmor", "auto armor", "preventSwordBlockBreaking", "preventSwordBlockAttack",
            "AutoDoubleHand", "autodoublehand", "auto double hand", "AutoClicker",
            "Failed to switch to mace after axe!", "Breaking shield with axe...", "Donut",
            "JumpReset", "axespam", "axe spam", "shieldbreaker", "shield breaker",
            "EndCrystalItemMixin", "findKnockbackSword", "attackRegisteredThisClick",
            "AimAssist", "aimassist", "aim assist", "triggerbot", "trigger bot", "FakeInv",
            "Friends", "swapBackToOriginalSlot", "FakeLag", "pingspoof", "ping spoof",
            "webmacro", "web macro", "lvstrng", "dqrkis", "selfdestruct", "self destruct",
            "AutoMace", "AutoFirework", "MaceSwap", "AirAnchor", "ElytraSwap", "FastXP",
            "FastExp", "NoJumpDelay", "PackSpoof", "Antiknockback", "catlean", "AuthBypass",
            "obfuscatedAuth", "LicenseCheckMixin", "BaseFinder", "invsee", "ItemExploit",
            "NoFall", "nofall", "WalksyCrystalOptimizerMod", "WalksyOptimizer", "WalskyOptimizer",
            "autoCrystalPlaceClock", "setBlockBreakingCooldown", "getBlockBreakingCooldown",
            "blockBreakingCooldown", "onBlockBreaking", "setItemUseCooldown", "setSelectedSlot",
            "invokeDoAttack", "invokeDoItemUse", "invokeOnMouseButton", "onTickMovement",
            "onPushOutOfBlocks", "onIsGlowing", "Automatically switches to sword when hitting with totem",
            "arrayOfString", "POT_CHEATS", "Dqrkis Client", "Entity.isGlowing",
            "ＡｕｔｏＣｒｙｓｔａｌ", "Ａｕｔｏ Ｃｒｙｓｔａｌ", "ＡｕｔｏＡｎｃｈｏｒ", "Ａｕｔｏ Ａｎｃｈｏｒ",
            "ＤｏｕｂｌｅＡｎｃｈｏｒ", "ＳａｆｅＡｎｃｈｏｒ", "ＡｕｔｏＴｏｔｅｍ", "Ａｕｔｏ Ｔｏｔｅｍ",
            "ＨｏｖｅｒＴｏｔｅｍ", "ＩｎｖｅｎｔｏｒｙＴｏｔｅｍ",
            "Blatant", "Ｂｌａｔａｎｔ", "Force Totem", "Ｆｏｒｃｅ Ｔｏｔｅｍ", "Stay Open For",
            "Ｓｔａｙ Ｏｐｅｎ Ｆｏｒ", "Auto Inventory Totem", "Ａｕｔｏ Ｉｎｖｅｎｔｏｒｙ Ｔｏｔｅｍ",
            "Only On Pop", "Ｏｎｌｙ Ｏｎ Ｐｏｐ", "Vertical Speed", "Ｖｅｒｔｉｃａｌ Ｓｐｅｅｄ",
            "Hover Totem", "Ｈｏｖｅｒ Ｔｏｔｅｍ", "Swap Speed", "Ｓｗａｐ Ｓｐｅｅｄ",
            "Strict One-Tick", "Ｓｔｒｉｃｔ Ｏｎｅ－Ｔｉｃｋ", "Mace Priority", "Ｍａｃｅ Ｐｒｉｏｒｉｔｙ",
            "Min Totems", "Ｍｉｎ Ｔｏｔｅｍｓ", "Min Pearls", "Ｍｉｎ Ｐｅａｒｌｓ", "Totem First",
            "Ｔｏｔｅｍ Ｆｉｒｓｔ", "Drop Interval", "Ｄｒｏｐ Ｉｎｔｅｒｖａｌ", "Random Pattern",
            "Ｒａｎｄｏｍ Ｐａｔｔｅｒｎ", "Loot Yeeter", "Ｌｏｏｔ Ｙｅｅｔｅｒ", "Horizontal Aim Speed",
            "Ｈｏｒｉｚｏｎｔａｌ Ａｉｍ Ｓｐｅｅｄ", "Vertical Aim Speed", "Ｖｅｒｔｉｃａｌ Ａｉｍ Ｓｐｅｅｄ",
            "Include Head", "Ｉｎｃｌｕｄｅ Ｈｅａｄ", "Web Delay", "Ｗｅｂ Ｄｅｌａｙ", "Holding Web",
            "Ｈｏｌｄｉｎｇ Ｗｅｂ", "Not When Affects Player", "Ｎｏｔ Ｗｈｅｎ Ａｆｆｅｃｔｓ Ｐｌａｙｅｒ",
            "Hit Delay", "Ｈｉｔ Ｄｅｌａｙ", "Switch Back", "Ｓｗｉｔｃｈ Ｂａｃｋ", "Require Hold Axe",
            "Ｒｅｑｕｉｒｅ Ｈｏｌｄ Ａｘｅ", "Fake Punch", "Ｆａｋｅ Ｐｕｎｃｈ", "placeInterval",
            "breakInterval", "stopOnKill", "activateOnRightClick", "holdCrystal",
            "ｐｌａｃｅＩｎｔｅｒｖａｌ", "ｂｒｅａｋＩｎｔｅｒｖａｌ", "ｓｔｏｐＯｎＫｉｌｌ",
            "ａｃｔｉｖａｔｅＯｎＲｉｇｈｔＣｌｉｃｋ", "ｄａｍａｇｅｔｉｃｋ", "ｈｏｌｄＣｒｙｓｔａｌ",
            "ｆａｋｅＰｕｎｃｈ", "Ｒｅｆｉｌｌｓ ｙｏｕｒ ｈｏｔｂａｒ ｗｉｔｈ ｐｏｔｉｏｎｓ",
            "Ｋｅｐｓ ｙｏｕ ｓｐｒｉｎｔｉｎｇ ａｔ ａｌｌ ｔｉｍｅｓ", "Macro Key", "Ａｕｔｏ Ｐｏｔ",
            "Ｍａｃｒｏ Ｋｅｙ", "Ｐｌａｃｅｓ ａｎｃｈｏｒ， ｃｈａｒｇｅｓ ｉｔ， ｐｒｏｔｅｃｔｓ ｙｏｕ， ａｎｄ ｅｘｐｌｏｄｅｓ",
            "Ａｕｔｏ ｓｗａｐ ｔｏ ｓｐｅａｒ ｏｎ ａｔｔａｃｋ"
    ));

    private static final List<String> WHITELISTED_MODS = Arrays.asList(
            "vmp-fabric", "vmp", "lithium", "sodium", "iris", "fabric-api", "modmenu",
            "ferrite-core", "lazydfu", "starlight", "entityculling", "immediatelyfast"
    );

    private static final Pattern HOST_URL_PATTERN = Pattern.compile("HostUrl=(.+)");

    public static List<String> getHeuristicLogs(@NotNull Path file) {
        List<String> logs = new ArrayList<>();
        try (InputStream is = Files.newInputStream(file)) {
            byte[] jarBytes = readAllBytesFromStream(is);
            logs.addAll(processJarBytes(jarBytes, "", logs, true));
        } catch (Exception ignored) {}
        return logs;
    }

    private static List<String> processJarBytes(byte[] bytes, String prefix, List<String> logs, boolean isRoot) {
        int classes = 0;
        int obf = 0;
        int num = 0;
        int uni = 0;
        int ultraShort = 0;
        String modId = null;

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                if (name.equals("fabric.mod.json") && isRoot) {
                    modId = extractModId(zis);
                }

                if (name.endsWith(".jar") && name.contains("META-INF/jars/")) {
                    processJarBytes(readAllBytesFromStream(zis), name + " -> ", logs, false);
                    continue;
                }

                for (String pattern : SUSPICIOUS_PATTERNS) {
                    if (name.contains(pattern)) {
                        logs.add(prefix + "Suspicious entry: " + name);
                    }
                }

                if (name.endsWith(".class")) {
                    classes++;
                    String className = name.substring(name.lastIndexOf('/') + 1, name.lastIndexOf('.'));

                    if (className.length() <= 2) ultraShort++;
                    if (className.matches("\\d+")) num++;
                    if (className.matches(".*[^\\x00-\\x7F].*")) uni++;
                    if (isObfuscatedPath(name)) obf++;

                    byte[] classBytes = readAllBytesFromStream(zis);
                    String contentIso = new String(classBytes, StandardCharsets.ISO_8859_1);
                    String contentUtf8 = new String(classBytes, StandardCharsets.UTF_8);

                    checkContent(contentIso, name, prefix, logs);
                    checkContent(contentUtf8, name, prefix, logs);
                    checkBehaviors(contentIso, name, prefix, logs);
                }
            }
        } catch (Exception ignored) {}

        if (classes > 5) {
            double obfPct = (double) obf / classes;
            if (obfPct > 0.25) logs.add(prefix + "High obfuscation rate: " + (int) (obfPct * 100) + "%");
            if (num > 0) logs.add(prefix + "Numeric classes found: " + num);
            if (uni > 0) logs.add(prefix + "Unicode classes found: " + uni);
            if (ultraShort > 0) logs.add(prefix + "Ultra-short class names: " + ultraShort);
        }

        if (modId != null && WHITELISTED_MODS.contains(modId) && obf > 0) {
            logs.add("CRITICAL: Fake mod identity - " + modId + " contains obfuscated code!");
        }

        return logs;
    }

    private static void checkContent(String content, String name, String prefix, List<String> logs) {
        for (String cheat : CHEAT_STRINGS) {
            if (content.contains(cheat)) {
                logs.add(prefix + "Cheat string in " + name + ": " + cheat);
            }
        }
    }

    private static void checkBehaviors(String content, String name, String prefix, List<String> logs) {
        if (content.contains("java/lang/Runtime") && content.contains("exec")) {
            logs.add(prefix + "Dangerous behavior in " + name + ": Runtime.exec()");
        }
        if (content.contains("HttpURLConnection") && content.contains("FileOutputStream")) {
            logs.add(prefix + "Dangerous behavior in " + name + ": Remote file download");
        }
        if (content.contains("setDoOutput") && content.contains("getOutputStream") && content.contains("getProperty")) {
            logs.add(prefix + "Dangerous behavior in " + name + ": Data exfiltration (POST)");
        }
        if (content.contains("java/lang/System") && (content.contains("loadLibrary") || content.contains("load"))) {
            logs.add(prefix + "Native library loading in " + name + ": System.loadLibrary/load");
        }
        if (content.contains("getDeclaredField") || content.contains("getDeclaredMethod") || content.contains("setAccessible")) {
            logs.add(prefix + "Reflection usage in " + name + ": Accessing private members");
        }
    }

    private static boolean isObfuscatedPath(String path) {
        String[] parts = path.split("/");
        int singleLetterParts = 0;
        for (String part : parts) {
            if (part.length() == 1) singleLetterParts++;
        }
        return singleLetterParts >= 2;
    }

    private static String extractModId(InputStream is) {
        try {
            String content = new String(readAllBytesFromStream(is), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(content);
            if (m.find()) return m.group(1);
        } catch (Exception ignored) {}
        return null;
    }

    private static byte[] readAllBytesFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static @Nullable String getDownloadSource(@NotNull Path file) {
        try {
            Path zonePath = Paths.get(file.toAbsolutePath() + ":Zone.Identifier");
            if (Files.exists(zonePath)) {
                String content = new String(Files.readAllBytes(zonePath), StandardCharsets.ISO_8859_1);

                Matcher matcher = Pattern.compile("HostUrl=(.*)", Pattern.CASE_INSENSITIVE).matcher(content);
                if (matcher.find()) {
                    String url = matcher.group(1).trim().toLowerCase();

                    if (url.contains("mediafire.com")) return "MediaFire";
                    if (url.contains("discord.com") || url.contains("discordapp.com")) return "Discord";
                    if (url.contains("dropbox.com")) return "Dropbox";
                    if (url.contains("drive.google.com")) return "Google Drive";
                    if (url.contains("mega.nz") || url.contains("mega.co.nz")) return "MEGA";
                    if (url.contains("github.com")) return "GitHub";
                    if (url.contains("modrinth.com")) return "Modrinth";
                    if (url.contains("curseforge.com")) return "CurseForge";
                    if (url.contains("anydesk.com")) return "AnyDesk";

                    if (url.contains("doomsdayclient.com")) return "DoomsdayClient";
                    if (url.contains("prestigeclient.vip")) return "PrestigeClient";
                    if (url.contains("198macros.com")) return "198Macros";
                    if (url.contains("dqrkis.xyz")) return "Dqrkis";

                    Matcher domainMatcher = Pattern.compile("https?://(?:www\\.)?([^/]+)").matcher(url);
                    if (domainMatcher.find()) {
                        return domainMatcher.group(1);
                    }

                    return url;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean checkModrinthApi(@NotNull Path file) {
        HttpURLConnection conn = null;
        try {
            String sha1 = FileUtil.getSha1Hash(file);
            URL url = new URL("https://api.modrinth.com/v2/version_file/" + sha1 + "?algorithm=sha1");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "FunModAnalyzer/1.1");
            conn.setConnectTimeout(3000);
            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    return reader.readLine().contains("\"project_id\"");
                }
            }
        } catch (Exception ignored) {} finally {
            if (conn != null) conn.disconnect();
        }
        return false;
    }
}