package com.conaxgames.libraries.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.board.Board;
import com.conaxgames.libraries.board.BoardEntry;
import com.conaxgames.libraries.event.impl.FakeDeathEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final LibraryPlugin library;

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null) {
            // Check if player has cElement metadata - if they do, don't give them a board
            if (player.hasMetadata("cElement")) {
                // Ensure any existing board is cleaned up for zero CPU cost
                library.getBoardManager().cleanupPlayerBoardIfCElement(player);
                return;
            }
            library.getBoardManager().getPlayerBoards().put(player.getUniqueId(), new Board(player, library.getBoardManager().getAdapter()));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null) {
            cleanupPlayerBoard(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null) {
            cleanupPlayerBoard(player);
        }
    }

    private void cleanupPlayerBoard(Player player) {
        Board board = library.getBoardManager().getPlayerBoards().get(player.getUniqueId());
        if (board != null) {
            // Clean up all board entries first
            if (!board.getEntries().isEmpty()) {
                board.getEntries().forEach(BoardEntry::remove);
                board.getEntries().clear();
            }
            // Remove board from map
            library.getBoardManager().getPlayerBoards().remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        /* If the event entity is not a player, we don't need to continue */
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player dead = (Player) event.getEntity();

        if ((event.getDamage()/2) >= dead.getHealth()) {
            new FakeDeathEvent(event, dead, event.getDamager()).call();
        }
    }
}
