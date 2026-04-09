package com.conaxgames.libraries.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MenuInventoryHolder implements InventoryHolder {

    @Getter
    private final Menu menu;
    @Getter
    private final UUID viewerId;
    @Setter
    @Getter
    private Map<Integer, Button> slotButtons;
    private Inventory inventory;

    public MenuInventoryHolder(Menu menu, UUID viewerId) {
        this.menu = menu;
        this.viewerId = viewerId;
        this.slotButtons = Collections.emptyMap();
    }

    static Map<Integer, Button> copyLayout(Map<Integer, Button> source) {
        return new HashMap<>(source);
    }

    public Button getButton(int slot) {
        return slotButtons.get(slot);
    }

    void attachInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }
}
