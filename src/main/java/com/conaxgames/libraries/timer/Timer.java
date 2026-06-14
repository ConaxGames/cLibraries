package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.timer.TimerClearEvent;
import com.conaxgames.libraries.event.impl.timer.TimerExtendEvent;
import com.conaxgames.libraries.event.impl.timer.TimerPauseEvent;
import com.conaxgames.libraries.event.impl.timer.TimerStartEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongPredicate;

public abstract class Timer {

    private final String name;
    private final long defaultCooldown;
    private final Map<UUID, TimerCooldown> cooldowns = new ConcurrentHashMap<>();

    protected Timer(String name, long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }

    public final String getDisplayName() {
        return name;
    }

    public final long getDefaultCooldown() {
        return defaultCooldown;
    }

    protected void handleExpiry(@Nullable Player player, UUID playerUUID) {
        cooldowns.remove(playerUUID);
    }

    public TimerCooldown clearCooldown(Player player) {
        return clearCooldown(player, player.getUniqueId());
    }

    public TimerCooldown clearCooldown(UUID playerUUID) {
        return clearCooldown(null, playerUUID);
    }

    public TimerCooldown clearCooldown(@Nullable Player player, UUID playerUUID) {
        var cooldown = cooldowns.remove(playerUUID);
        if (cooldown != null) {
            cooldown.cancel();
            dispatch(new TimerClearEvent(player, playerUUID, this));
        }
        return cooldown;
    }

    public boolean isPaused(Player player) {
        return isPaused(player.getUniqueId());
    }

    public boolean isPaused(UUID playerUUID) {
        var cooldown = cooldowns.get(playerUUID);
        return cooldown != null && cooldown.isPaused();
    }

    public void setPaused(Player player, boolean paused) {
        setPaused(player.getUniqueId(), paused);
    }

    public void setPaused(UUID playerUUID, boolean paused) {
        var cooldown = cooldowns.get(playerUUID);
        if (cooldown != null && cooldown.isPaused() != paused) {
            var event = new TimerPauseEvent(playerUUID, this, paused);
            dispatch(event);
            if (!event.isCancelled()) {
                cooldown.setPaused(paused);
            }
        }
    }

    public long getRemaining(Player player) {
        return getRemaining(player.getUniqueId());
    }

    public long getRemaining(UUID playerUUID) {
        var cooldown = cooldowns.get(playerUUID);
        return cooldown == null ? 0L : cooldown.getRemaining();
    }

    public boolean setCooldown(Player player) {
        return setCooldown(player, player.getUniqueId(), defaultCooldown, false, null);
    }

    public boolean setCooldown(Player player, long duration, boolean overwrite) {
        return setCooldown(player, player.getUniqueId(), duration, overwrite, null);
    }

    public boolean setCooldown(UUID playerUUID, long duration, boolean overwrite) {
        return setCooldown(null, playerUUID, duration, overwrite, null);
    }

    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration,
                               boolean overwrite, @Nullable LongPredicate currentCooldownPredicate) {
        if (duration <= 0L) {
            clearCooldown(player, playerUUID);
            return false;
        }

        var existing = cooldowns.get(playerUUID);
        if (existing != null) {
            long remaining = existing.getRemaining();
            if (!overwrite && remaining > 0L && duration <= remaining) {
                return false;
            }

            var event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
            dispatch(event);
            if (event.isCancelled()) {
                return false;
            }

            if (currentCooldownPredicate != null && !currentCooldownPredicate.test(remaining)) {
                return false;
            }

            existing.setRemaining(event.getNewDuration());
            return true;
        }

        var event = new TimerStartEvent(player, playerUUID, this, duration);
        dispatch(event);
        if (event.isCancelled()) {
            return false;
        }

        cooldowns.put(playerUUID, new TimerCooldown(this, playerUUID, duration));
        return true;
    }

    public Map<UUID, TimerCooldown> getCooldowns() {
        return Collections.unmodifiableMap(cooldowns);
    }

    private static void dispatch(Event event) {
        LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
    }
}
