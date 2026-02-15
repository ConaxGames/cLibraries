package com.conaxgames.libraries.event.impl.menu;

import com.conaxgames.libraries.event.CancellableEvent;
import com.conaxgames.libraries.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class MenuBackEvent extends CancellableEvent {

    private final Player viewer;
    private final Menu current;
    private final Menu back;

    public MenuBackEvent(Player viewer, Menu current, Menu back) {
        this.viewer = viewer;
        this.current = current;
        this.back = back;
    }
}
