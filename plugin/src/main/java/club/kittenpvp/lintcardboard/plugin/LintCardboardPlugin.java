package club.kittenpvp.lintcardboard.plugin;

import club.kittenpvp.lintcardboard.brand.ServerBrand;
import club.kittenpvp.lintcardboard.plugin.bridge.BridgeHub;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class LintCardboardPlugin extends JavaPlugin {
    private static BridgeHub bridge;

    public static BridgeHub bridge() {
        BridgeHub active = bridge;
        if (active == null) {
            throw new IllegalStateException("LintCardboard bridge is not enabled");
        }
        return active;
    }

    @Override
    public void onEnable() {
        bridge = new BridgeHub();
        String seen = getServer().getName();
        getLogger().info("LintCardboard bridge active, server brand: " + seen);
        try {
            writeEvidence(seen);
        } catch (IOException failure) {
            throw new IllegalStateException("LintCardboard plugin failed to record brand evidence", failure);
        }
    }

    @Override
    public void onDisable() {
        bridge = null;
    }

    private void writeEvidence(String seen) throws IOException {
        Properties evidence = new Properties();
        evidence.setProperty("plugin.brand", ServerBrand.NAME);
        evidence.setProperty("plugin.version", ServerBrand.VERSION);
        evidence.setProperty("plugin.brandSeen", seen);
        evidence.setProperty("plugin.bridgeActive", Boolean.toString(bridge() != null));
        Path target = getDataFolder().toPath().resolve("lintcardboard-plugin.properties");
        Files.createDirectories(target.getParent());
        try (OutputStream output = Files.newOutputStream(target)) {
            evidence.store(output, "lintcardboard-plugin");
        }
    }
}
