package ru.eneontodo.russianroulette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.Player;

public class Lobby {
    private final int id;
    private final List<Player> players;
    private final int maxPlayers;
    private final RussianRoulettePlugin plugin;
    private final Random random;

    private boolean gameStarted;
    private int currentPlayerIndex;

    // Per-lobby game state (previously stored globally in GameManager).
    private final List<Integer> bulletPositions = new ArrayList<>();
    private int currentChamber = -1;
    private int roundNumber = 1;

    public Lobby(int id, RussianRoulettePlugin plugin, int maxPlayers) {
        this.id = id;
        this.plugin = plugin;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.currentPlayerIndex = 0;
        this.random = new Random();
    }

    public int getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers && !gameStarted && !players.contains(player)) {
            players.add(player);
            return true;
        }
        return false;
    }

    public void removePlayer(Player player) {
        int idx = players.indexOf(player);
        if (idx == -1) {
            return;
        }
        players.remove(idx);
        // Keep the turn pointer aimed at the same logical player after the shift.
        if (idx < currentPlayerIndex && currentPlayerIndex > 0) {
            currentPlayerIndex--;
        }
        if (players.isEmpty()) {
            if (gameStarted) {
                gameStarted = false;
                getGameManager().resetGameForLobby(this);
            }
        } else if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public int getAvailableSlots() {
        return maxPlayers - players.size();
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty() || currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    // --- Game state accessors used by GameManager ---

    public List<Integer> getBulletPositions() {
        return bulletPositions;
    }

    public void setBulletPositions(List<Integer> positions) {
        bulletPositions.clear();
        bulletPositions.addAll(positions);
    }

    public int getCurrentChamber() {
        return currentChamber;
    }

    public void setCurrentChamber(int chamber) {
        this.currentChamber = chamber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void incrementRound() {
        this.roundNumber++;
    }

    public void startGame() {
        if (players.isEmpty() || gameStarted) {
            return;
        }
        gameStarted = true;
        currentChamber = -1;
        roundNumber = 1;
        currentPlayerIndex = random.nextInt(players.size());
        getGameManager().startGame(this);
        announceTurn(getCurrentPlayer());
    }

    public void resetGameState() {
        gameStarted = false;
        currentPlayerIndex = 0;
        currentChamber = -1;
        roundNumber = 1;
        bulletPositions.clear();
    }

    public void nextTurn() {
        if (players.isEmpty()) {
            gameStarted = false;
            return;
        }
        // Determine the next player in circular order (before anyone is removed),
        // skipping those who are offline or already eliminated.
        Player nextPlayer = null;
        int size = players.size();
        for (int step = 1; step <= size; step++) {
            Player candidate = players.get((currentPlayerIndex + step) % size);
            if (candidate.isOnline() && candidate.getHealth() > 0.0) {
                nextPlayer = candidate;
                break;
            }
        }
        // Remove eliminated / disconnected players.
        players.removeIf(p -> !p.isOnline() || p.getHealth() <= 0.0);

        if (players.size() <= 1 || nextPlayer == null || !players.contains(nextPlayer)) {
            // Game is over (or nobody valid remains); the game loop announces the winner.
            if (players.isEmpty()) {
                gameStarted = false;
            }
            return;
        }
        currentPlayerIndex = players.indexOf(nextPlayer);
        announceTurn(getCurrentPlayer());
    }

    private void announceTurn(Player player) {
        if (player == null) {
            return;
        }
        LocalizationManager loc = plugin.getLocalizationManager();
        player.sendMessage(loc.tr(player.getName(), "yourTurnMessage"));
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", player.getName());
        for (Player p : players) {
            if (!p.equals(player)) {
                p.sendMessage(loc.trp(p.getName(), "otherPlayerTurn", placeholders));
            }
        }
    }

    private GameManager getGameManager() {
        return plugin.getGameManager();
    }
}
