package com.conaxgames.libraries.menu;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.menu.MenuOpenEvent;
import com.conaxgames.libraries.menu.listener.ButtonListener;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XItemFlag;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for creating interactive GUI menus in Minecraft.
 * <p>
 * This class provides a framework for creating menus with buttons, automatic updates,
 * and various customization options. It handles inventory creation, button placement,
 * and event management.
 * </p>
 * <p>
 * To create a menu, extend this class and implement the {@link #getButtons(Player)} method
 * to define the layout and functionality of your menu.
 * </p>
 * 
 * @author ConaxGames
 * @version 1.0
 */
@Getter
@Setter
public abstract class Menu {

    /**
     * Map of slot positions to buttons in this menu.
     * Uses a thread-safe implementation to prevent concurrent modification issues.
     */
    private final ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();
    
    /**
     * Whether the menu should automatically update its contents.
     * When true, the menu will refresh every second (20 ticks).
     */
    private boolean autoUpdate = false;
    
    /**
     * Whether the menu should update after a button click.
     * Useful for menus that change state based on user interaction.
     */
    private boolean updateAfterClick = true;
    
    /**
     * Whether empty slots should be filled with placeholder items.
     * When true, empty slots will be filled with gray stained glass panes.
     */
    private boolean placeholder = false;
    
    /**
     * Whether item attributes (enchantments, durability, etc.) should be hidden.
     * When true, all items will have their attributes hidden using XSeries item flags.
     */
    private boolean hideItemAttributes = false;
    
    /**
     * Whether inventory click events should be cancelled.
     * When false, players can take items from the menu.
     */
    private boolean noncancellingInventory = false;
    
    /**
     * The static title of the menu.
     * If null, the title will be determined by the {@link #getTitle(Player)} method.
     */
    private String staticTitle = null;
    
    /**
     * Reference to the previous menu, used for back navigation.
     * Set this when opening a new menu from an existing one to enable back navigation.
     */
    private Menu previous;

    /**
     * Map of currently opened menus by player name.
     * Used to track which menu each player has open.
     */
    public static Map<String, Menu> currentlyOpenedMenus;
    
    /**
     * Map of update tasks by player name.
     * Used to manage automatic menu updates.
     */
    public static Map<String, BukkitRunnable> checkTasks;

    /**
     * Map of open inventories by player name.
     * Used to track which inventory each player has open.
     */
    private final Map<String, Inventory> openInventories = new HashMap<>();

    /**
     * Static initializer that registers the button listener and initializes maps.
     * This is called when the class is loaded.
     */
    static {
        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), LibraryPlugin.getInstance().getPlugin());
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
    }

    /**
     * Default constructor for creating a menu with a dynamic title.
     */
    public Menu() {}

    /**
     * Constructor for creating a menu with a static title.
     * 
     * @param staticTitle The title of the menu, cannot be null
     * @throws NullPointerException if staticTitle is null
     */
    public Menu(String staticTitle) {
        this.staticTitle = Preconditions.checkNotNull(staticTitle, "Menu title cannot be null");
    }

    /**
     * Creates the menu inventory and populates it with buttons.
     * This method handles the creation of the inventory, button placement,
     * placeholder filling, and attribute hiding.
     * 
     * @param player The player for whom the inventory is being created
     * @return The created inventory
     */
    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = this.getButtons(player);

        int size = this.size(invButtons);
        Inventory inv = Bukkit.createInventory(player, size, this.getTitle(player));

        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            if (buttonEntry.getKey() > size) continue;

            try {
                ItemStack item = buttonEntry.getValue().getButtonItem(player);
                if (isHideItemAttributes()) {
                    applyItemFlags(item);
                }
                inv.setItem(buttonEntry.getKey(), item);
            } catch (ArrayIndexOutOfBoundsException e) {
                LibraryPlugin.getInstance().getLibraryLogger().toConsole(
                        "Menu",
                        "An item slot in the " + this.staticTitle + " menu was invalid.",
                        e
                );
            }
        }

        if (this.isPlaceholder()) {
            Button placeholder = Button.placeholder(XMaterial.GRAY_STAINED_GLASS_PANE.get(), (byte) 7, CC.DARK_GRAY + "");
            for (int index = 0; index < size; ++index) {
                if (invButtons.get(index) != null) continue;
                this.buttons.put(index, placeholder);
                ItemStack item = placeholder.getButtonItem(player);
                if (isHideItemAttributes()) {
                    applyItemFlags(item);
                }
                inv.setItem(index, item);
            }
        }

        return inv;
    }

    /**
     * Opens the menu to the player with firstOpen set to true.
     * This is a convenience method that calls {@link #openMenu(Player, boolean)}.
     * 
     * @param player The player to open the menu for
     */
    public void openMenu(Player player) {
        openMenu(player, true);
    }

    /**
     * Opens the menu to the player, optionally triggering the open event.
     * 
     * @param player The player to open the menu for
     * @param firstOpen Whether this is the first time the menu is being opened
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

    /**
     * Internal method to open the menu for a player.
     * Creates the inventory, stores it in the openInventories map,
     * opens it for the player, and starts the update task.
     * 
     * @param player The player to open the menu for
     */
    private void open(Player player) {
        Inventory inv = this.createInventory(player);
        openInventories.put(player.getName(), inv);
        player.openInventory(inv);
        this.update(player, inv);
    }

    /**
     * Updates the inventory contents based on current button definitions.
     * This is useful for refreshing the menu without reopening it.
     * 
     * @param player The player whose menu should be updated
     */
    public void buttonUpdate(Player player) {
        Inventory inv = openInventories.get(player.getName());
        if (inv != null) {
            inv.setContents(this.createInventory(player).getContents());
        }
    }

    /**
     * Performs automatic updates if the menu is configured to update automatically.
     * This method cancels any existing update task, stores the menu in the currentlyOpenedMenus map,
     * calls the onOpen method, and starts a new update task if autoUpdate is enabled.
     * 
     * @param player The player whose menu should be updated
     * @param inv The inventory to update
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
     * This is called when a player closes a menu or opens a new one.
     * 
     * @param player The player whose check task should be cancelled
     */
    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    /**
     * Calculates the proper size of the inventory based on the highest slot used by buttons.
     * The size is always a multiple of 9 (rows) and never exceeds 54 (6 rows).
     * 
     * @param buttons The map of buttons to calculate the size for
     * @return The calculated inventory size
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
     * Useful for creating button layouts using a grid system.
     * 
     * @param x The x coordinate (0-8)
     * @param y The y coordinate (0-5)
     * @return The corresponding slot index
     */
    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    /**
     * Returns the menu title.
     * If staticTitle is set, it will be returned.
     * Otherwise, subclasses can override this method to provide a dynamic title.
     * 
     * @param player The player to get the title for
     * @return The menu title
     */
    public String getTitle(Player player) {
        return this.staticTitle;
    }

    /**
     * Retrieve the buttons for this menu.
     * This method must be implemented by subclasses to define the menu layout.
     * 
     * @param player The player to get the buttons for
     * @return A map of slot positions to buttons
     */
    public abstract Map<Integer, Button> getButtons(Player player);

    /**
     * Called when the player opens this menu.
     * Subclasses can override this method to perform initialization when the menu is opened.
     * 
     * @param player The player who opened the menu
     */
    public void onOpen(Player player) {
        // Optional override
    }

    /**
     * Called when the player closes this menu.
     * This method removes the inventory from the openInventories map.
     * 
     * @param player The player who closed the menu
     */
    public void onClose(Player player) {
        openInventories.remove(player.getName());
    }

    /**
     * Utility method for computing a "bordered" index.
     * This is useful for creating menus with borders around content.
     * 
     * @param index The current index
     * @return The next index, skipping border columns
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
     * This is useful for creating bordered menus with a specific number of content slots.
     * 
     * @param listSize The number of content items
     * @return The calculated inventory size
     */
    public Integer getBorderedSize(int listSize) {
        return (int) Math.max(27, (Math.min(Math.ceil(listSize / 7.0) + 2, 6) * 9));
    }

    /**
     * Applies all item flags to hide attributes and tooltips.
     * This method uses XSeries to apply item flags in a version-compatible way.
     * 
     * @param item The item to apply flags to
     */
    private void applyItemFlags(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            XItemFlag.decorationOnly(meta);
            item.setItemMeta(meta);
        }
    }
}
