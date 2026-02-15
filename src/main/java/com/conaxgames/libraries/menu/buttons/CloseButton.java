package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CloseButton extends Button {

    @Override
    public String getName(Player player) {
        return CC.RED + "Close";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return XMaterial.BARRIER.get();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
    }
}
