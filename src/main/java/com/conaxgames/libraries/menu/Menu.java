package com.conaxgames.libraries.menu;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.menu.MenuOpenEvent;
import com.conaxgames.libraries.menu.listener.ButtonListener;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.conaxgames.libraries.util.scheduler.Scheduler;

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
    private boolean noncancellingInventory = false;
    private String staticTitle = null;
    private Menu previous;

    public static Map<String, Menu> currentlyOpenedMenus;
    public static Map<String, Scheduler.CancellableTask> checkTasks;
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

    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = this.getButtons(player);
        int size = this.size(invButtons);
        Inventory inv = Bukkit.createInventory(player, size, this.getTitle(player));

        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            if (buttonEntry.getKey() > size) continue;
            try {
                ItemStack item = buttonEntry.getValue().getButtonItem(player);
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
            Button placeholderBtn = Button.placeholder(XMaterial.GRAY_STAINED_GLASS_PANE.get(), CC.DARK_GRAY + "");
            for (int index = 0; index < size; ++index) {
                if (invButtons.get(index) != null) continue;
                this.buttons.put(index, placeholderBtn);
                inv.setItem(index, placeholderBtn.getButtonItem(player));
            }
        }

        return inv;
    }

    public void openMenu(Player player) {
        openMenu(player, true);
    }

    public void openMenu(Player player, boolean firstOpen) {
        if (firstOpen) {
            MenuOpenEvent openEvent = new MenuOpenEvent(player, this);
            if (openEvent.call()) {
                return;
            }
        }

        if (Bukkit.isPrimaryThread()) {
            open(player);
        } else {
            LibraryPlugin.getInstance().getScheduler().runTask(LibraryPlugin.getInstance().getPlugin(), () -> open(player));
        }
    }

    private void open(Player player) {
        Inventory inv = this.createInventory(player);
        openInventories.put(player.getName(), inv);
        player.openInventory(inv);
        this.update(player, inv);
    }

    public void buttonUpdate(Player player) {
        Inventory inv = openInventories.get(player.getName());
        if (inv != null) {
            inv.setContents(this.createInventory(player).getContents());
        }
    }

    private void update(final Player player, final Inventory inv) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);

        Scheduler.CancellableTask task = LibraryPlugin.getInstance().getScheduler().runTaskTimerCancellable(
            LibraryPlugin.getInstance().getPlugin(),
            () -> {
                if (!player.isOnline()) {
                    cancelCheck(player);
                    currentlyOpenedMenus.remove(player.getName());
                    return;
                }
                if (Menu.this.isAutoUpdate()) {
                    inv.setContents(Menu.this.createInventory(player).getContents());
                }
            },
            10L, 20L
        );
        checkTasks.put(player.getName(), task);
    }

    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }
        return Math.min(54, (int) (Math.ceil((double) (highest + 1) / 9.0) * 9.0));
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {}

    public void onClose(Player player) {
        openInventories.remove(player.getName());
    }

    public int getBorderedIndex(int index) {
        if (index == 7 || index == 16 || index == 25 || index == 34 || index == 43 || index == 52 || index == 61) {
            index += 3;
        } else {
            index++;
        }
        return index;
    }

    public int getBorderedSize(int listSize) {
        return (int) Math.max(27, (Math.min(Math.ceil(listSize / 7.0) + 2, 6) * 9));
    }
}
