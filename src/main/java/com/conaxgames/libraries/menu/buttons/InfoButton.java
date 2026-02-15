
package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoButton extends Button {

    private final String name;
    private final List<String> desc;
    private final Material material;

    public InfoButton(String name, List<String> desc, Material mat) {
        this.name = name;
        this.desc = desc;
        this.material = mat;
    }

    @Override
    public String getName(Player player) {
        return this.name;
    }

    @Override
    public List<String> getDescription(Player player) {
        return this.desc;
    }

    @Override
    public Material getMaterial(Player player) {
        return this.material;
    }
}

