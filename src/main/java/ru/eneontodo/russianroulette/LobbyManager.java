package ru.eneontodo.russianroulette;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public final class LobbyManager {
    private final List<Lobby> lobbies;
    private int nextLobbyId;
    private final RussianRoulettePlugin plugin;
    private final int maxPlayersPerLobby;

    public LobbyManager(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
        this.lobbies = new ArrayList<>();
        this.nextLobbyId = 1;
        this.maxPlayersPerLobby = plugin.getConfigManager().getMaxPlayers();
        for (int i = 0; i < 3; i++) {
            createLobby();
        }
    }

    public Lobby createLobby() {
        int id = nextLobbyId++;// Increment for next lobby
        Lobby lobby = new Lobby(id, plugin, maxPlayersPerLobby);// Create new lobby
        lobbies.add(lobby);// Add to the list of lobbies
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

    public List<Lobby> getLobbies() {// get all lobbies
        return new ArrayList<>(lobbies);
    }

    public void removePlayerFromLobby(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.removePlayer(player);
        }
    }
}