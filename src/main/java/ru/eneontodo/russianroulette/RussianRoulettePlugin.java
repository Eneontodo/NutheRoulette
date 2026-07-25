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

        if (getCommand("roulette") != null) {
            CommandHandler handler = new CommandHandler(this); // Single instance for executor + tab completer
            getCommand("roulette").setExecutor(handler);
            getCommand("roulette").setTabCompleter(handler);
        } else {
            getLogger().warning("Command 'roulette' is not defined in plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this); // Clean up on disconnect
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
