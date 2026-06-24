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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public static DecodedSnapshot decode(String base64) {
        if (base64 == null || base64.isBlank()) {
            return null;
        }
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             BukkitObjectInputStream data = new BukkitObjectInputStream(bytes)) {
            ItemStack[] contents = readItems(data);
            ItemStack[] armor = readItems(data);
            ItemStack[] extra = readItems(data);
            int level = data.readInt();
            float exp = data.readFloat();
            int effectCount = data.readInt();
            List<PotionEffect> effects = new ArrayList<>(effectCount);
            for (int i = 0; i < effectCount; i++) {
                effects.add((PotionEffect) data.readObject());
            }
            return new DecodedSnapshot(contents, armor, extra, level, exp, effects);
        } catch (Exception e) {
            return null;
        }
    }

    public static void restore(Player player, String base64) {
        DecodedSnapshot snapshot = decode(base64);
        if (snapshot == null) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("PlayerInventoryUtil",
                    "Failed to restore inventory for " + player.getName() + ": invalid or corrupt snapshot");
            return;
        }
        clear(player);
        PlayerInventory inv = player.getInventory();
        ItemStack[] storage = snapshot.contents();
        if (storage.length > 36) {
            storage = Arrays.copyOf(storage, 36);
        }
        if (XReflection.supports(9)) {
            inv.setStorageContents(storage);
        } else {
            inv.setContents(storage);
        }
        inv.setArmorContents(snapshot.armor());
        if (XReflection.supports(9)) {
            inv.setExtraContents(snapshot.extra());
        }
        player.setLevel(snapshot.level());
        player.setExp(snapshot.exp());
        for (PotionEffect effect : snapshot.getEffects()) {
            player.addPotionEffect(effect);
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

    public record DecodedSnapshot(
            ItemStack[] contents,
            ItemStack[] armor,
            ItemStack[] extra,
            int level,
            float exp,
            List<PotionEffect> effects
    ) {
        public List<PotionEffect> getEffects() {
            return effects == null ? Collections.emptyList() : effects;
        }
    }
}
