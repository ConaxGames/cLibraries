package com.conaxgames.libraries.event.impl.menu;

import com.conaxgames.libraries.event.CancellableEvent;
import com.conaxgames.libraries.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class MenuOpenEvent extends CancellableEvent {

    private final Player viewer;
    private final Menu menu;

    public MenuOpenEvent(Player viewer, Menu menu) {
        this.viewer = viewer;
        this.menu = menu;
    }
}
