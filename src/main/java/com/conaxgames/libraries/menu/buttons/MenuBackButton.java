package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class MenuBackButton extends Button {

    private final Consumer<Player> openPreviousMenuConsumer;

    public MenuBackButton(Consumer<Player> openPreviousMenuConsumer) {
        this.openPreviousMenuConsumer = Preconditions.checkNotNull(openPreviousMenuConsumer, "openPreviousMenuConsumer");
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Back";
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        lore.addAll(FormatUtil.wordWrap(CC.GRAY + "Click here to return to the previous menu.", 24));

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return XMaterial.RED_BED.get();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        openPreviousMenuConsumer.accept(player);
    }

}