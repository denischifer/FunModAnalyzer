package dev.denischifer.core.analyzer;

import dev.denischifer.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModAnalyzer {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final Set<String> SUSPICIOUS_PATTERNS = Set.of(
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
            "そ.class", "な.class", "ど.class", "ぐ.class", "ず.class", "で.class",
            "つ.class", "べ.class", "せ.class", "と.class", "み.class", "び.class",
            "す.class", "の.class"
    );

    private static final Set<String> CHEAT_STRINGS = Set.of(
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
            "arrayOfString", "POT_CHEATS", "Dqrkis Client", "Entity.isGlowing"
    );

    private static final Pattern HOST_URL_PATTERN = Pattern.compile("HostUrl=(.+)");

    public static boolean checkModrinthApi(@NotNull Path file) {
        try {
            String sha1 = FileUtil.getSha1Hash(file);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.modrinth.com/v2/version_file/" + sha1 + "?algorithm=sha1"))
                    .header("User-Agent", "FunModAnalyzer/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("\"project_id\"");
        } catch (Exception e) {
            return false;
        }
    }

    public static List<String> getHeuristicLogs(@NotNull Path file) {
        List<String> logs = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                for (String pattern : SUSPICIOUS_PATTERNS) {
                    if (name.contains(pattern)) {
                        logs.add("Suspicious file/entry name: " + name + " (Match: " + pattern + ")");
                    }
                }

                if (name.endsWith(".class")) {
                    byte[] bytes = zis.readAllBytes();
                    String content = new String(bytes, StandardCharsets.ISO_8859_1);
                    for (String cheat : CHEAT_STRINGS) {
                        if (content.contains(cheat)) {
                            logs.add("Suspicious string in " + name + ": " + cheat);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return logs;
    }

    public static @Nullable String getDownloadSource(@NotNull Path file) {
        try {
            Path zonePath = Path.of(file + ":Zone.Identifier");
            if (Files.exists(zonePath)) {
                String content = Files.readString(zonePath);
                Matcher matcher = HOST_URL_PATTERN.matcher(content);
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
                    return matcher.group(1).trim();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean scanForCheats(@NotNull Path file) {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                for (String pattern : SUSPICIOUS_PATTERNS) {
                    if (name.contains(pattern)) {
                        return true;
                    }
                }

                if (name.endsWith(".class")) {
                    byte[] bytes = zis.readAllBytes();
                    String content = new String(bytes, StandardCharsets.ISO_8859_1);
                    for (String cheat : CHEAT_STRINGS) {
                        if (content.contains(cheat)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}