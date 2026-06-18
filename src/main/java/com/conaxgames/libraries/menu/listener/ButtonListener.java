package com.conaxgames.libraries.menu.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.cryptomorin.xseries.inventory.XInventoryView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

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
        // Shift-clicks and double-click collects have ambiguous destinations
        // and can move items across non-editable slots, so they stay blocked.
        boolean ambiguous = event.getClick().isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK;
        if (event.getRawSlot() != event.getSlot()) {
            event.setCancelled(!holder.hasEditable() || ambiguous);
            return;
        }
        if (holder.editable(event.getSlot())) {
            event.setCancelled(ambiguous);
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
