package com.conaxgames.libraries.menu.menus;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.buttons.BooleanButton;
import com.conaxgames.libraries.util.Callback;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {

    private final String title;
    private final String details;
    private Callback<Boolean> response;

    @ConstructorProperties(value={"title", "response"})
    public ConfirmMenu(String title, Callback<Boolean> response, String details) {
        this.title = title;
        this.details = details;
        this.response = response;
        setPlaceholder(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();

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
        return 9 * 3;
    }
}

