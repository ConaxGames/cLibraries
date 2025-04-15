package com.conaxgames.libraries.menu;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.menu.MenuOpenEvent;
import com.conaxgames.libraries.menu.listener.ButtonListener;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.ItemFlagHelper;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class Menu {

    private final ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();
    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean placeholder = false;
    private boolean hideItemAttributes = true;
    private boolean noncancellingInventory = false;
    private String staticTitle = null;
    private Menu previous;

    public static Map<String, Menu> currentlyOpenedMenus;
    public static Map<String, BukkitRunnable> checkTasks;

    private final Map<String, Inventory> openInventories = new HashMap<>();

    static {
        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), LibraryPlugin.getInstance().getPlugin());
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
    }

    public Menu() {}

    public Menu(String staticTitle) {
        this.staticTitle = Preconditions.checkNotNull(staticTitle, "Menu title cannot be null");
    }

    /**
     * Creates the menu inventory and populates it with buttons.
     */
    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = this.getButtons(player);

        int size = this.size(invButtons);
        Inventory inv = Bukkit.createInventory(player, size, this.getTitle(player));

        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            if (buttonEntry.getKey() > size) continue;

            try {
                inv.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
            } catch (ArrayIndexOutOfBoundsException e) {
                LibraryPlugin.getInstance().getLibraryLogger().toConsole(
                        "Menu",
                        "An item slot in the " + this.staticTitle + " menu was invalid.",
                        e
                );
            }
        }

        if (this.isPlaceholder()) {
            // Fills gray stained glass in all empty slots (good for panel menus)
            Button placeholder = Button.placeholder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial(), (byte) 7, CC.DARK_GRAY + "");
            for (int index = 0; index < size; ++index) {
                if (invButtons.get(index) != null) continue;
                this.buttons.put(index, placeholder);
                inv.setItem(index, placeholder.getButtonItem(player));
            }
        }

        if (isHideItemAttributes()) {
            for (ItemStack item : inv.getContents()) {
                if (item != null) {
                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta != null) {
                        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                        itemMeta.addItemFlags(ItemFlagHelper.getHideTooltipFlag());
                        item.setItemMeta(itemMeta);
                    }
                }
            }
        }

        return inv;
    }

    /**
     * Opens the menu to the player (with firstOpen = true).
     */
    public void openMenu(Player player) {
        openMenu(player, true);
    }

    /**
     * Opens the menu to the player, optionally triggering the open event for the first time.
     */
    public void openMenu(Player player, boolean firstOpen) {
        if (firstOpen) {
            MenuOpenEvent openEvent = new MenuOpenEvent(player, this);
            // If the event is canceled, do not open
            if (openEvent.call()) {
                return;
            }
        }

        if (Bukkit.isPrimaryThread()) {
            open(player);
        } else {
            Bukkit.getScheduler().runTask(LibraryPlugin.getInstance().getPlugin(), () -> open(player));
        }
    }

    private void open(Player player) {
        Inventory inv = this.createInventory(player);
        openInventories.put(player.getName(), inv);
        player.openInventory(inv);
        this.update(player, inv);
    }

    /**
     * Update the inventory contents based on current button definitions.
     */
    public void buttonUpdate(Player player) {
        Inventory inv = openInventories.get(player.getName());
        if (inv != null) {
            inv.setContents(this.createInventory(player).getContents());
        }
    }

    /**
     * Performs automatic updates if the menu is configured to update automatically.
     */
    private void update(final Player player, final Inventory inv) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelCheck(player);
                    currentlyOpenedMenus.remove(player.getName());
                    return;
                }

                if (Menu.this.isAutoUpdate()) {
                    inv.setContents(Menu.this.createInventory(player).getContents());
                }
            }
        };

        runnable.runTaskTimer(LibraryPlugin.getInstance().getPlugin(), 10L, 20L);
        checkTasks.put(player.getName(), runnable);
    }

    /**
     * Cancels any active check task for this player.
     */
    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    /**
     * Calculates the proper size of the inventory based on the highest slot used by buttons.
     */
    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }
        return Math.min(54, (int) (Math.ceil((double) (highest + 1) / 9.0) * 9.0));
    }

    /**
     * Converts x,y coordinates into a slot index for the inventory.
     */
    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    /**
     * Returns the menu title. Override for dynamic titles or use the staticTitle field.
     */
    public String getTitle(Player player) {
        return this.staticTitle;
    }

    /**
     * Retrieve the buttons for this menu. Must be implemented by subclasses.
     */
    public abstract Map<Integer, Button> getButtons(Player player);

    /**
     * Called when the player opens this menu.
     */
    public void onOpen(Player player) {
        // Optional override
    }

    /**
     * Called when the player closes this menu.
     */
    public void onClose(Player player) {
        openInventories.remove(player.getName());
    }

    /**
     * Utility method for computing a "bordered" index.
     */
    public Integer getBorderedIndex(int index) {
        if (index == 7 || index == 16 || index == 25 || index == 34 || index == 43 || index == 52 || index == 61) {
            index += 3;
        } else {
            index++;
        }
        return index;
    }

    /**
     * Computes the size needed given a list size that might skip certain border columns.
     */
    public Integer getBorderedSize(int listSize) {
        return (int) Math.max(27, (Math.min(Math.ceil(listSize / 7.0) + 2, 6) * 9));
    }
}
