package com.conaxgames.libraries.event.impl.menu;

import com.conaxgames.libraries.event.BaseEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class MenuButtonJumpToEvent extends BaseEvent {

    private final Player viewer;
    private final PaginatedMenu menu;
    private final Button button;

    public MenuButtonJumpToEvent(Player viewer, PaginatedMenu menu, Button button) {
        this.viewer = viewer;
        this.menu = menu;
        this.button = button;
    }

}
