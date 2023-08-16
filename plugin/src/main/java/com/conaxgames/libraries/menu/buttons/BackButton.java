
package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.List;

public class BackButton extends Button {
    private Menu back;

    @ConstructorProperties(value={"back"})
    public BackButton(Menu back) {
        this.back = back;
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return XMaterial.RED_BED.parseMaterial();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        this.back.openMenu(player, false);
    }
}

