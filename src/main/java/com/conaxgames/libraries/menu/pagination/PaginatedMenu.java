package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.pagination.buttons.PageButton;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PaginatedMenu extends Menu {
    private int page = 1;

    @Override
    public String getTitle(Player player) {
        return this.getPrePaginatedTitle(player) + " (" + this.page + "/" + this.getPages(player) + ")";
    }

    public final void modPage(Player player, int mod) {
        this.page += mod;
        this.getButtons().clear();
        this.openMenu(player, false);
    }

    private boolean hasNext(Player player) {
        int pg = getPage() + 1;
        return pg > 0 && getPages(player) >= pg;
    }

    private boolean hasPrevious(Player player) {
        int pg = getPage() + -1;
        return pg > 0 && getPages(player) >= pg;
    }

    public final int getPages(Player player) {
        int buttonAmount = this.getAllPagesButtons(player).size();
        if (buttonAmount == 0) {
            return 1;
        }
        return (int)Math.ceil((double)buttonAmount / (double)this.getMaxItemsPerPage(player));
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        int minIndex = (int)((double)(this.page - 1) * (double)this.getMaxItemsPerPage(player));
        int maxIndex = (int)((double)this.page * (double)this.getMaxItemsPerPage(player));

        int previousSlot = this.previousPageSlot(player);
        int nextSlot = this.nextPageSlot(player);

        HashMap<Integer, Button> buttons = new HashMap<>();

        if (hasPrevious(player)) {
            buttons.put(previousSlot, new PageButton(-1, this));
        }
        if (hasNext(player)) {
            buttons.put(nextSlot, new PageButton(1, this));
        }

        for (Map.Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();
            if (ind < minIndex || ind >= maxIndex) continue;
            buttons.put(ind -= (int)((double)this.getMaxItemsPerPage(player) * (double)(this.page - 1)) - 9, entry.getValue());
        }

        Map<Integer, Button> global = this.getGlobalButtons(player);
        if (global != null) {
            for (Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }
        }

        return buttons;
    }

    public int getMaxItemsPerPage(Player player) {
        return 18;
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    public abstract String getPrePaginatedTitle(Player var1);

    public abstract Map<Integer, Button> getAllPagesButtons(Player var1);

    public abstract int previousPageSlot(Player var1);
    public abstract int nextPageSlot(Player var1);

}

