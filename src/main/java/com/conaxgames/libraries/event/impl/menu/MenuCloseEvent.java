package com.conaxgames.libraries.event.impl.menu;

import com.conaxgames.libraries.event.BaseEvent;
import com.conaxgames.libraries.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class MenuCloseEvent extends BaseEvent {

    private final Player viewer;
    private final Menu menu;

    public MenuCloseEvent(Player viewer, Menu menu) {
        this.viewer = viewer;
        this.menu = menu;
    }

}
