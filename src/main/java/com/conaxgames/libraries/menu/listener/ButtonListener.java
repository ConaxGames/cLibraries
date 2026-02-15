package com.conaxgames.libraries.menu.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.menu.MenuBackEvent;
import com.conaxgames.libraries.event.impl.menu.MenuCloseEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ButtonListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu == null) return;

        if (!openMenu.isNoncancellingInventory()
                && event.getClickedInventory() != null
                && event.getClickedInventory().getHolder() instanceof Player) {
            event.setCancelled(true);
        }

        if (event.getSlot() != event.getRawSlot()) {
            if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.NUMBER_KEY) {
                event.setCancelled(true);
            }
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
                    player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                    event.setCurrentItem(null);
                }
            }
            return;
        }

        if (openMenu.getButtons().containsKey(event.getSlot())) {
            Button button = openMenu.getButtons().get(event.getSlot());
            boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
            if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    player.getInventory().addItem(event.getCurrentItem());
                }
            } else {
                event.setCancelled(cancel);
            }
            button.clicked(player, event.getSlot(), event.getClick());
            Menu current = Menu.currentlyOpenedMenus.get(player.getName());
            if (current == openMenu && current.isUpdateAfterClick()) {
                current.buttonUpdate(player);
            }
            if (event.isCancelled()) {
                LibraryPlugin.getInstance().getScheduler().runTaskLater(LibraryPlugin.getInstance().getPlugin(), player::updateInventory, 1L);
            }
        } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            event.setCancelled(true);
            if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
                player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                event.setCurrentItem(null);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu == null) return;

        LibraryPlugin.getInstance().getScheduler().runTaskLater(LibraryPlugin.getInstance().getPlugin(), () -> {
            Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());
            if (openMenu.getPrevious() != null) {
                MenuBackEvent backEvent = new MenuBackEvent(player, openMenu, openMenu.getPrevious());
                backEvent.call();
                if (!backEvent.isCancelled() && newMenu == null) {
                    openMenu.getPrevious().openMenu(player);
                }
            } else if (newMenu == null) {
                new MenuCloseEvent(player, openMenu).call();
            }
        }, 2L);

        openMenu.onClose(player);
        Menu.cancelCheck(player);
        Menu.currentlyOpenedMenus.remove(player.getName());
    }
}
