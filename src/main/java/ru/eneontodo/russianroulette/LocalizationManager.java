package ru.eneontodo.russianroulette;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> messages = new HashMap<>();
    private final Map<String, String> playerLanguages = new HashMap<>();

    public LocalizationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }
    // TODO: add new languages here

    public void reload() {
        messages.clear();
        loadMessages("ru_RU", "locale/messages_ru.yml"); // Russian
        loadMessages("en_US", "locale/messages_en.yml"); // English
    }

    private void loadMessages(String key, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        messages.put(key, YamlConfiguration.loadConfiguration(file));
    }

    public String get(String lang, String key) {
        FileConfiguration config = messages.get(lang);
        if (config == null) {
            config = messages.get("ru_RU"); // Default to Russian
        }
        if (config == null) {
            return "&c[No translation: " + key + "]";
        }
        return config.getString(key, "&c[No translation: " + key + "]");
    }

    public String formatWithPlaceholders(String lang, String key, Map<String, String> placeholders) {
        String message = get(lang, key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void setPlayerLanguage(String playerName, String lang) {
        playerLanguages.put(playerName, lang);
    }

    public String getPlayerLanguage(String playerName) {
        return playerLanguages.getOrDefault(playerName, "ru_RU");
    }

    public String tr(String playerName, String key) {
        return formatWithPlaceholders(getPlayerLanguage(playerName), key, new HashMap<>());
    }

    public String trp(String playerName, String key, Map<String, String> placeholders) {
        return formatWithPlaceholders(getPlayerLanguage(playerName), key, placeholders);
    }
}
