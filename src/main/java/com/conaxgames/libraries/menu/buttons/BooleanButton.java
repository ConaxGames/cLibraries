package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.Callback;
import com.conaxgames.libraries.util.WoolUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class BooleanButton extends Button {

    private final boolean confirm;
    private final Callback<Boolean> callback;
    private final String details;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        player.closeInventory();
        this.callback.callback(this.confirm);
    }

    @Override
    public String getName(Player player) {
        return this.confirm ? CC.GREEN + "Confirm" : CC.RED + "Cancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        if (this.confirm) {
            description.addAll(FormatUtil.wordWrap(CC.GRAY + details));
        } else {
            description.add(CC.GRAY + "Cancel this action.");
        }

        return description;
    }

    @Override
    public int getDamageValue(Player player) {
        return this.confirm ? (byte)5 : 14;
    }

    @Override
    public Material getMaterial(Player player) {
        return WoolUtil.convertChatColorToXClay(this.confirm ? ChatColor.GREEN : ChatColor.RED).get();
    }

    @ConstructorProperties(value={"confirm", "callback"})
    public BooleanButton(boolean confirm, Callback<Boolean> callback, String details) {
        this.details = details;
        this.confirm = confirm;
        this.callback = callback;
    }
}

