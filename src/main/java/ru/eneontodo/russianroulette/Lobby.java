package ru.eneontodo.russianroulette;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

public class Lobby {
    private final int id;
    private final List<Player> players;
    private final int maxPlayers;
    private boolean gameStarted;
    private int currentPlayerIndex;
    private final Random random;

    public Lobby(int id, RussianRoulettePlugin plugin, int maxPlayers) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.currentPlayerIndex = 0;
        this.random = new Random();
    }

    public int getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers && !gameStarted) {
            players.add(player);
            return true;
        }
        return false;
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (players.isEmpty() && gameStarted) {
            gameStarted = false;
            getGameManager().resetGameForLobby(this);
        }
    }

    public boolean isFull() {
        return players.size() >= maxPlayers; // Check if lobby is full
    }

    public int getAvailableSlots() {
        return maxPlayers - players.size(); // Get number of available slots
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty() || currentPlayerIndex >= players.size()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    public void startGame() { // Start the game if there are players
        if (!players.isEmpty() && !gameStarted) { // Start the game if there are players
            gameStarted = true;
            currentPlayerIndex = random.nextInt(players.size());
            announceTurn(getCurrentPlayer());
            getGameManager().startGame(this);
        } else if (players.isEmpty()) { // No players, cannot start
            gameStarted = false;
        }
    }

    public void nextTurn() { // Move to the next player's turn
        if (players.isEmpty()) { 
            gameStarted = false;
            return;
        }
        players.removeIf(p -> p.getHealth() <= 0.0); // Remove eliminated players
        if (players.isEmpty()) {
            gameStarted = false;
            getGameManager().resetGameForLobby(this);
            return;
        }
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); // Next player
        } while (players.size() > 1 && getCurrentPlayer().getHealth() <= 0.0);
        announceTurn(getCurrentPlayer());
    }

    private void announceTurn(Player player) {
        if (player != null) { // Announce whose turn it is
            player.sendMessage(ChatColor.GOLD + "Now it's your turn!");
            for (Player p : players) {
                if (p != player) {
                    p.sendMessage(ChatColor.GRAY + player.getName() + " makes his move.");
                }
            }
        }
    }

    private GameManager getGameManager() {
        return RussianRoulettePlugin.getPlugin(RussianRoulettePlugin.class).getGameManager();
    }
}