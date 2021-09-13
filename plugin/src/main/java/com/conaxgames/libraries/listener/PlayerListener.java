package com.conaxgames.libraries.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.board.Board;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (LibraryPlugin.getInstance().getBoardManager() != null) {
            LibraryPlugin.getInstance().getBoardManager().getPlayerBoards().put(player.getUniqueId(), new Board(player, LibraryPlugin.getInstance().getBoardManager().getAdapter()));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (LibraryPlugin.getInstance().getBoardManager() != null) {
            LibraryPlugin.getInstance().getBoardManager().getPlayerBoards().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (LibraryPlugin.getInstance().getBoardManager() != null) {
            LibraryPlugin.getInstance().getBoardManager().getPlayerBoards().remove(player.getUniqueId());
        }
    }
}
