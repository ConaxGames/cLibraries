package com.conaxgames.libraries.util.inventory;

import com.conaxgames.libraries.LibraryPlugin;
import com.cryptomorin.xseries.inventory.XInventoryView;
import com.cryptomorin.xseries.reflection.XReflection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

public final class PlayerInventoryUtil {

    private PlayerInventoryUtil() {
    }

    public static String capture(Player player) {
        PlayerInventory inv = player.getInventory();
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             BukkitObjectOutputStream data = new BukkitObjectOutputStream(bytes)) {
            ItemStack[] storage = XReflection.supports(9) ? inv.getStorageContents() : inv.getContents();
            writeItems(data, storage);
            writeItems(data, inv.getArmorContents());
            writeItems(data, XReflection.supports(9) ? inv.getExtraContents() : new ItemStack[0]);
            data.writeInt(player.getLevel());
            data.writeFloat(player.getExp());
            Collection<PotionEffect> effects = player.getActivePotionEffects();
            data.writeInt(effects.size());
            for (PotionEffect effect : effects) {
                data.writeObject(effect);
            }
            data.flush();
            return Base64.getEncoder().encodeToString(bytes.toByteArray());
        } catch (Exception e) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("PlayerInventoryUtil", "Failed to serialise inventory for " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    public static void restore(Player player, String base64) {
        if (base64 == null || base64.isBlank()) {
            return;
        }
        clear(player);
        PlayerInventory inv = player.getInventory();
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             BukkitObjectInputStream data = new BukkitObjectInputStream(bytes)) {
            ItemStack[] storage = readItems(data);
            if (storage.length > 36) {
                storage = Arrays.copyOf(storage, 36);
            }
            if (XReflection.supports(9)) {
                inv.setStorageContents(storage);
            } else {
                inv.setContents(storage);
            }
            inv.setArmorContents(readItems(data));
            ItemStack[] extra = readItems(data);
            if (XReflection.supports(9)) {
                inv.setExtraContents(extra);
            }
            player.setLevel(data.readInt());
            player.setExp(data.readFloat());
            int effects = data.readInt();
            for (int i = 0; i < effects; i++) {
                player.addPotionEffect((PotionEffect) data.readObject());
            }
        } catch (Exception e) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("PlayerInventoryUtil", "Failed to restore inventory for " + player.getName() + ": " + e.getMessage());
        }
    }

    public static void clear(Player player) {
        XInventoryView.of(player.getOpenInventory()).setCursor(null);
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[4]);
        if (XReflection.supports(9)) {
            inv.setExtraContents(new ItemStack[inv.getExtraContents().length]);
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setLevel(0);
        player.setExp(0.0f);
    }

    private static void writeItems(BukkitObjectOutputStream data, ItemStack[] items) throws Exception {
        data.writeInt(items.length);
        for (ItemStack item : items) {
            data.writeObject(item);
        }
    }

    private static ItemStack[] readItems(BukkitObjectInputStream data) throws Exception {
        ItemStack[] items = new ItemStack[data.readInt()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (ItemStack) data.readObject();
        }
        return items;
    }
}
