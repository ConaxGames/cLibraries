package com.conaxgames.libraries.menu.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.cryptomorin.xseries.inventory.XInventoryView;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ButtonListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory top = XInventoryView.of(event.getView()).getTopInventory();
        if (!(top.getHolder() instanceof Menu.Holder holder) || !holder.viewerId.equals(player.getUniqueId())) {
            return;
        }
        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }
        boolean shift = event.getClick().isShiftClick();
        if (event.getRawSlot() != event.getSlot()) {
            if (shift && holder.hasEditable()) {
                ItemStack moving = event.getCurrentItem();
                if (moving != null && moving.getType() != Material.AIR && moving.getAmount() > 0) {
                    event.setCancelled(true);
                    int remaining = moving.getAmount();
                    int max = moving.getMaxStackSize();

                    for (int slot = 0; slot < top.getSize() && remaining > 0; slot++) {
                        if (!holder.editable(slot)) {
                            continue;
                        }
                        ItemStack existing = top.getItem(slot);
                        if (existing == null || existing.getType() == Material.AIR || existing.getAmount() <= 0
                                || !existing.isSimilar(moving)) {
                            continue;
                        }
                        int space = max - existing.getAmount();
                        if (space <= 0) {
                            continue;
                        }
                        int added = Math.min(space, remaining);
                        existing.setAmount(existing.getAmount() + added);
                        top.setItem(slot, existing);
                        remaining -= added;
                    }

                    for (int slot = 0; slot < top.getSize() && remaining > 0; slot++) {
                        ItemStack existing = top.getItem(slot);
                        if (!holder.editable(slot) || (existing != null && existing.getType() != Material.AIR && existing.getAmount() > 0)) {
                            continue;
                        }
                        int added = Math.min(max, remaining);
                        ItemStack copy = moving.clone();
                        copy.setAmount(added);
                        top.setItem(slot, copy);
                        remaining -= added;
                    }

                    if (remaining <= 0) {
                        event.setCurrentItem(null);
                    } else {
                        moving.setAmount(remaining);
                        event.setCurrentItem(moving);
                    }
                    LibraryPlugin.getInstance().getScheduler().runTaskLater(
                            LibraryPlugin.getInstance().getPlugin(),
                            player::updateInventory,
                            1L
                    );
                }
                return;
            }
            event.setCancelled(!holder.hasEditable());
            return;
        }
        if (holder.editable(event.getSlot())) {
            return;
        }
        event.setCancelled(true);
        Button button = holder.button(event.getSlot());
        if (button == null) {
            return;
        }
        button.click(player, event.getClick());
        if (Menu.opened(player) == holder.menu && holder.menu.updateAfterClick()) {
            holder.menu.update(player);
        }
        LibraryPlugin.getInstance().getScheduler().runTaskLater(
                LibraryPlugin.getInstance().getPlugin(),
                player::updateInventory,
                1L
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory top = XInventoryView.of(event.getView()).getTopInventory();
        if (!(top.getHolder() instanceof Menu.Holder holder) || !holder.viewerId.equals(player.getUniqueId())) {
            return;
        }
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < top.getSize() && !holder.editable(rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof Menu.Holder holder) || !holder.viewerId.equals(player.getUniqueId())) {
            return;
        }
        Menu menu = holder.menu;
        menu.closed(player);
        Menu.endSession(player.getUniqueId());
        Menu previous = menu.previous(player);
        if (previous != null) {
            LibraryPlugin.getInstance().getScheduler().runTaskLater(
                    LibraryPlugin.getInstance().getPlugin(),
                    () -> {
                        if (Menu.opened(player) == null) {
                            previous.open(player);
                        }
                    },
                    2L
            );
        }
    }
}
