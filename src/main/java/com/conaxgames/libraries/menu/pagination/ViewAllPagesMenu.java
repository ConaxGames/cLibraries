package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.buttons.BackButton;
import com.conaxgames.libraries.menu.pagination.buttons.JumpToPageButton;
import com.conaxgames.libraries.util.CC;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ViewAllPagesMenu extends Menu {

    private static final int SIZE = 54;
    private static final int BACK_BUTTON_SLOT = 49;

    private final PaginatedMenu menu;

    public ViewAllPagesMenu(PaginatedMenu menu) {
        this.menu = menu;
    }

    @Override
    public String getTitle(Player player) {
        return CC.GREEN + "View All Pages - " + this.menu.getPrePaginatedTitle(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(BACK_BUTTON_SLOT, new BackButton(this.menu));

        int totalPages = this.menu.getPages(player);
        int currentPage = this.menu.getPage();
        int index = 0;

        for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) {
            if (index == BACK_BUTTON_SLOT) index++;
            JumpToPageButton button = new JumpToPageButton(pageNumber, this.menu);
            button.setGlowing(pageNumber == currentPage);
            buttons.put(index++, button);
        }
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return SIZE;
    }
}
