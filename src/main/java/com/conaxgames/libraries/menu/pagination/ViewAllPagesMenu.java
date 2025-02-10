package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends Menu {

    PaginatedMenu menu;

    public PaginatedMenu getMenu() {
        return this.menu;
    }

    @Override
    public String getTitle(Player player) {
        return this.menu.getPrePaginatedTitle(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        //buttons.put(size(getButtons()) - 1, new BackButton(this.menu));

        int index = 0;
        for (int pageNumber = 1; pageNumber <= this.menu.getPages(player); ++pageNumber) {
            buttons.put(index++, new JumpToPageButton(pageNumber, this.menu));
        }
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @ConstructorProperties(value={"menu"})
    public ViewAllPagesMenu(PaginatedMenu menu) {
        this.menu = menu;
    }

//    @Override
//    public int size(Map<Integer, Button> buttons) {
//        return 54;
//        // this fixes the issue, bredda tell me where 45 is coming from
//    }
}

