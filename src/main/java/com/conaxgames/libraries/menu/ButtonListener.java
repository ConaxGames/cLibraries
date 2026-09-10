package com.conaxgames.libraries.menu;

import com.conaxgames.libraries.LibraryPlugin;
import com.cryptomorin.xseries.XItemStack;
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

final class ButtonListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getInventory();
        if (!(top.getHolder() instanceof Menu.Holder) || !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Menu.Holder holder = (Menu.Holder) top.getHolder();
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            // Collecting to the cursor pulls from every slot of the view, including menu icons.
            event.setCancelled(true);
        } else if (slot >= top.getSize()) {
            if (!event.getClick().isShiftClick()) {
                event.setCancelled(!holder.hasEditable);
                return;
            }
            // Vanilla would shift the stack into any menu slot, so move it into editable slots ourselves.
            event.setCancelled(true);
            ItemStack moving = event.getCurrentItem();
            if (!holder.hasEditable || XItemStack.isEmpty(moving)) {
                return;
            }
            int remaining = moving.getAmount();
            for (int i = 0; i < top.getSize() && remaining > 0; i++) {
                ItemStack existing = top.getItem(i);
                if (!holder.editable(i) || XItemStack.isEmpty(existing) || !existing.isSimilar(moving)) {
                    continue;
                }
                int added = Math.min(existing.getMaxStackSize() - existing.getAmount(), remaining);
                if (added > 0) {
                    existing.setAmount(existing.getAmount() + added);
                    top.setItem(i, existing);
                    remaining -= added;
                }
            }
            for (int i = 0; i < top.getSize() && remaining > 0; i++) {
                if (!holder.editable(i) || XItemStack.notEmpty(top.getItem(i))) {
                    continue;
                }
                ItemStack copy = moving.clone();
                copy.setAmount(Math.min(moving.getMaxStackSize(), remaining));
                top.setItem(i, copy);
                remaining -= copy.getAmount();
            }
            moving.setAmount(remaining);
            event.setCurrentItem(remaining > 0 ? moving : null);
        } else if (holder.editable(slot)) {
            return;
        } else {
            event.setCancelled(true);
            Button button = holder.buttons.get(slot);
            if (button == null) {
                return;
            }
            button.click(player, event.getClick());
            if (holder.menu.updateAfterClick) {
                holder.menu.update(player);
            }
        }
        // The client already predicted the vanilla outcome, resync once the server state has settled.
        LibraryPlugin.getInstance().getScheduler().runTaskLater(LibraryPlugin.getInstance().getPlugin(), player::updateInventory, 1L);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getInventory();
        if (!(top.getHolder() instanceof Menu.Holder)) {
            return;
        }
        Menu.Holder holder = (Menu.Holder) top.getHolder();
        for (int slot : event.getRawSlots()) {
            if (slot < top.getSize() && !holder.editable(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu.Holder) || !(event.getPlayer() instanceof Player)) {
            return;
        }
        Menu.Holder holder = (Menu.Holder) event.getInventory().getHolder();
        Player player = (Player) event.getPlayer();
        if (holder.updater != null) {
            holder.updater.cancel();
        }
        if (holder.menu.onClose != null) {
            holder.menu.onClose.accept(player);
        }
        Menu previous = holder.menu.previous != null ? holder.menu.previous.apply(player) : null;
        if (previous == null) {
            return;
        }
        // The client ignores an open sent while it is still closing, and a click may already be opening the next menu.
        LibraryPlugin.getInstance().getScheduler().runTaskLater(LibraryPlugin.getInstance().getPlugin(), () -> {
            if (player.isOnline() && Menu.opened(player) == null) {
                previous.open(player);
            }
        }, 2L);
    }
}
