package com.conaxgames.libraries.event.impl;

import com.conaxgames.libraries.event.CancellableEvent;
import com.conaxgames.libraries.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Getter
public class MenuOpenEvent extends CancellableEvent {
    private final Player viewer;
    private final Menu menu;

    public MenuOpenEvent(Player viewer, Menu menu) {
        this.viewer = viewer;
        this.menu = menu;
    }

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
