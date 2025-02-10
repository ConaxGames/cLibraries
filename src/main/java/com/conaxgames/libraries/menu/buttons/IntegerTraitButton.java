package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class IntegerTraitButton<T> extends Button {

    private final T target;
    private final String trait;
    private final String description;
    private final BiConsumer<T, Integer> writeFunction;
    private final Function<T, Integer> readFunction;
    private final Consumer<T> saveFunction;

    public IntegerTraitButton(T target, String trait, String description, BiConsumer<T, Integer> writeFunction, Function<T, Integer> readFunction) {
        this(target, trait, description, writeFunction, readFunction, (i) -> {});
    }

    public IntegerTraitButton(T target, String trait, String description, BiConsumer<T, Integer> writeFunction, Function<T, Integer> readFunction, Consumer<T> saveFunction) {
        this.target = target;
        this.trait = trait;
        this.description = description;
        this.writeFunction = writeFunction;
        this.readFunction = readFunction;
        this.saveFunction = saveFunction;
    }

    @Override
    public String getName(Player player) {
        return CC.SECONDARY + "Edit " + trait;
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        lore.addAll(FormatUtil.wordWrap(CC.GRAY + "" + description));
        lore.add(" ");
        lore.add(CC.GRAY + "Current: " + CC.WHITE + readFunction.apply(target));
        lore.add(" ");
        lore.add(CC.YELLOW + "Left-click to increase by 1!");
        lore.add(CC.YELLOW + "Right-click to decrease by 1!");
        lore.add(" ");
        lore.addAll(FormatUtil.wordWrap(CC.DARK_GRAY + "Holding shift will increase or decrease by 10."));
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.GHAST_TEAR;
    }

    @Override
    public int getAmount(Player player) {
        return readFunction.apply(target);
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        int current = readFunction.apply(target);
        int change = clickType.isShiftClick() ? 10 : 1;

        if (clickType.isRightClick()) {
            change = -change;
        }

        writeFunction.accept(target, current + change);
        saveFunction.accept(target);

        player.sendMessage(ChatColor.GREEN + "Set " + trait + " to " + (current + change) + ".");
    }

}