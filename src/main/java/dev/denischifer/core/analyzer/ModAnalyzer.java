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
            "Ｈｏｌｄｉｎｇ Ｗｅｂ", "Not When Affects Player", "Ｎｏｔ Ｗｈｅｎ Ａｆфects Player",
            "Hit Delay", "Ｈｉｔ Ｄｅｌａｙ", "Switch Back", "Ｓｗｉｔｃｈ Ｂａｃｋ", "Require Hold Axe",
            "Ｒｅｑｕｉｒｅ Ｈｏｌｄ Ａｘｅ", "Fake Punch", "Ｆａｋｅ Ｐｕｎｃｈ", "placeInterval",
            "breakInterval", "stopOnKill", "activateOnRightClick", "holdCrystal",
            "ｐｌａｃｅＩｎｔｅｒｖａｌ", "ｂｒｅａｋＩｎｔｅｒｖａｌ", "ｓｔｏｐＯｎＫｉｌｌ",
            "ａｃｔｉｖａｔｅＯｎＲｉｇｈｔＣｌｉｃｋ", "ｄａｍａｇｅｔｉｃｋ", "ｈｏｌｄＣｒｙｓｔａｌ",
            "ｆａｋｅＰｕｎｃｈ", "Ｒｅｆｉｌｌｓ ｙｏｕｒ ｈｏｔｂａｒ ｗｉｔｈ ｐｏｔｉｏｎｓ",
            "Ｋｅｐｓ ｙｏｕ ｓｐｒｉｎｔｉｎｇ ａｔ ａｌｌ ｔｉｍｅｓ", "Macro Key", "Ａｕｔｏ Ｐｏｔ",
            "Ｍａｃｒｏ Ｋｅｙ", "Ｐｌａｃｅｓ ａｎｃｈｏｒ， ｃｈａｒｇｅｓ ｉｔ， ｐｒоtеcts you, and explodes",
            "Ａｕｔｏ ｓｗａｐ ｔｏ ｓｐｅａｒ ｏｎ ａｔｔａｃｋ"
    ));

    private static final List<String> WHITELISTED_MODS = Arrays.asList(
            "vmp-fabric", "vmp", "lithium", "sodium", "iris", "fabric-api", "modmenu",
            "ferrite-core", "lazydfu", "starlight", "entityculling", "immediatelyfast"
    );

    public static List<ModInfo.AnalysisEntry> getHeuristicResults(@NotNull Path file) {
        List<ModInfo.AnalysisEntry> results = new ArrayList<>();
        try (InputStream is = Files.newInputStream(file)) {
            byte[] jarBytes = readAllBytesFromStream(is);
            results.addAll(processJarBytes(jarBytes, "", true));
        } catch (Exception ignored) {}
        return results;
    }

    private static List<ModInfo.AnalysisEntry> processJarBytes(byte[] bytes, String prefix, boolean isRoot) {
        List<ModInfo.AnalysisEntry> results = new ArrayList<>();
        int classes = 0, obf = 0, num = 0, uni = 0, ultraShort = 0;
        String modId = null;

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.equals("fabric.mod.json") && isRoot) modId = extractModId(zis);
                if (name.endsWith(".jar") && name.contains("META-INF/jars/")) {
                    results.addAll(processJarBytes(readAllBytesFromStream(zis), name + " -> ", false));
                    continue;
                }

                for (String pattern : SUSPICIOUS_PATTERNS) {
                    if (name.contains(pattern)) {
                        results.add(createEntry(name, "Suspicious Path", pattern));
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
                    checkContent(classBytes, name, results);
                    checkBehaviors(new String(classBytes, StandardCharsets.ISO_8859_1), name, results);
                }
            }
        } catch (Exception ignored) {}

        if (classes > 5) {
            if ((double) obf / classes > 0.25) results.add(createEntry(prefix, "Obfuscation", "Rate: " + (int)((double)obf/classes*100) + "%"));
            if (num > 0) results.add(createEntry(prefix, "Obfuscation", "Numeric classes: " + num));
            if (uni > 0) results.add(createEntry(prefix, "Obfuscation", "Unicode classes: " + uni));
            if (ultraShort > 0) results.add(createEntry(prefix, "Obfuscation", "Short names: " + ultraShort));
        }
        if (modId != null && WHITELISTED_MODS.contains(modId) && obf > 0) {
            results.add(createEntry(modId, "CRITICAL", "Fake mod identity detected"));
        }
        return results;
    }

    private static void checkContent(byte[] bytes, String name, List<ModInfo.AnalysisEntry> results) {
        String iso = new String(bytes, StandardCharsets.ISO_8859_1);
        String utf8 = new String(bytes, StandardCharsets.UTF_8);
        for (String cheat : CHEAT_STRINGS) {
            if (iso.contains(cheat) || utf8.contains(cheat)) {
                results.add(createEntry(name, "Cheat String", cheat));
            }
        }
    }

    private static void checkBehaviors(String content, String name, List<ModInfo.AnalysisEntry> results) {
        if (content.contains("java/lang/Runtime") && content.contains("exec"))
            results.add(createEntry(name, "Dangerous Behavior", "Runtime.exec()"));
        if (content.contains("HttpURLConnection") && content.contains("FileOutputStream"))
            results.add(createEntry(name, "Dangerous Behavior", "Remote Download"));
        if (content.contains("setDoOutput") && content.contains("getOutputStream") && content.contains("getProperty"))
            results.add(createEntry(name, "Dangerous Behavior", "Data Exfiltration"));
        if (content.contains("java/lang/System") && (content.contains("loadLibrary") || content.contains("load")))
            results.add(createEntry(name, "Native Loading", "System.load"));
        if (content.contains("getDeclaredField") || content.contains("getDeclaredMethod") || content.contains("setAccessible"))
            results.add(createEntry(name, "Reflection", "Private Access"));
    }

    private static ModInfo.AnalysisEntry createEntry(String clazz, String type, String detail) {
        return ModInfo.AnalysisEntry.builder().className(clazz).type(type).detail(detail).build();
    }

    private static boolean isObfuscatedPath(String path) {
        String[] parts = path.split("/");
        int s = 0;
        for (String p : parts) if (p.length() == 1) s++;
        return s >= 2;
    }

    private static String extractModId(InputStream is) {
        try {
            String c = new String(readAllBytesFromStream(is), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(c);
            if (m.find()) return m.group(1);
        } catch (Exception ignored) {}
        return null;
    }

    private static byte[] readAllBytesFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        int n; byte[] d = new byte[16384];
        while ((n = is.read(d, 0, d.length)) != -1) b.write(d, 0, n);
        return b.toByteArray();
    }

    public static @Nullable String getDownloadSource(@NotNull Path file) {
        try {
            Path z = Paths.get(file.toAbsolutePath() + ":Zone.Identifier");
            if (Files.exists(z)) {
                String c = new String(Files.readAllBytes(z), StandardCharsets.ISO_8859_1);
                Matcher m = Pattern.compile("HostUrl=(.*)", Pattern.CASE_INSENSITIVE).matcher(c);
                if (m.find()) {
                    String u = m.group(1).trim().toLowerCase();
                    String[] hosts = {"mediafire.com", "discord", "dropbox.com", "google.com", "mega.nz", "github.com", "modrinth.com", "curseforge.com", "anydesk.com", "doomsdayclient.com", "prestigeclient.vip", "198macros.com", "dqrkis.xyz"};
                    for (String h : hosts) if (u.contains(h)) return h;
                    Matcher dm = Pattern.compile("https?://(?:www\\.)?([^/]+)").matcher(u);
                    if (dm.find()) return dm.group(1);
                    return u;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean checkModrinthApi(@NotNull Path file) {
        HttpURLConnection c = null;
        try {
            URL u = new URL("https://api.modrinth.com/v2/version_file/" + FileUtil.getSha1Hash(file) + "?algorithm=sha1");
            c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("User-Agent", "FunModAnalyzer/1.1");
            c.setConnectTimeout(3000);
            if (c.getResponseCode() == 200) {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
                    return r.readLine().contains("\"project_id\"");
                }
            }
        } catch (Exception ignored) {} finally { if (c != null) c.disconnect(); }
        return false;
    }
}