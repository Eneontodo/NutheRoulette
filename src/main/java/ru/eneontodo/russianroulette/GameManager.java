package ru.eneontodo.russianroulette;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {
    private final RussianRoulettePlugin plugin;
    private boolean isGameActive = false;
    private List<Integer> bulletPositions = new ArrayList<>();
    private int currentChamber = 0;
    private int roundNumber = 1;
    private final Random random;

    public GameManager(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public void startGame(Lobby lobby) {
        if (isGameActive) {
            Player currentPlayer = lobby.getCurrentPlayer();
            if (currentPlayer != null) {
                currentPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getGameAlreadyActiveMessage())); // Notify current player
            }
            return;
        }
        isGameActive = true;
        currentChamber = 0;
        roundNumber = 1;
        bulletPositions = generateBulletPositions(plugin.getConfigManager().getChamberSize(), plugin.getConfigManager().getBulletCount()); // Generate bullet positions
        List<Player> playersInLobby = lobby.getPlayers(); // Get players in the lobby
        String startMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getStartGameMessage()); // Prepare start message
        for (Player p : playersInLobby) {
            p.sendMessage(startMessage.replace("%round%", String.valueOf(roundNumber))); // Notify players of game start
        }
        new GameRunnable(lobby).runTaskTimer(plugin, 0L, plugin.getConfigManager().getRoundDelayTicks()); // Schedule game rounds
    }

    public void resetGameForLobby(Lobby lobby) {
        isGameActive = false;
        bulletPositions.clear();
        currentChamber = 0;
        roundNumber = 1;
        String endMessage = ChatColor.GOLD + "Game in lobby " + lobby.getId() + " has ended."; // Prepare end message
        for (Player p : lobby.getPlayers()) {
            p.sendMessage(endMessage); // Notify players of game end
        }
    }

    private List<Integer> generateBulletPositions(int chamberSize, int bulletCount) { // Generate unique bullet positions
        List<Integer> positions = new ArrayList<>(); // Store bullet positions
        while (positions.size() < bulletCount) { // Until we have enough bullets
            int pos = random.nextInt(chamberSize); // Random position
            if (!positions.contains(pos)) { // Ensure uniqueness
                positions.add(pos); // Add position
            }
        }
        Collections.sort(positions);
        return positions;
    }

    private class GameRunnable extends BukkitRunnable {
        private final Lobby lobby;

        public GameRunnable(Lobby lobby) {
            this.lobby = lobby;
        }

        @Override
        public void run() {
            if (!isGameActive || lobby.getPlayers().isEmpty()) { // Stop if game is not active or no players
                cancel();
                return;
            }

            Player currentPlayer = lobby.getCurrentPlayer();
            if (currentPlayer == null || currentPlayer.getHealth() <= 0.0) {
                lobby.nextTurn();
                long aliveCount = lobby.getPlayers().stream().filter(p -> p.getHealth() > 0.0).count();
                if (aliveCount <= 1) {
                    Player winner = lobby.getPlayers().stream().filter(p -> p.getHealth() > 0.0).findFirst().orElse(null); // Determine winner
                    String winMessage = ChatColor.GOLD + "Game ended! Winner: ";// Prepare win message
                    winMessage += (winner != null) ? winner.getName() : "no one won.";// Append winner name or no one
                    for (Player p : lobby.getPlayers()) {
                        p.sendMessage(winMessage);
                    }
                    getGameManager().resetGameForLobby(lobby);
                    cancel();
                    return;
                }
                return;
            }

            String spinMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getSpinningDrumMessage());
            currentPlayer.sendMessage(spinMessage.replace("%player%", currentPlayer.getName()));

            currentChamber = (currentChamber + 1) % plugin.getConfigManager().getChamberSize();

            if (bulletPositions.contains(currentChamber)) {
                currentPlayer.setHealth(0.0);
                String shotMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getShotMessage());
                currentPlayer.sendMessage(shotMessage);
                for (Player p : lobby.getPlayers()) {
                    if (p != currentPlayer) {
                        p.sendMessage(ChatColor.GRAY + currentPlayer.getName() + " loss."); // Notify others of loss
                    }
                }
            } else {
                String safeMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getSafeMessage()); // Safe message
                currentPlayer.sendMessage(safeMessage);
            }

            lobby.nextTurn();
        }

        private GameManager getGameManager() {
            return plugin.getGameManager();
        }
    }
}