package ru.eneontodo.russianroulette;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final RussianRoulettePlugin plugin;

    public CommandHandler(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { // Ensure only players can execute commands
            sender.sendMessage("This command can only be executed by the player.");
            return true;
        }

        Player player = (Player) sender;
        LobbyManager lobbyManager = plugin.getLobbyManager();

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "usage: /roulette <join|leave|start|list|lang>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // TODO: add lang command

        switch (subCommand) {
            case "join" -> { // Join an available lobby
                Lobby currentLobby = lobbyManager.getLobbyByPlayer(player);
                if (currentLobby != null) {
                    player.sendMessage(ChatColor.RED + "You are already in the lobby #" + currentLobby.getId() + ".");
                    return true;    
                }
                Lobby availableLobby = lobbyManager.findAvailableLobby(); // Find or create a lobby with available slots
                if (availableLobby.addPlayer(player)) {
                    player.sendMessage(ChatColor.GREEN + "You have entered the lobby #" + availableLobby.getId() + ". Places remaining:" + availableLobby.getAvailableSlots());
                    for (Player p : availableLobby.getPlayers()) { // Notify other players in the lobby
                        if (p != player) {
                            p.sendMessage(ChatColor.GRAY + player.getName() + " joined the lobby.");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to enter the lobby.");
                }
            }

            case "leave" -> { // Leave the player's current lobby
                Lobby playerLobby = lobbyManager.getLobbyByPlayer(player);
                if (playerLobby == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getNoLobbyMessage()));
                    return true;
                }
                lobbyManager.removePlayerFromLobby(player);
                player.sendMessage(ChatColor.GREEN + "You have left the lobby #" + playerLobby.getId() + ".");
                for (Player p : playerLobby.getPlayers()) {
                    p.sendMessage(ChatColor.GRAY + player.getName() + "left the lobby.");
                }
            }

            case "start" -> { // Start the game in the player's lobby
                Lobby lobbyToStart = lobbyManager.getLobbyByPlayer(player);
                if (lobbyToStart == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getNoLobbyMessage()));
                    return true;
                }
                if (!lobbyToStart.getPlayers().get(0).equals(player)) {
                    player.sendMessage(ChatColor.RED + "Only the lobby leader can start the game.");
                    return true;
                }
                if (lobbyToStart.getPlayers().size() < 1) {
                    player.sendMessage(ChatColor.RED + "Not enough players to start the game.");
                    return true;
                }
                lobbyToStart.startGame();
            }

            case "list" -> { // List all active lobbies
                List<Lobby> allLobbies = lobbyManager.getLobbies();
                player.sendMessage(ChatColor.YELLOW + "=== List of active lobbies ===");
                for (Lobby l : allLobbies) {
                    player.sendMessage(ChatColor.YELLOW + "ID: " + l.getId() +
                            ", Players: " + l.getPlayers().size() + "/" + plugin.getConfigManager().getMaxPlayers() +
                            ", Game: " + (l.isGameStarted() ? "Yes" : "No"));
                }
            }
            case "lang"-> { // Language command
                handleLangCommand(player, args);
                return true;
            }
            default -> player.sendMessage(ChatColor.RED + "Unknown subcommand. Use: join, leave, start, list, lang.");
        }

        return true;
    }

    private void handleLangCommand(Player player, String[] args) { 
    if (args.length < 2) {
        player.sendMessage(ChatColor.GOLD + "Available languages: /roulette lang ru — Russian, /roulette lang en — English");
        return;
    }

    String langKey = args[1].toLowerCase();
    LocalizationManager loc = plugin.getLocalizationManager();

    if (langKey.equals("ru")) {
        loc.setPlayerLanguage(player.getName(), "ru_RU");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLanguage changed to Russian!"));
    } else if (langKey.equals("en")) {
        loc.setPlayerLanguage(player.getName(), "en_US");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLanguage changed to English!"));
    } else {
        player.sendMessage(ChatColor.GOLD + "Available languages: /roulette lang ru — Russian, /roulette lang en — English");
        return;
    }
}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { 
        if (args.length == 1) {
            List<String> completions = Arrays.asList("join", "leave", "start", "list", "lang");
            return completions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && "lang".equalsIgnoreCase(args[0])) {
            return Arrays.asList("ru", "en").stream()
                    .filter(lang -> lang.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }   
        return new ArrayList<>();
    }
}