package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TimerManager {

    private final Set<Timer> timers = new LinkedHashSet<>();
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener listener) {
            LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager()
                    .registerEvents(listener, LibraryPlugin.getInstance().getPlugin());
        }
    }

    public void unregisterTimer(Timer timer) {
        timers.remove(timer);
    }

    public <T extends Timer> T getTimer(Class<T> timerClass) {
        for (var timer : timers) {
            if (timerClass.isInstance(timer)) {
                return timerClass.cast(timer);
            }
        }
        return null;
    }

    public Set<Timer> getTimers() {
        return Collections.unmodifiableSet(timers);
    }

    public void setCooldown(Player player, String key, long duration) {
        setCooldown(player.getUniqueId(), key, duration);
    }

    public void setCooldown(UUID uuid, String key, long duration) {
        cooldowns.computeIfAbsent(uuid, _ -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis() + duration);
    }

    public boolean hasCooldown(Player player, String key) {
        return getRemaining(player.getUniqueId(), key) > 0L;
    }

    public boolean hasCooldown(UUID uuid, String key) {
        return getRemaining(uuid, key) > 0L;
    }

    public long getRemaining(Player player, String key) {
        return getRemaining(player.getUniqueId(), key);
    }

    public long getRemaining(UUID uuid, String key) {
        var keys = cooldowns.get(uuid);
        if (keys == null) {
            return 0L;
        }
        var expiry = keys.get(key);
        if (expiry == null) {
            return 0L;
        }
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0L) {
            keys.remove(key);
            return 0L;
        }
        return remaining;
    }

    public void removeCooldown(Player player, String key) {
        removeCooldown(player.getUniqueId(), key);
    }

    public void removeCooldown(UUID uuid, String key) {
        var keys = cooldowns.get(uuid);
        if (keys != null) {
            keys.remove(key);
        }
    }

    public void clearExpiredCooldowns() {
        cooldowns.forEach((_, keys) ->
                keys.values().removeIf(expiry -> System.currentTimeMillis() >= expiry));
        cooldowns.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
}
