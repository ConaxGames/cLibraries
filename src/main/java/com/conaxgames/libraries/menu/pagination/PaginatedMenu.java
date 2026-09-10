package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        private int maxPerPage = 45;
        private int[] contentSlots;
        private Function<Player, List<Button>> entries = player -> Collections.emptyList();
        private int previousSlot = 48;
        private int nextSlot = 50;
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

        public Builder set(int slot, Button button) {
            globals.put(slot, button);
            return this;
        }

        public Builder fill(Button filler) {
            this.filler = filler;
            return this;
        }

        public Builder previous(Menu previousMenu) {
            this.previousMenu = player -> previousMenu;
            return this;
        }

        public Builder previous(Function<Player, Menu> previousMenu) {
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
            int[] slots = contentSlots != null ? contentSlots : IntStream.range(0, maxPerPage).toArray();
            Menu[] self = new Menu[1];
            int[] page = {0};
            int[] total = {1};
            Button back = Button.builder(XMaterial.RED_DYE).name("&cPrevious Page").onClick((player, type) -> {
                page[0]--;
                self[0].open(player);
            }).build();
            Button next = Button.builder(XMaterial.GREEN_DYE).name("&aNext Page").onClick((player, type) -> {
                page[0]++;
                self[0].open(player);
            }).build();

            // Menu renders before resolving the title, so the counters are already current when it is built.
            Menu.Builder builder = Menu.builder(player -> title + " (" + (page[0] + 1) + "/" + total[0] + ")")
                    .rows(rows)
                    .refreshInPlace(false)
                    .previous(previousMenu)
                    .autoUpdate(updateTicks)
                    .fill(filler)
                    .render((player, layout) -> {
                        List<Button> all = entries.apply(player);
                        total[0] = Math.max(1, (all.size() + slots.length - 1) / slots.length);
                        page[0] = Math.min(page[0], total[0] - 1);
                        int start = page[0] * slots.length;
                        for (int i = 0; i < slots.length && start + i < all.size(); i++) {
                            layout.set(slots[i], all.get(start + i));
                        }
                        if (page[0] > 0) {
                            layout.set(previousSlot, back);
                        }
                        if (page[0] < total[0] - 1) {
                            layout.set(nextSlot, next);
                        }
                    });
            globals.forEach(builder::set);
            self[0] = builder.build();
            return self[0];
        }
    }
}
