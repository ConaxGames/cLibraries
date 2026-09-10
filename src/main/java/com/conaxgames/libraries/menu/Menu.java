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
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Menu {

    static {
        Bukkit.getPluginManager().registerEvents(new ButtonListener(), LibraryPlugin.getInstance().getPlugin());
    }

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
        this.buttons = builder.buttons;
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

    private static Holder holder(Player player) {
        InventoryHolder top = XInventoryView.of(player.getOpenInventory()).getTopInventory().getHolder();
        return top instanceof Holder ? (Holder) top : null;
    }

    public void open(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            LibraryPlugin.getInstance().getScheduler().runTask(LibraryPlugin.getInstance().getPlugin(), () -> open(player));
            return;
        }
        Map<Integer, Button> layout = render(player);
        int size = size(layout);
        Holder holder = holder(player);
        if (holder != null && holder.menu == this && refreshInPlace && holder.inventory.getSize() == size) {
            fill(holder, layout, size);
        } else {
            String name = title.apply(player);
            // 1.8 rejects titles over 32 characters, the limit was dropped in 1.9.
            if (!VersioningChecker.supports("1.9") && name.length() > 32) {
                name = name.substring(0, 32);
            }
            holder = new Holder(this, player.getUniqueId(), size, name);
            fill(holder, layout, size);
            // Null when another plugin cancelled the InventoryOpenEvent, so nothing is on screen to keep updated.
            if (player.openInventory(holder.inventory) == null) {
                return;
            }
            if (updateTicks > 0L) {
                holder.updater = LibraryPlugin.getInstance().getScheduler()
                        .runTaskTimerCancellable(LibraryPlugin.getInstance().getPlugin(), () -> update(player), updateTicks, updateTicks);
            }
        }
        if (onOpen != null) {
            onOpen.accept(player);
        }
    }

    public void update(Player player) {
        Holder holder = holder(player);
        if (holder == null || holder.menu != this) {
            return;
        }
        Map<Integer, Button> layout = render(player);
        int size = size(layout);
        if (holder.inventory.getSize() == size) {
            fill(holder, layout, size);
        } else {
            open(player);
        }
    }

    private Map<Integer, Button> render(Player player) {
        Map<Integer, Button> layout = new HashMap<>(buttons);
        if (renderer != null) {
            renderer.render(player, new Layout(layout));
        }
        return layout;
    }

    private int size(Map<Integer, Button> layout) {
        if (rows > 0) {
            return rows * 9;
        }
        int needed = 1;
        for (int slot : layout.keySet()) {
            needed = Math.max(needed, slot / 9 + 1);
        }
        return Math.min(6, needed) * 9;
    }

    private void fill(Holder holder, Map<Integer, Button> layout, int size) {
        if (filler != null) {
            for (int slot = 0; slot < size; slot++) {
                layout.putIfAbsent(slot, filler);
            }
        }
        boolean seeded = holder.buttons != null;
        holder.buttons = layout;
        holder.hasEditable = false;
        for (int slot = 0; slot < size; slot++) {
            Button button = layout.get(slot);
            if (button != null && button.editable()) {
                holder.hasEditable = true;
                // Editable slots belong to the viewer once seeded, refreshes must not wipe what they placed.
                if (seeded) {
                    continue;
                }
            }
            holder.inventory.setItem(slot, button != null ? button.icon() : null);
        }
    }

    public static final class Holder implements InventoryHolder {

        public final Menu menu;
        public final UUID viewerId;
        private final Inventory inventory;
        Map<Integer, Button> buttons;
        boolean hasEditable;
        Scheduler.CancellableTask updater;

        private Holder(Menu menu, UUID viewerId, int size, String title) {
            this.menu = menu;
            this.viewerId = viewerId;
            this.inventory = Bukkit.createInventory(this, size, title);
        }

        public boolean editable(int slot) {
            Button button = buttons.get(slot);
            return button != null && button.editable();
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

        public Layout set(int row, int col, Button button) {
            return set(row * 9 + col, button);
        }

        public Layout editable(int slot) {
            return editable(slot, null);
        }

        public Layout editable(int slot, ItemStack initial) {
            return set(slot, Button.editable(initial));
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
            this.rows = rows;
            return this;
        }

        public Builder set(int slot, Button button) {
            buttons.put(slot, button);
            return this;
        }

        public Builder set(int row, int col, Button button) {
            return set(row * 9 + col, button);
        }

        public Builder fill(Button filler) {
            this.filler = filler;
            return this;
        }

        public Builder render(Renderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder autoUpdate() {
            return autoUpdate(20L);
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

        public Builder previous(Menu previous) {
            this.previous = player -> previous;
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
