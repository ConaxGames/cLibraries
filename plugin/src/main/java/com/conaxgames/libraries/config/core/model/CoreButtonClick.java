package com.conaxgames.libraries.config.core.model;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface CoreButtonClick {

    void onClick(Player player, int slot, ClickType clickType);

}
