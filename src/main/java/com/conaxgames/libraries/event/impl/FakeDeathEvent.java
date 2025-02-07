package com.conaxgames.libraries.event.impl;

import com.conaxgames.libraries.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Getter
public class FakeDeathEvent extends CancellableEvent {
    private final EntityDamageByEntityEvent event;
    private final Player dead;
    private final Entity killer;

    public FakeDeathEvent(EntityDamageByEntityEvent event, Player dead, Entity killer) {
        this.event = event;
        this.dead = dead;
        this.killer = killer;
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
