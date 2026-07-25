package ru.eneontodo.russianroulette;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    private static final String DEFAULT_LANGUAGE = "ru_RU";

    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> messages = new HashMap<>();
    private String language = DEFAULT_LANGUAGE; // Single server-wide language

    public LocalizationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }
    // TODO: add new languages here

    public void reload() {
        messages.clear();
        loadMessages("ru_RU", "locale/messages_ru.yml"); // Russian
        loadMessages("en_US", "locale/messages_en.yml"); // English
        this.language = normalize(plugin.getConfig().getString("language", DEFAULT_LANGUAGE));
    }

    private void loadMessages(String key, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        messages.put(key, YamlConfiguration.loadConfiguration(file));
    }

    // Accept "ru", "en", "ru_RU", "en_US" (any case); fall back to the default.
    private String normalize(String lang) {
        if (lang == null) {
            return DEFAULT_LANGUAGE;
        }
        switch (lang.toLowerCase()) {
            case "ru":
            case "ru_ru":
                return "ru_RU";
            case "en":
            case "en_us":
                return "en_US";
            default:
                return messages.containsKey(lang) ? lang : DEFAULT_LANGUAGE;
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        this.language = normalize(lang);
        plugin.getConfig().set("language", this.language);
        plugin.saveConfig(); // Persist so the choice survives restarts
    }

    public String get(String key) {
        FileConfiguration config = messages.get(language);
        if (config == null) {
            config = messages.get(DEFAULT_LANGUAGE);
        }
        if (config == null) {
            return "&c[No translation: " + key + "]";
        }
        return config.getString(key, "&c[No translation: " + key + "]");
    }

    public String tr(String key) {
        return ChatColor.translateAlternateColorCodes('&', get(key));
    }

    public String trp(String key, Map<String, String> placeholders) {
        String message = get(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
