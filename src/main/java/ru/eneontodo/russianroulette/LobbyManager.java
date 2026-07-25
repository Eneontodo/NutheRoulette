package ru.eneontodo.russianroulette;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

public final class LobbyManager {
    private static final int MIN_LOBBIES = 3;

    private final List<Lobby> lobbies;
    private int nextLobbyId;
    private final RussianRoulettePlugin plugin;
    private final int maxPlayersPerLobby;

    public LobbyManager(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
        this.lobbies = new ArrayList<>();
        this.nextLobbyId = 1;
        this.maxPlayersPerLobby = plugin.getConfigManager().getMaxPlayers();
        for (int i = 0; i < MIN_LOBBIES; i++) {
            createLobby();
        }
    }

    public Lobby createLobby() {
        int id = nextLobbyId++; // Increment for next lobby
        Lobby lobby = new Lobby(id, plugin, maxPlayersPerLobby);
        lobbies.add(lobby);
        return lobby;
    }

    public Lobby findAvailableLobby() {
        for (Lobby lobby : lobbies) {
            if (!lobby.isFull() && !lobby.isGameStarted()) {
                return lobby;
            }
        }
        return createLobby();
    }

    public Lobby getLobbyByPlayer(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.getPlayers().contains(player)) {
                return lobby;
            }
        }
        return null;
    }

    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }

    public void removePlayerFromLobby(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.removePlayer(player);
            pruneEmptyLobbies();
        }
    }

    // Prevent unbounded growth: drop extra empty, idle lobbies but always keep a few ready.
    private void pruneEmptyLobbies() {
        Iterator<Lobby> it = lobbies.iterator();
        while (it.hasNext() && lobbies.size() > MIN_LOBBIES) {
            Lobby lobby = it.next();
            if (lobby.getPlayers().isEmpty() && !lobby.isGameStarted()) {
                it.remove();
            }
        }
    }
}
