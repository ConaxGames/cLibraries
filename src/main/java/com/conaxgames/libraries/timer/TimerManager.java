package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class TimerManager implements Listener {

    private final Set<Timer> timers = new LinkedHashSet<>();

    public void registerTimer(Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().registerEvents((Listener) timer, LibraryPlugin.getInstance().getPlugin());
        }
    }

    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }

    public <T extends Timer> T getTimer(Class<T> timerClass) {
        for (Timer timer : this.timers) {
            if (timerClass.isInstance(timer)) {
                return timerClass.cast(timer);
            }
        }
        return null;
    }
}
