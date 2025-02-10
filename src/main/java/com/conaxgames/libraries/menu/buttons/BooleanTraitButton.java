package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BooleanTraitButton<T> extends Button {

    private final T target;
    private final String trait;
    private final String description;
    private final BiConsumer<T, Boolean> writeFunction;
    private final Function<T, Boolean> readFunction;
    private final Consumer<T> saveFunction;

    public BooleanTraitButton(T target, String trait, String description, BiConsumer<T, Boolean> writeFunction, Function<T, Boolean> readFunction) {
        this(target, trait, description, writeFunction, readFunction, (i) -> {});
    }

    public BooleanTraitButton(T target, String trait, String description, BiConsumer<T, Boolean> writeFunction, Function<T, Boolean> readFunction, Consumer<T> saveFunction) {
        this.target = target;
        this.trait = trait;
        this.description = description;
        this.writeFunction = writeFunction;
        this.readFunction = readFunction;
        this.saveFunction = saveFunction;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Edit " + trait;
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        lore.addAll(FormatUtil.wordWrap(CC.GRAY + "" + description, 24));
        lore.add(" ");
        lore.add(CC.GRAY + "Current: " + CC.WHITE + (readFunction.apply(target) ? "Enabled" : "Disabled"));
        lore.add(" ");
        lore.add(CC.YELLOW + "Click to " + (readFunction.apply(target) ? "disable" : "enable") + "!");
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return XMaterial.INK_SAC.parseMaterial();
    }

    @Override
    public int getDamageValue(Player player) {
        return readFunction.apply(target) ? 10 : 8;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        boolean current = readFunction.apply(target);

        writeFunction.accept(target, !current);
        saveFunction.accept(target);

        playNeutral(player);
        player.sendMessage(CC.GREEN + "Set " + trait + " to " + (current ? "off" : "on") + ".");
    }

}
