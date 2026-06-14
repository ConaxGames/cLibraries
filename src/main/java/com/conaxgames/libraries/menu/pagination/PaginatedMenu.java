package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class PaginatedMenu {

    private PaginatedMenu() {
    }

    public static Builder builder(String title) {
        return new Builder(title);
    }

    public static final class Builder {

        private final String title;
        private final Map<Integer, Button> globals = new HashMap<>();
        private int rows = 6;
        private int maxPerPage = 45;
        private int[] contentSlots;
        private Function<Player, List<Button>> entries = player -> Collections.emptyList();
        private int previousSlot = 48;
        private int nextSlot = 50;
        private ItemStack previousIcon;
        private ItemStack nextIcon;
        private Button filler;
        private long updateTicks = 0L;
        private Menu previousMenu;

        private Builder(String title) {
            this.title = title;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder maxPerPage(int maxPerPage) {
            this.maxPerPage = maxPerPage;
            return this;
        }

        public Builder contentSlots(int... contentSlots) {
            this.contentSlots = contentSlots;
            return this;
        }

        public Builder entries(List<Button> entries) {
            this.entries = player -> entries;
            return this;
        }

        public Builder entries(Function<Player, List<Button>> entries) {
            this.entries = entries;
            return this;
        }

        public Builder previousSlot(int previousSlot) {
            this.previousSlot = previousSlot;
            return this;
        }

        public Builder nextSlot(int nextSlot) {
            this.nextSlot = nextSlot;
            return this;
        }

        public Builder previousIcon(ItemStack previousIcon) {
            this.previousIcon = previousIcon;
            return this;
        }

        public Builder nextIcon(ItemStack nextIcon) {
            this.nextIcon = nextIcon;
            return this;
        }

        public Builder set(int slot, Button button) {
            if (button != null) {
                globals.put(slot, button);
            }
            return this;
        }

        public Builder fill(Button filler) {
            this.filler = filler;
            return this;
        }

        public Builder previous(Menu previousMenu) {
            this.previousMenu = previousMenu;
            return this;
        }

        public Builder autoUpdate() {
            return autoUpdate(20L);
        }

        public Builder autoUpdate(long updateTicks) {
            this.updateTicks = updateTicks;
            return this;
        }

        public Menu build() {
            int[] slots = contentSlots != null ? contentSlots : defaultSlots(maxPerPage);
            int perPage = slots.length;
            ItemStack previous = previousIcon != null ? previousIcon
                    : Button.builder(XMaterial.RED_DYE).name("&cPrevious Page").build().icon();
            ItemStack next = nextIcon != null ? nextIcon
                    : Button.builder(XMaterial.GREEN_DYE).name("&aNext Page").build().icon();

            Menu[] self = new Menu[1];
            int[] page = {0};

            Function<Player, String> titleFunction = player -> {
                int total = totalPages(entries.apply(player).size(), perPage);
                int current = Math.min(page[0], total - 1) + 1;
                return title + " (" + current + "/" + total + ")";
            };

            Menu.Builder builder = Menu.builder(titleFunction)
                    .rows(rows)
                    .refreshInPlace(false);
            if (previousMenu != null) {
                builder.previous(previousMenu);
            }
            if (updateTicks > 0L) {
                builder.autoUpdate(updateTicks);
            }
            globals.forEach(builder::set);
            if (filler != null) {
                builder.fill(filler);
            }

            builder.render((player, layout) -> {
                List<Button> all = entries.apply(player);
                int total = totalPages(all.size(), perPage);
                page[0] = Math.max(0, Math.min(page[0], total - 1));

                int start = page[0] * perPage;
                for (int i = 0; i < perPage && start + i < all.size(); i++) {
                    layout.set(slots[i], all.get(start + i));
                }

                if (page[0] > 0) {
                    layout.set(previousSlot, arrow(previous, () -> {
                        page[0]--;
                        self[0].open(player);
                    }));
                }
                if (page[0] < total - 1) {
                    layout.set(nextSlot, arrow(next, () -> {
                        page[0]++;
                        self[0].open(player);
                    }));
                }
            });

            Menu menu = builder.build();
            self[0] = menu;
            return menu;
        }

        private static Button arrow(ItemStack icon, Runnable onClick) {
            return Button.builder(icon).onClick((player, type) -> onClick.run()).build();
        }

        private static int totalPages(int size, int perPage) {
            return Math.max(1, (int) Math.ceil(size / (double) perPage));
        }

        private static int[] defaultSlots(int count) {
            int[] slots = new int[count];
            for (int i = 0; i < count; i++) {
                slots[i] = i;
            }
            return slots;
        }
    }
}
