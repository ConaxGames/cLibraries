package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CloseButton extends Button {

    @Override
    public String getName(Player var1) {
        return CC.RED + "Close";
    }

    @Override
    public List<String> getDescription(Player var1) {
        return null;
    }

    @Override
    public Material getMaterial(Player var1) {
        return Material.BARRIER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
    }
}
