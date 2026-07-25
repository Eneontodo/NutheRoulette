package ru.eneontodo.russianroulette;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager {
    private final RussianRoulettePlugin plugin;
    private final Random random;

    public GameManager(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public void startGame(Lobby lobby) {
        int chamberSize = plugin.getConfigManager().getChamberSize();
        int bulletCount = plugin.getConfigManager().getBulletCount();
        lobby.setBulletPositions(generateBulletPositions(chamberSize, bulletCount));

        LocalizationManager loc = plugin.getLocalizationManager();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%round%", String.valueOf(lobby.getRoundNumber()));
        for (Player p : lobby.getPlayers()) {
            p.sendMessage(loc.trp("startGameMessage", placeholders));
        }

        long delay = plugin.getConfigManager().getRoundDelayTicks();
        new GameRunnable(lobby).runTaskTimer(plugin, delay, delay); // Schedule the rounds
    }

    public void resetGameForLobby(Lobby lobby) {
        lobby.resetGameState();
    }

    private List<Integer> generateBulletPositions(int chamberSize, int bulletCount) {
        // Guard against invalid config that would otherwise cause an infinite loop
        // (bulletCount > chamberSize) or an exception (chamberSize == 0).
        if (chamberSize < 1) {
            chamberSize = 1;
        }
        if (bulletCount < 1) {
            bulletCount = 1;
        }
        if (bulletCount > chamberSize) {
            bulletCount = chamberSize;
        }
        List<Integer> positions = new ArrayList<>();
        while (positions.size() < bulletCount) {
            int pos = random.nextInt(chamberSize);
            if (!positions.contains(pos)) {
                positions.add(pos);
            }
        }
        Collections.sort(positions);
        return positions;
    }

    private void announceWinner(Lobby lobby, Player winner) {
        LocalizationManager loc = plugin.getLocalizationManager();
        for (Player p : lobby.getPlayers()) {
            String winnerName = (winner != null) ? winner.getName() : loc.tr("noWinner");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%winner%", winnerName);
            p.sendMessage(loc.trp("gameEnded", placeholders));
        }
    }

    private class GameRunnable extends BukkitRunnable {
        private final Lobby lobby;

        public GameRunnable(Lobby lobby) {
            this.lobby = lobby;
        }

        @Override
        public void run() {
            if (!lobby.isGameStarted() || lobby.getPlayers().isEmpty()) {
                cancel();
                return;
            }

            // Winner check first: dead players are removed from the lobby in nextTurn(),
            // so as soon as one (or zero) players remain, the game is over.
            List<Player> remaining = lobby.getPlayers();
            long aliveCount = remaining.stream()
                    .filter(p -> p.isOnline() && p.getHealth() > 0.0)
                    .count();
            if (aliveCount <= 1) {
                Player winner = remaining.stream()
                        .filter(p -> p.isOnline() && p.getHealth() > 0.0)
                        .findFirst().orElse(null);
                announceWinner(lobby, winner);
                resetGameForLobby(lobby);
                cancel();
                return;
            }

            Player currentPlayer = lobby.getCurrentPlayer();
            if (currentPlayer == null || !currentPlayer.isOnline() || currentPlayer.getHealth() <= 0.0) {
                lobby.nextTurn();
                return;
            }

            LocalizationManager loc = plugin.getLocalizationManager();
            int chamberSize = plugin.getConfigManager().getChamberSize();
            lobby.setCurrentChamber((lobby.getCurrentChamber() + 1) % chamberSize);

            Map<String, String> spinPlaceholders = new HashMap<>();
            spinPlaceholders.put("%player%", currentPlayer.getName());
            spinPlaceholders.put("%round%", String.valueOf(lobby.getRoundNumber()));
            for (Player p : lobby.getPlayers()) {
                p.sendMessage(loc.trp("spinningDrumMessage", spinPlaceholders));
            }

            if (lobby.getBulletPositions().contains(lobby.getCurrentChamber())) {
                currentPlayer.sendMessage(loc.tr("shotMessage"));
                Map<String, String> lostPlaceholders = new HashMap<>();
                lostPlaceholders.put("%player%", currentPlayer.getName());
                for (Player p : lobby.getPlayers()) {
                    if (!p.equals(currentPlayer)) {
                        p.sendMessage(loc.trp("playerLost", lostPlaceholders));
                    }
                }
                currentPlayer.setHealth(0.0);
            } else {
                currentPlayer.sendMessage(loc.tr("safeMessage"));
            }

            lobby.incrementRound();
            lobby.nextTurn();
        }
    }
}
