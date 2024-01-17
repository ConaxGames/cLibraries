package com.conaxgames.libraries.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.board.Board;
import com.conaxgames.libraries.event.impl.FakeDeathEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
            library.getBoardManager().getPlayerBoards().put(player.getUniqueId(), new Board(player, library.getBoardManager().getAdapter()));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null) {
            library.getBoardManager().getPlayerBoards().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (library.getBoardManager() != null) {
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
