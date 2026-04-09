package com.conaxgames.libraries.menu.listener;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.menu.MenuBackEvent;
import com.conaxgames.libraries.event.impl.menu.MenuCloseEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.MenuInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class ButtonListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onButtonPress(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof MenuInventoryHolder)) {
            return;
        }
        MenuInventoryHolder holder = (MenuInventoryHolder) top.getHolder();
        if (!holder.getViewerId().equals(player.getUniqueId())) {
            return;
        }
        Menu menu = holder.getMenu();

        if (!menu.isNoncancellingInventory()) {
            if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Player) {
                event.setCancelled(true);
            }
        }

        if (event.getSlot() != event.getRawSlot()) {
            handleBottomInventoryClick(event, player, menu);
            return;
        }

        Button button = holder.getButton(event.getSlot());
        if (button != null) {
            boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
            if (!(cancel || (event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT))) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    player.getInventory().addItem(event.getCurrentItem());
                }
            } else {
                event.setCancelled(cancel);
            }
            button.clicked(player, event.getSlot(), event.getClick());

            UUID id = player.getUniqueId();
            if (Menu.currentlyOpenedMenus.get(id) == menu && menu.isUpdateAfterClick()) {
                menu.buttonUpdate(player);
                event.setCancelled(cancel);
            }
            if (event.isCancelled()) {
                LibraryPlugin.getInstance().getScheduler().runTaskLater(
                        LibraryPlugin.getInstance().getPlugin(),
                        player::updateInventory,
                        1L
                );
            }
        } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            handleShiftIntoTopWhenEmptySlot(event, player, menu);
        }
    }

    private void handleBottomInventoryClick(InventoryClickEvent event, Player player, Menu menu) {
        if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
        }
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            event.setCancelled(true);
            handleShiftIntoTopWhenEmptySlot(event, player, menu);
        }
    }

    private void handleShiftIntoTopWhenEmptySlot(InventoryClickEvent event, Player player, Menu menu) {
        if (menu.isNoncancellingInventory() && event.getCurrentItem() != null) {
            player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
            event.setCurrentItem(null);
        } else if (event.getCurrentItem() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        UUID id = player.getUniqueId();
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof MenuInventoryHolder)) {
            return;
        }
        MenuInventoryHolder holder = (MenuInventoryHolder) inv.getHolder();
        if (!holder.getViewerId().equals(id)) {
            return;
        }
        Menu openMenu = holder.getMenu();

        LibraryPlugin.getInstance().getScheduler().runTaskLater(LibraryPlugin.getInstance().getPlugin(), () -> {
            Menu newMenu = Menu.currentlyOpenedMenus.get(id);

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
        Menu.currentlyOpenedMenus.remove(id);
    }
}
