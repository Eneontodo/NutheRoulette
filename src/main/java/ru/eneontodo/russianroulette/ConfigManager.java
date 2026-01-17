package ru.eneontodo.russianroulette;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final FileConfiguration config;

    public ConfigManager(RussianRoulettePlugin plugin) { // Initialize configuration with default values
        this.config = plugin.getConfig();
        config.addDefault("chamberSize", 6);
        config.addDefault("bulletCount", 1);
        config.addDefault("startGameMessage", "&aLet's start playing Russian roulette!");
        config.addDefault("spinningDrumMessage", "&e%player% spins the drum...");
        config.addDefault("safeMessage", "&aClick... You're lucky, you survived!");
        config.addDefault("shotMessage", "&cBANG! You lost...");
        config.addDefault("noLobbyMessage", "&cYou are not in any lobby.");
        config.addDefault("notYourTurnMessage", "&cIt's not your turn.");
        config.addDefault("gameAlreadyActiveMessage", "&cGame is already active!");
        config.addDefault("maxPlayers", 4);
        config.addDefault("roundDelayTicks", 60L);
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public int getChamberSize() {
        return config.getInt("chamberSize", 6);
    }

    public int getBulletCount() {
        return config.getInt("bulletCount", 1);
    }

    public String getStartGameMessage() {
        return config.getString("startGameMessage", "&aLet's start playing Russian roulette!");
    }

    public String getSpinningDrumMessage() {
        return config.getString("spinningDrumMessage", "&e%player% spins the drum...");
    }

    public String getSafeMessage() {
        return config.getString("safeMessage", "&aClick... You're lucky, you survived!");
    }

    public String getShotMessage() {
        return config.getString("shotMessage", "&cBANG! You lost...");
    }

    public String getNoLobbyMessage() {
        return config.getString("noLobbyMessage", "&cYou are not in any lobby.");
    }

    public String getNotYourTurnMessage() {
        return config.getString("notYourTurnMessage", "&cIt's not your turn.");
    }

    public String getGameAlreadyActiveMessage() {
        return config.getString("gameAlreadyActiveMessage", "&cGame is already active!");
    }

    public int getMaxPlayers() {
        return config.getInt("maxPlayers", 4);
    }

    public long getRoundDelayTicks() {
        return config.getLong("roundDelayTicks", 60L);
    }
}