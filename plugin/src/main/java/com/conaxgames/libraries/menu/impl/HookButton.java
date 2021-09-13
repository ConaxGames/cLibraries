package com.conaxgames.libraries.menu.impl;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HookButton extends Button {

    private final Plugin plugin;

    public HookButton(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName(Player var1) {
        return CC.SECONDARY + plugin.getName();
    }

    @Override
    public List<String> getDescription(Player var1) {
        List<String> description = new ArrayList<>();

        boolean hooked = plugin.getDescription().getDepend().contains("cLibraries");
        boolean loadBefore = plugin.getDescription().getLoadBefore().contains("cLibraries");
        boolean softDepend = plugin.getDescription().getSoftDepend().contains("cLibraries");

        description.add(CC.GRAY + "Depend: " + hooked);
        description.add(CC.GRAY + "Soft depend: " + softDepend);
        description.add(CC.GRAY + "Load before: " + loadBefore);

        return description;
    }

    @Override
    public Material getMaterial(Player var1) {
        return Material.BOOK;
    }
}
