package ru.eneontodo.russianroulette;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> messages = new HashMap<>();
    private final Map<String, String> playerLanguages = new HashMap<>();

    public LocalizationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessages("ru_RU", "locale/messages_ru.yml"); // Russian
        loadMessages("en_US", "locale/messages_en.yml"); // English
    }
    // TODO: 
    // add new languages

    private void loadMessages(String key, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        messages.put(key, YamlConfiguration.loadConfiguration(file));
    }

    public String get(String lang, String key) {
        FileConfiguration config = messages.get(lang);
        if (config == null) config = messages.get("ru_RU"); // Default to Russian
        return config.getString(key, "&c[No translation: " + key + "]");
    }

    public String format(String lang, String key, Object... args) {
        String message = get(lang, key);
        for (int i = 0; i < args.length; i++) {
            message = message.replace("%arg" + (i + 1) + "%", String.valueOf(args[i]));
        }
        message = message.replace("%player%", "{player}") // Standardize placeholders
                         .replace("%round%", "{round}")
                         .replace("%slots%", "{slots}")
                         .replace("%winner%", "{winner}")
                         .replace("%id%", "{id}")
                         .replace("%count%", "{count}")
                         .replace("%max%", "{max}")
                         .replace("%status%", "{status}");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String formatWithPlaceholders(String lang, String key, Map<String, String> placeholders) {
        String message = get(lang, key);
        message = ChatColor.translateAlternateColorCodes('&', message);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
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