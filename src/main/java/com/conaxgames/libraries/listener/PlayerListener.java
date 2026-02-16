package com.conaxgames.libraries.listener;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final LibraryPlugin library;

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null && !player.hasMetadata("cElement")) {
            library.getBoardManager().createBoard(player);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null && !player.hasMetadata("cElement")) {
            library.getBoardManager().removeBoard(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null && !player.hasMetadata("cElement")) {
            library.getBoardManager().removeBoard(player);
        }
    }
}
