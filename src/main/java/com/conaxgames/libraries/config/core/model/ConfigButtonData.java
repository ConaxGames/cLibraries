package com.conaxgames.libraries.config.core.model;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.hooks.HookType;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.ItemBuilderUtil;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ConfigButtonData {

    public final List<String> actions;
    public final List<String> conditions;
    public final String name;
    public final String permission;
    public final int slot;
    public final XMaterial material;
    public final int materialData;
    public final boolean shiny;
    public final List<String> lore;

    @Setter
    public String skull64 = null;


    public ItemBuilderUtil getItemBuilder(Player player) {
        ItemBuilderUtil builder = new ItemBuilderUtil(this.material.get());
        builder.setName(CC.translate(this.name));
        builder.setDurability((short) this.materialData);

        this.lore.forEach(s -> {
            String translated = s;
            if (LibraryPlugin.getInstance().getHookManager().isHooked(HookType.PLACEHOLDERAPI)) {
                translated = PlaceholderAPI.setPlaceholders(player, translated);
            }
            builder.addLoreLineList(FormatUtil.wordWrap(CC.translate(translated)));
        });

        if (this.getMaterial().equals(XMaterial.PLAYER_HEAD) && this.skull64 != null) {
            builder.setSkullProfile(this.skull64);
        }

        return builder;
    }

    public boolean hasAction() {
        return actions != null;
    }

    public boolean hasCondition() {
        return conditions != null && !conditions.isEmpty();
    }

    public boolean hasPermission(Player player) {
        if (this.permission == null) return true;

        return player.hasPermission(this.permission);
    }

}
