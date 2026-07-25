package ru.eneontodo.russianroulette;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final RussianRoulettePlugin plugin;

    public PlayerListener(RussianRoulettePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove a disconnecting player so they don't block a running game or leak references.
        plugin.getLobbyManager().removePlayerFromLobby(event.getPlayer());
    }
}
