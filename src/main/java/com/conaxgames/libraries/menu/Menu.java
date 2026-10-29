package com.conaxgames.libraries.menu;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import com.conaxgames.libraries.util.scheduler.Scheduler;
import com.cryptomorin.xseries.inventory.XInventoryView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Menu {

    private static boolean listening;

    @FunctionalInterface
    public interface Renderer {
        void render(Player player, Layout layout);
    }

    private final Function<Player, String> title;
    private final int rows;
    private final Map<Integer, Button> buttons;
    private final Renderer renderer;
    private final Button filler;
    private final long updateTicks;
    private final boolean refreshInPlace;
    private final Consumer<Player> onOpen;
    final boolean updateAfterClick;
    final Consumer<Player> onClose;
    final Function<Player, Menu> previous;

    private Menu(Builder builder) {
        this.title = builder.title;
        this.rows = builder.rows;
        this.buttons = new HashMap<>(builder.buttons);
        this.renderer = builder.renderer;
        this.filler = builder.filler;
        this.updateTicks = builder.updateTicks;
        this.updateAfterClick = builder.updateAfterClick;
        this.refreshInPlace = builder.refreshInPlace;
        this.onOpen = builder.onOpen;
        this.onClose = builder.onClose;
        this.previous = builder.previous;
    }

    public static Builder builder(String title) {
        String translated = CC.translate(title);
        return new Builder(player -> translated);
    }

    public static Builder builder(Function<Player, String> title) {
        return new Builder(player -> CC.translate(title.apply(player)));
    }

    public static Menu opened(Player player) {
        Holder holder = holder(player);
        return holder != null ? holder.menu : null;
    }

    public static void close(Player player) {
        Holder holder = holder(player);
        if (holder != null) {
            holder.exiting = true;
            player.closeInventory();
        }
    }

    private static Holder holder(Player player) {
        InventoryHolder top = XInventoryView.of(player.getOpenInventory()).getTopInventory().getHolder();
        return top instanceof Holder ? (Holder) top : null;
    }

    public void open(Player player) {
        show(player, false);
    }

    public void update(Player player) {
        show(player, true);
    }

    private void show(Player player, boolean update) {
        if (!Bukkit.isPrimaryThread()) {
            LibraryPlugin.getInstance().getScheduler().runTask(LibraryPlugin.getInstance().getPlugin(), () -> show(player, update));
            return;
        }
        // Registered on first use, menus may be built before the library is enabled.
        if (!listening) {
            listening = true;
            Bukkit.getPluginManager().registerEvents(new ButtonListener(), LibraryPlugin.getInstance().getPlugin());
        }
        Holder current = holder(player);
        boolean reopening = current != null && current.menu == this;
        if (update && !reopening) {
            return;
        }
        Map<Integer, Button> layout = new HashMap<>(buttons);
        if (renderer != null) {
            renderer.render(player, new Layout(layout));
        }
        int size = rows * 9;
        if (size == 0) {
            for (int slot : layout.keySet()) {
                size = Math.max(size, slot / 9 * 9 + 9);
            }
            size = Math.min(54, Math.max(9, size));
        }
        // Updates always refresh in place, the title only changes when the inventory is replaced.
        Holder holder = reopening && (update || refreshInPlace) && current.inventory.getSize() == size ? current : null;
        if (holder == null) {
            String name = title.apply(player);
            // 1.8 rejects titles over 32 characters, the limit was dropped in 1.9.
            if (!VersioningChecker.supports("1.9") && name.length() > 32) {
                name = name.substring(0, 32);
            }
            holder = new Holder(this, size, name);
        }
        boolean hasEditable = false;
        for (int slot = 0; slot < size; slot++) {
            if (filler != null) {
                layout.putIfAbsent(slot, filler);
            }
            Button button = layout.get(slot);
            boolean editable = button != null && button.editable;
            hasEditable |= editable;
            // Editable slots belong to the viewer once seeded, refreshes must not wipe what they placed.
            if (editable && reopening && slot < current.inventory.getSize() && current.editable(slot)) {
                holder.inventory.setItem(slot, current.inventory.getItem(slot));
            } else {
                holder.inventory.setItem(slot, button != null ? button.icon : null);
            }
        }
        holder.buttons = layout;
        holder.hasEditable = hasEditable;
        if (holder == current) {
            return;
        }
        if (reopening) {
            current.replacing = true;
        }
        // Null when another plugin cancelled the InventoryOpenEvent, so nothing is on screen to keep updated.
        boolean opened = player.openInventory(holder.inventory) != null;
        if (reopening) {
            current.replacing = false;
        }
        if (!opened) {
            return;
        }
        if (updateTicks > 0L) {
            holder.updater = LibraryPlugin.getInstance().getScheduler()
                    .runTaskTimerCancellable(LibraryPlugin.getInstance().getPlugin(), () -> update(player), updateTicks, updateTicks);
        }
        if (!reopening && onOpen != null) {
            onOpen.accept(player);
        }
    }

    static final class Holder implements InventoryHolder {

        final Menu menu;
        private final Inventory inventory;
        Map<Integer, Button> buttons;
        boolean hasEditable;
        boolean replacing;
        boolean exiting;
        Scheduler.CancellableTask updater;

        private Holder(Menu menu, int size, String title) {
            this.menu = menu;
            this.inventory = Bukkit.createInventory(this, size, title);
        }

        boolean editable(int slot) {
            Button button = buttons.get(slot);
            return button != null && button.editable;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    public static final class Layout {

        private final Map<Integer, Button> buttons;

        private Layout(Map<Integer, Button> buttons) {
            this.buttons = buttons;
        }

        public Layout set(int slot, Button button) {
            buttons.put(slot, button);
            return this;
        }
    }

    public static final class Builder {

        private final Function<Player, String> title;
        private final Map<Integer, Button> buttons = new HashMap<>();
        private int rows;
        private Renderer renderer;
        private Button filler;
        private long updateTicks;
        private boolean updateAfterClick = true;
        private boolean refreshInPlace = true;
        private Consumer<Player> onOpen;
        private Consumer<Player> onClose;
        private Function<Player, Menu> previous;

        private Builder(Function<Player, String> title) {
            this.title = title;
        }

        public Builder rows(int rows) {
            if (rows < 1 || rows > 6) {
                throw new IllegalArgumentException("Menus have 1 to 6 rows: " + rows);
            }
            this.rows = rows;
            return this;
        }

        public Builder set(int slot, Button button) {
            buttons.put(slot, button);
            return this;
        }

        public Builder fill(Button filler) {
            this.filler = filler;
            return this;
        }

        public Builder render(Renderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder autoUpdate(long updateTicks) {
            this.updateTicks = updateTicks;
            return this;
        }

        public Builder updateAfterClick(boolean updateAfterClick) {
            this.updateAfterClick = updateAfterClick;
            return this;
        }

        public Builder refreshInPlace(boolean refreshInPlace) {
            this.refreshInPlace = refreshInPlace;
            return this;
        }

        public Builder onOpen(Consumer<Player> onOpen) {
            this.onOpen = onOpen;
            return this;
        }

        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder previous(Function<Player, Menu> previous) {
            this.previous = previous;
            return this;
        }

        public Menu build() {
            return new Menu(this);
        }
    }
}
