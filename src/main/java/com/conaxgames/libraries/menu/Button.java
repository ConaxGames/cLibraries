package com.conaxgames.libraries.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.conaxgames.libraries.util.ItemBuilderUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Button {

    @Deprecated
    public static Button placeholder(Material material, byte data, String... title) {
        return Button.placeholder(material, data, title == null || title.length == 0 ? "" : Joiner.on("").join(title));
    }

    public static Button placeholder(Material material) {
        return Button.placeholder(material, "");
    }

    public static Button placeholder(Material material, String title) {
        return Button.placeholder(material, (byte) 0, title);
    }

    public static Button placeholder(final Material material, final byte data, final String title) {
        return new Button() {
            @Override
            public String getName(Player player) {
                return title;
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of();
            }

            @Override
            public Material getMaterial(Player player) {
                return material;
            }

            @Override
            public int getDamageValue(Player player) {
                return data;
            }
        };
    }

    public static Button fromItem(final ItemStack item) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return item;
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }
        };
    }

    public abstract String getName(Player player);
    public abstract List<String> getDescription(Player player);
    public abstract Material getMaterial(Player player);

    public int getDamageValue(Player player) {
        return 0;
    }

    public void clicked(Player player, int slot, ClickType clickType) {}

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public boolean shinyItem(Player player) {
        return false;
    }

    public String skullOwner(Player player) {
        return null;
    }

    public int getAmount(Player player) {
        return 1;
    }

    public ItemStack getButtonItem(Player player) {
        Material material = this.getMaterial(player);
        if (material == null) {
            material = XMaterial.BEDROCK.get();
        }

        ItemBuilderUtil builder = new ItemBuilderUtil(material, this.getAmount(player), (byte) this.getDamageValue(player))
                .setName(this.getName(player));

        List<String> description = this.getDescription(player);
        if (description != null) {
            builder.setLore(description);
        }

        if (skullOwner(player) != null) {
            builder.setSkullOwner(skullOwner(player));
        }

        if (shinyItem(player)) {
            builder.setGlow();
        }

        return builder.toItemStack();
    }

    public static void playFail(Player player) {
        XSound.BLOCK_GRASS_BREAK.play(player);
    }

    public static void playSuccess(Player player) {
        XSound.BLOCK_NOTE_BLOCK_HARP.play(player);
    }

    public static void playNeutral(Player player) {
        XSound.UI_BUTTON_CLICK.play(player);
    }
}
