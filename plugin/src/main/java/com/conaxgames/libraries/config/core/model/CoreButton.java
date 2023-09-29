package com.conaxgames.libraries.config.core.model;

import com.conaxgames.libraries.config.core.model.ConfigButtonData;
import com.conaxgames.libraries.config.core.model.CoreButtonClick;
import com.conaxgames.libraries.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CoreButton extends Button {

    private final ConfigButtonData data;
    private final CoreButtonClick clickConsumer;

    public CoreButton(ConfigButtonData data, CoreButtonClick clickConsumer) {
        this.data = data;
        this.clickConsumer = clickConsumer;
    }

    @Override public String getName(Player var1) {return null;}
    @Override public List<String> getDescription(Player var1) {return null;}
    @Override public Material getMaterial(Player var1) {return null;}

    @Override
    public ItemStack getButtonItem(Player player) {
        return data.getItemBuilder().toItemStack();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        clickConsumer.onClick(player, slot, clickType);
    }
}
