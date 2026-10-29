package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.util.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.IntStream;

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
        private int[] contentSlots;
        private Function<Player, List<Button>> entries = player -> Collections.emptyList();
        private int previousSlot = -1;
        private int nextSlot = -1;
        private Button filler;
        private long updateTicks;
        private Function<Player, Menu> previousMenu;

        private Builder(String title) {
            this.title = title;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder maxPerPage(int maxPerPage) {
            this.contentSlots = IntStream.range(0, maxPerPage).toArray();
            return this;
        }

        public Builder contentSlots(int... contentSlots) {
            this.contentSlots = contentSlots;
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

        public Builder set(int slot, Button button) {
            globals.put(slot, button);
            return this;
        }

        public Builder fill(Button filler) {
            this.filler = filler;
            return this;
        }

        public Builder previous(Function<Player, Menu> previousMenu) {
            this.previousMenu = previousMenu;
            return this;
        }

        public Builder autoUpdate(long updateTicks) {
            this.updateTicks = updateTicks;
            return this;
        }

        public Menu build() {
            int footer = (rows - 1) * 9;
            int[] slots = contentSlots != null ? contentSlots : IntStream.range(0, footer).toArray();
            int previousSlot = this.previousSlot >= 0 ? this.previousSlot : footer + 3;
            int nextSlot = this.nextSlot >= 0 ? this.nextSlot : footer + 5;
            // The menu is shared between viewers, so each keeps its own {page, total} until they close it.
            Map<Player, int[]> pages = new WeakHashMap<>();
            Function<Player, int[]> state = player -> pages.computeIfAbsent(player, p -> new int[]{0, 1});
            Button back = Button.of(ItemBuilder.of(XMaterial.RED_DYE).name("&cPrevious Page").build(), (player, type) -> {
                state.apply(player)[0]--;
                Menu.opened(player).open(player);
            });
            Button next = Button.of(ItemBuilder.of(XMaterial.GREEN_DYE).name("&aNext Page").build(), (player, type) -> {
                state.apply(player)[0]++;
                Menu.opened(player).open(player);
            });

            // Menu renders before resolving the title, so the counters are already current when it is built.
            Menu.Builder builder = Menu.builder(player -> {
                        int[] page = state.apply(player);
                        return title + " (" + (page[0] + 1) + "/" + page[1] + ")";
                    })
                    .rows(rows)
                    .refreshInPlace(false)
                    .previous(previousMenu)
                    .autoUpdate(updateTicks)
                    .fill(filler)
                    .onClose(pages::remove)
                    .render((player, layout) -> {
                        int[] page = state.apply(player);
                        List<Button> all = entries.apply(player);
                        page[1] = Math.max(1, (all.size() + slots.length - 1) / slots.length);
                        page[0] = Math.min(page[0], page[1] - 1);
                        int start = page[0] * slots.length;
                        for (int i = 0; i < slots.length && start + i < all.size(); i++) {
                            layout.set(slots[i], all.get(start + i));
                        }
                        if (page[0] > 0) {
                            layout.set(previousSlot, back);
                        }
                        if (page[0] < page[1] - 1) {
                            layout.set(nextSlot, next);
                        }
                    });
            globals.forEach(builder::set);
            return builder.build();
        }
    }
}
