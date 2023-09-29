package com.conaxgames.libraries.config.core.model;

import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.ItemBuilderUtil;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    public String skull64 = null;


    public void setSkull64(String skull) {
        this.skull64 = skull;
    }

    public ItemBuilderUtil getItemBuilder() {
        ItemBuilderUtil builder = new ItemBuilderUtil(this.material.parseMaterial());
        builder.setName(CC.translate(this.name));
        this.lore.forEach(s -> builder.addLoreLineList(FormatUtil.wordWrap(CC.translate(s))));
        builder.setDurability((short) this.materialData);

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
