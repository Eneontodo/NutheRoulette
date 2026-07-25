package ru.eneontodo.russianroulette;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final RussianRoulettePlugin plugin;

    public CommandHandler(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { // Ensure only players can execute commands
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        LobbyManager lobbyManager = plugin.getLobbyManager();
        LocalizationManager loc = plugin.getLocalizationManager();
        String name = player.getName();

        if (args.length == 0) {
            player.sendMessage(loc.tr("usage"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "join" -> { // Join an available lobby
                Lobby currentLobby = lobbyManager.getLobbyByPlayer(player);
                if (currentLobby != null) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%id%", String.valueOf(currentLobby.getId()));
                    player.sendMessage(loc.trp("alreadyInLobby", placeholders));
                    return true;
                }
                Lobby availableLobby = lobbyManager.findAvailableLobby();
                if (availableLobby.addPlayer(player)) {
                    Map<String, String> selfPlaceholders = new HashMap<>();
                    selfPlaceholders.put("%id%", String.valueOf(availableLobby.getId()));
                    selfPlaceholders.put("%slots%", String.valueOf(availableLobby.getAvailableSlots()));
                    player.sendMessage(loc.trp("lobbyJoinedSelf", selfPlaceholders));

                    Map<String, String> broadcast = new HashMap<>();
                    broadcast.put("%player%", name);
                    broadcast.put("%slots%", String.valueOf(availableLobby.getAvailableSlots()));
                    for (Player p : availableLobby.getPlayers()) {
                        if (!p.equals(player)) {
                            p.sendMessage(loc.trp("playerJoinedLobby", broadcast));
                        }
                    }
                } else {
                    player.sendMessage(loc.tr("failedToJoin"));
                }
            }

            case "leave" -> { // Leave the player's current lobby
                Lobby playerLobby = lobbyManager.getLobbyByPlayer(player);
                if (playerLobby == null) {
                    player.sendMessage(loc.tr("noLobbyMessage"));
                    return true;
                }
                int lobbyId = playerLobby.getId();
                lobbyManager.removePlayerFromLobby(player);

                Map<String, String> selfPlaceholders = new HashMap<>();
                selfPlaceholders.put("%id%", String.valueOf(lobbyId));
                player.sendMessage(loc.trp("lobbyLeftSelf", selfPlaceholders));

                Map<String, String> broadcast = new HashMap<>();
                broadcast.put("%player%", name);
                for (Player p : playerLobby.getPlayers()) {
                    p.sendMessage(loc.trp("playerLeftLobby", broadcast));
                }
            }

            case "start" -> { // Start the game in the player's lobby
                Lobby lobbyToStart = lobbyManager.getLobbyByPlayer(player);
                if (lobbyToStart == null) {
                    player.sendMessage(loc.tr("noLobbyMessage"));
                    return true;
                }
                if (lobbyToStart.isGameStarted()) {
                    player.sendMessage(loc.tr("gameAlreadyActiveMessage"));
                    return true;
                }
                if (lobbyToStart.getPlayers().isEmpty() || !lobbyToStart.getPlayers().get(0).equals(player)) {
                    player.sendMessage(loc.tr("leaderOnlyStart"));
                    return true;
                }
                if (lobbyToStart.getPlayers().size() < 2) {
                    player.sendMessage(loc.tr("notEnoughPlayers"));
                    return true;
                }
                lobbyToStart.startGame();
            }

            case "list" -> { // List all active lobbies
                player.sendMessage(loc.tr("lobbyListHeader"));
                for (Lobby l : lobbyManager.getLobbies()) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%id%", String.valueOf(l.getId()));
                    placeholders.put("%count%", String.valueOf(l.getPlayers().size()));
                    placeholders.put("%max%", String.valueOf(l.getMaxPlayers()));
                    placeholders.put("%status%", loc.tr(l.isGameStarted() ? "gameStatusActive" : "gameStatusWaiting"));
                    player.sendMessage(loc.trp("lobbyInfo", placeholders));
                }
            }

            case "reload" -> { // Reload config and locale files
                if (!player.hasPermission("russianroulette.reload")) {
                    player.sendMessage(loc.tr("noPermission"));
                    return true;
                }
                plugin.getConfigManager().reload();
                plugin.getLocalizationManager().reload();
                player.sendMessage(loc.tr("configReloaded"));
            }

            case "lang" -> { // Language command (admin-only)
                if (!player.hasPermission("russianroulette.lang")) {
                    player.sendMessage(loc.tr("noPermission"));
                    return true;
                }
                handleLangCommand(player, args);
            }

            default -> player.sendMessage(loc.tr("unknownSubcommand"));
        }

        return true;
    }

    private void handleLangCommand(Player player, String[] args) {
        LocalizationManager loc = plugin.getLocalizationManager();

        if (args.length < 2) {
            player.sendMessage(loc.tr("availableLanguages"));
            return;
        }

        String langKey = args[1].toLowerCase();
        if (langKey.equals("ru") || langKey.equals("en")) {
            loc.setLanguage(langKey.equals("ru") ? "ru_RU" : "en_US"); // Server-wide language
            player.sendMessage(loc.tr("languageChanged"));
        } else {
            player.sendMessage(loc.tr("availableLanguages"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(Arrays.asList("join", "leave", "start", "list"));
            if (sender.hasPermission("russianroulette.lang")) {
                options.add("lang");
            }
            if (sender.hasPermission("russianroulette.reload")) {
                options.add("reload");
            }
            return options.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && "lang".equalsIgnoreCase(args[0]) && sender.hasPermission("russianroulette.lang")) {
            return Arrays.asList("ru", "en").stream()
                    .filter(lang -> lang.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
