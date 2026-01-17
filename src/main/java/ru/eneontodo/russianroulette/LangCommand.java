package ru.eneontodo.russianroulette;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LangCommand implements CommandExecutor {
    private final RussianRoulettePlugin plugin;

    public LangCommand(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can change the language.");
            return true;
        }

        Player player = (Player) sender;
        String lang = "ru_RU";

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("ru")) {
                lang = "ru_RU"; // set to Russian
            } else if (args[0].equalsIgnoreCase("en")) {
                lang = "en_US"; // set to English
            } else {
                player.sendMessage(plugin.getLocalizationManager().tr(player.getName(), "availableLanguages"));
                return true;
            }
        } else {
            player.sendMessage(plugin.getLocalizationManager().tr(player.getName(), "availableLanguages"));
            return true;
        }

        plugin.getLocalizationManager().setPlayerLanguage(player.getName(), lang); // Save player's language preference
        player.sendMessage(plugin.getLocalizationManager().tr(player.getName(), "languageChanged")); // Notify player of language change
        return true;
    }
}