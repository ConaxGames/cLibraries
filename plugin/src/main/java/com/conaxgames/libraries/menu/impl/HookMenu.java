package com.conaxgames.libraries.menu.impl;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.hooks.Hook;
import com.conaxgames.libraries.hooks.HookManager;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class HookMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Hooked Plugins";
    }

    @Override
    public Map<Integer, Button> getButtons(Player var1) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        HookManager hookManager = LibraryPlugin.getInstance().getHookManager();

        int index = 0;
        for (Plugin plugin : hookManager.getDepends()) {
            buttons.put(index++, new HookButton(plugin, null));
        }
        for (Hook hook : hookManager.getHooks()) {
            buttons.put(index++, new HookButton(hook.getPlugin(), hook));
        }

        return buttons;
    }
}
