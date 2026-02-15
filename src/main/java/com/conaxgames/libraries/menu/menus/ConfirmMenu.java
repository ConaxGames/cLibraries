package com.conaxgames.libraries.menu.menus;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.buttons.BooleanButton;
import com.conaxgames.libraries.util.Callback;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {

    private final String title;
    private final String details;
    private final Callback<Boolean> response;

    public ConfirmMenu(String title, Callback<Boolean> response, String details) {
        this.title = title;
        this.details = details;
        this.response = response;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(11, new BooleanButton(true, this.response, details));
        buttons.put(15, new BooleanButton(false, this.response, details));
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}
