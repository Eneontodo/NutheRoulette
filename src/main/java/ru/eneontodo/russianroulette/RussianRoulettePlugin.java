package ru.eneontodo.russianroulette;

import org.bukkit.plugin.java.JavaPlugin;

public class RussianRoulettePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private GameManager gameManager;
    private LobbyManager lobbyManager;
    private LocalizationManager localizationManager;

    @Override
    public void onEnable() {
        getLogger().info("The 'Russian Roulette' plugin is enabled!");// Plugin startup message
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.localizationManager = new LocalizationManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.gameManager = new GameManager(this);

        getCommand("roulette").setExecutor(new CommandHandler(this));// Main command
        getCommand("roulette").setTabCompleter(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("The 'Russian Roulette' plugin is disabled.");// Plugin shutdown message
    }

    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
}