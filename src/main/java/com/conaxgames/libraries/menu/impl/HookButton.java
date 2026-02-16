package com.conaxgames.libraries.menu.impl;

import com.conaxgames.libraries.hooks.Hook;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HookButton extends Button {

    private final Plugin plugin;
    private final Hook hook;

    public HookButton(Plugin plugin, Hook hook) {
        this.plugin = plugin;
        this.hook = hook;
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

        description.add(CC.GRAY + "Depend: " + CC.SECONDARY + hooked);
        description.add(CC.GRAY + "Soft depend: " + CC.SECONDARY + softDepend);
        description.add(CC.GRAY + "Load before: " + CC.SECONDARY + loadBefore);

        if (hook != null) {
            description.add(" ");
            description.add(CC.GRAY + "Hook Type: " + CC.SECONDARY + hook.getHookType().name());
            description.add(CC.GRAY + "Plugin Version: " + CC.SECONDARY + hook.getPlugin().getDescription().getVersion());
        }

        return description;
    }

    @Override
    public Material getMaterial(Player var1) {
        return XMaterial.BOOK.get();
    }
}
