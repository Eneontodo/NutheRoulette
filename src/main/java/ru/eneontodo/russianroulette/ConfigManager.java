package ru.eneontodo.russianroulette;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final RussianRoulettePlugin plugin;
    private FileConfiguration config;

    public ConfigManager(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
        setupDefaults();
    }

    private void setupDefaults() {
        this.config = plugin.getConfig();
        config.addDefault("chamberSize", 6);
        config.addDefault("bulletCount", 1);
        config.addDefault("maxPlayers", 4);
        config.addDefault("roundDelayTicks", 60L);
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        setupDefaults();
    }

    public int getChamberSize() {
        return Math.max(1, config.getInt("chamberSize", 6));
    }

    public int getBulletCount() {
        // At least one bullet, otherwise nobody could ever lose and the game never ends.
        return Math.max(1, config.getInt("bulletCount", 1));
    }

    public int getMaxPlayers() {
        // At least two players are required for a meaningful game.
        return Math.max(2, config.getInt("maxPlayers", 4));
    }

    public long getRoundDelayTicks() {
        return Math.max(1L, config.getLong("roundDelayTicks", 60L));
    }
}
