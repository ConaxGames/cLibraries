package com.conaxgames.libraries.menu;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.listener.ButtonListener;
import com.conaxgames.libraries.message.CC;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Menu {

    private static final Map<UUID, Menu> OPEN_MENUS = new ConcurrentHashMap<>();
    private static final Map<UUID, Scheduler.CancellableTask> UPDATE_TASKS = new ConcurrentHashMap<>();

    static {
        Bukkit.getServer().getPluginManager().registerEvents(new ButtonListener(), LibraryPlugin.getInstance().getPlugin());
    }

    @FunctionalInterface
    public interface Renderer {
        void render(Player player, Layout layout);
    }

    private final Function<Player, String> title;
    private final int rows;
    private final Map<Integer, Button> staticButtons;
    private final Renderer renderer;
    private final Button filler;
    private final long updateTicks;
    private final boolean updateAfterClick;
    private final boolean refreshInPlace;
    private final Consumer<Player> onOpen;
    private final Consumer<Player> onClose;
    private final Menu previous;
    private final Predicate<Player> previousCondition;

    private Menu(Builder builder) {
        this.title = builder.title;
        this.rows = builder.rows;
        this.staticButtons = builder.buttons;
        this.renderer = builder.renderer;
        this.filler = builder.filler;
        this.updateTicks = builder.updateTicks;
        this.updateAfterClick = builder.updateAfterClick;
        this.refreshInPlace = builder.refreshInPlace;
        this.onOpen = builder.onOpen;
        this.onClose = builder.onClose;
        this.previous = builder.previous;
        this.previousCondition = builder.previousCondition;
    }

    public static Builder builder(String title) {
        return new Builder(player -> CC.translate(title));
    }

    public static Builder builder(Function<Player, String> title) {
        return new Builder(player -> CC.translate(title.apply(player)));
    }

    public static Menu opened(Player player) {
        return OPEN_MENUS.get(player.getUniqueId());
    }

    public void open(Player player) {
        Runnable open = () -> {
            UUID id = player.getUniqueId();
            Map<Integer, Button> layout = render(player);
            int size = resolveSize(layout);

            Inventory top = XInventoryView.of(player.getOpenInventory()).getTopInventory();
            if (refreshInPlace
                    && top.getHolder() instanceof Holder existing
                    && existing.menu == this
                    && existing.viewerId.equals(id)
                    && top.getSize() == size) {
                fill(existing, layout, size);
                beginSession(player);
                return;
            }

            Holder holder = new Holder(this, id);
            Inventory inv = Bukkit.createInventory(holder, size, title.apply(player));
            holder.inventory = inv;
            fill(holder, layout, size);
            player.openInventory(inv);
            beginSession(player);
        };
        if (Bukkit.isPrimaryThread()) {
            open.run();
        } else {
            LibraryPlugin.getInstance().getScheduler().runTask(LibraryPlugin.getInstance().getPlugin(), open);
        }
    }

    public void update(Player player) {
        Inventory top = XInventoryView.of(player.getOpenInventory()).getTopInventory();
        if (!(top.getHolder() instanceof Holder holder) || holder.menu != this || !holder.viewerId.equals(player.getUniqueId())) {
            return;
        }
        Map<Integer, Button> layout = render(player);
        int size = resolveSize(layout);
        if (top.getSize() != size) {
            open(player);
            return;
        }
        fill(holder, layout, size);
    }

    public Menu previous(Player player) {
        if (previous != null && previousCondition != null && !previousCondition.test(player)) {
            return null;
        }
        return previous;
    }

    public Inventory inventory(Player player) {
        Inventory top = XInventoryView.of(player.getOpenInventory()).getTopInventory();
        if (!(top.getHolder() instanceof Holder holder) || holder.menu != this || !holder.viewerId.equals(player.getUniqueId())) {
            return null;
        }
        return top;
    }

    public boolean updateAfterClick() {
        return updateAfterClick;
    }

    public void closed(Player player) {
        if (onClose != null) {
            onClose.accept(player);
        }
    }

    public static void endSession(UUID id) {
        cancelUpdates(id);
        OPEN_MENUS.remove(id);
    }

    private Map<Integer, Button> render(Player player) {
        Map<Integer, Button> layout = new HashMap<>(staticButtons);
        if (renderer != null) {
            renderer.render(player, new Layout(layout));
        }
        return layout;
    }

    private int resolveSize(Map<Integer, Button> layout) {
        int size = rows > 0 ? rows * 9 : autoSize(layout);
        if (filler != null) {
            for (int slot = 0; slot < size; slot++) {
                layout.putIfAbsent(slot, filler);
            }
        }
        return size;
    }

    private static int autoSize(Map<Integer, Button> layout) {
        int highest = -1;
        for (int slot : layout.keySet()) {
            if (slot > highest) {
                highest = slot;
            }
        }
        return highest < 0 ? 9 : Math.min(54, ((highest + 9) / 9) * 9);
    }

    private void fill(Holder holder, Map<Integer, Button> layout, int size) {
        boolean seeded = holder.filled;
        holder.slotButtons = layout;
        holder.hasEditable = false;
        holder.filled = true;
        for (int slot = 0; slot < size; slot++) {
            Button button = layout.get(slot);
            if (button != null && button.editable()) {
                holder.hasEditable = true;
                // Only seed the initial stack once; player-placed items survive refreshes.
                if (!seeded) {
                    holder.inventory.setItem(slot, button.icon());
                }
                continue;
            }
            holder.inventory.setItem(slot, button != null ? button.icon() : null);
        }
    }

    private void beginSession(Player player) {
        UUID id = player.getUniqueId();
        cancelUpdates(id);
        OPEN_MENUS.put(id, this);
        if (onOpen != null) {
            onOpen.accept(player);
        }
        if (updateTicks <= 0L) {
            return;
        }
        Scheduler.CancellableTask task = LibraryPlugin.getInstance().getScheduler().runTaskTimerCancellable(
                LibraryPlugin.getInstance().getPlugin(),
                () -> {
                    if (!player.isOnline()) {
                        endSession(id);
                        return;
                    }
                    update(player);
                },
                updateTicks,
                updateTicks
        );
        UPDATE_TASKS.put(id, task);
    }

    private static void cancelUpdates(UUID id) {
        Scheduler.CancellableTask task = UPDATE_TASKS.remove(id);
        if (task != null) {
            task.cancel();
        }
    }

    public static final class Holder implements InventoryHolder {

        public final Menu menu;
        public final UUID viewerId;
        private Map<Integer, Button> slotButtons = Map.of();
        private boolean hasEditable;
        private boolean filled;
        Inventory inventory;

        Holder(Menu menu, UUID viewerId) {
            this.menu = menu;
            this.viewerId = viewerId;
        }

        public Button button(int slot) {
            return slotButtons.get(slot);
        }

        public boolean editable(int slot) {
            Button button = slotButtons.get(slot);
            return button != null && button.editable();
        }

        public boolean hasEditable() {
            return hasEditable;
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
            if (button != null) {
                buttons.put(slot, button);
            }
            return this;
        }

        public Layout set(int row, int col, Button button) {
            return set(row * 9 + col, button);
        }

        public Layout editable(int slot) {
            return editable(slot, null);
        }

        public Layout editable(int slot, ItemStack initial) {
            buttons.put(slot, Button.editable(initial));
            return this;
        }
    }

    public static final class Builder {

        private final Function<Player, String> title;
        private final Map<Integer, Button> buttons = new HashMap<>();
        private int rows = 0;
        private Renderer renderer;
        private Button filler;
        private long updateTicks = 0L;
        private boolean updateAfterClick = true;
        private boolean refreshInPlace = true;
        private Consumer<Player> onOpen;
        private Consumer<Player> onClose;
        private Menu previous;
        private Predicate<Player> previousCondition;

        private Builder(Function<Player, String> title) {
            this.title = title;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder set(int slot, Button button) {
            if (button != null) {
                buttons.put(slot, button);
            }
            return this;
        }

        public Builder set(int row, int col, Button button) {
            return set(row * 9 + col, button);
        }

        public Builder editable(int slot) {
            return editable(slot, null);
        }

        public Builder editable(int slot, ItemStack initial) {
            buttons.put(slot, Button.editable(initial));
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
            return previous(previous, null);
        }

        public Builder previous(Menu previous, Predicate<Player> condition) {
            this.previous = previous;
            this.previousCondition = condition;
            return this;
        }

        public Menu build() {
            return new Menu(this);
        }
    }
}
