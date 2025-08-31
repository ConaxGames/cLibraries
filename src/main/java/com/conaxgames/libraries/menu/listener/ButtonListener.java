package com.conaxgames.libraries.menu.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.menu.MenuBackEvent;
import com.conaxgames.libraries.event.impl.menu.MenuCloseEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ButtonListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {

            if (!openMenu.isNoncancellingInventory()) {
                // Has the player clicked the bottom inventory? Their inventory?..
                if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Player) {
                    event.setCancelled(true);
                }
            }

            if (event.getSlot() != event.getRawSlot()) {
                if (event.getClick().equals(ClickType.DOUBLE_CLICK) || event.getClick().equals(ClickType.NUMBER_KEY)) {
                    event.setCancelled(true);
                }
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                    if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
                        player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                        event.setCurrentItem(null);
                    } else if (event.getCurrentItem() != null) {
                        event.setCancelled(true);
                    }
                }
                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Menu newMenu;
                Button button = openMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
                if (!(cancel || event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }
                button.clicked(player, event.getSlot(), event.getClick());
                if (Menu.currentlyOpenedMenus.containsKey(player.getName()) &&
                        (newMenu = Menu.currentlyOpenedMenus.get(player.getName())) == openMenu &&
                        newMenu.isUpdateAfterClick()) {
                    newMenu.buttonUpdate(player);
                    event.setCancelled(cancel);
                }
                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(LibraryPlugin.getInstance().getPlugin(), player::updateInventory, 1L);
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
                    player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                    event.setCurrentItem(null);
                } else if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {

            LibraryPlugin.getInstance().getScheduler().runTaskLater(() -> {
                Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());

                if (openMenu.getPrevious() != null) {
                    MenuBackEvent backEvent = new MenuBackEvent(player, openMenu, openMenu.getPrevious());
                    backEvent.call();
                    if (!backEvent.isCancelled()) {
                        if (newMenu == null) { // only go back if there isn't a new menu opened?
                            openMenu.getPrevious().openMenu(player);
                        }
                    }
                } else if (newMenu == null) { // player didn't open a new menu
                    new MenuCloseEvent(player, openMenu).call();
                }
            }, 2L);

            openMenu.onClose(player);

            Menu.cancelCheck(player);
            Menu.currentlyOpenedMenus.remove(player.getName());
        }
    }
}

