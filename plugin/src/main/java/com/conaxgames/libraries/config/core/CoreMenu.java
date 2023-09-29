package com.conaxgames.libraries.config.core;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.config.CommentedConfiguration;
import com.conaxgames.libraries.config.core.model.CoreButtonProcessor;
import com.conaxgames.libraries.config.core.model.ConfigButtonData;
import com.conaxgames.libraries.config.core.model.ConfigMenuData;
import com.conaxgames.libraries.config.core.model.CoreButton;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.Config;
import com.cryptomorin.xseries.XMaterial;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreMenu {

    private final JavaPlugin javaPlugin;
    private final String fileDestination;
    private final boolean alwaysSync;
    private final boolean syncOnCreation;
    private final List<String> noSyncSections;

    public List<ConfigMenuData> menus = new ArrayList<>();
    public String baseMenu;

    public CoreMenu(JavaPlugin javaPlugin, String fileDestination, boolean alwaysSync, boolean syncOnCreation, List<String> noSyncSections) {
        this.javaPlugin = javaPlugin;
        this.fileDestination = fileDestination;
        this.alwaysSync = alwaysSync;
        this.syncOnCreation = syncOnCreation;
        this.noSyncSections = noSyncSections;
    }

    public void reload() {
        CommentedConfiguration config = getResource(this.javaPlugin, this.fileDestination, this.alwaysSync, this.syncOnCreation, this.noSyncSections);
        if (config == null) return;

        menus.clear();
        baseMenu = config.getString("base-menu", null);

        ConfigurationSection menuSection = config.getConfigurationSection("menus");
        if (menuSection != null) {
            menuSection.getKeys(false).forEach(name -> {
                String title = menuSection.getString(name + ".title");
                int size = menuSection.getInt(name + ".size");
                String back = (menuSection.contains(name + ".back") ? menuSection.getString(name + ".back") : null);
                boolean fillGlass = menuSection.getBoolean(name + ".settings.fillGlass", false);
                boolean hideItemAttributes = menuSection.getBoolean(name + ".settings.hideItemAttributes", true);
                boolean updateOnClick = menuSection.getBoolean(name + ".settings.updateOnClick", true);
                boolean autoUpdate = menuSection.getBoolean(name + ".settings.autoUpdate", false);

                List<ConfigButtonData> btns = new ArrayList<>();

                ConfigurationSection buttonSection = config.getConfigurationSection("menus." + name + ".buttons");
                if (buttonSection != null) {
                    buttonSection.getKeys(false).forEach(button -> {
                        List<String> actions = buttonSection.getStringList(button + ".actions");
                        List<String> conditions = buttonSection.getStringList(button + ".conditions");
                        String buttonName = buttonSection.getString(button + ".name", "Undefined");
                        String permission = buttonSection.getString(button + ".permission", null);
                        String skull64 = buttonSection.getString(button + ".skull64", null);
                        int buttonSlot = buttonSection.getInt(button + ".slot");
                        boolean shiny = buttonSection.getBoolean(button + ".shiny", false);
                        int buttonMaterialData = buttonSection.getInt(button + ".material-data", 0);
                        List<String> buttonLore = buttonSection.getStringList(button + ".lore");

                        XMaterial buttonMaterial = XMaterial.BEDROCK;
                        try {
                            buttonMaterial = XMaterial.valueOf(buttonSection.getString(button + ".material"));
                        } catch (IllegalArgumentException e) {
                            LibraryPlugin.getInstance().sendDebug("CoreConfigMenu", "Invalid button material for " + button);
                        }

                        ConfigButtonData buttonData = new ConfigButtonData(actions, conditions, buttonName, permission,
                                buttonSlot, buttonMaterial, buttonMaterialData, shiny, buttonLore);
                        buttonData.setSkull64(skull64);
                        btns.add(buttonData);
                    });
                }

                ConfigMenuData coreMenu = new ConfigMenuData(name, title, size, fillGlass, hideItemAttributes, updateOnClick, autoUpdate, back, btns);
                this.menus.add(coreMenu);
            });
        }
    }

    public CommentedConfiguration getResource(JavaPlugin javaPlugin, @NonNull String destination, boolean alwaysSync, boolean syncOnCreation, List<String> noSyncSections) {
        String[] dontSync = (noSyncSections == null ? new String[0] : noSyncSections.toArray(new String[0]));

        String dest = destination.replace(".yml", "");

        Config config = new Config(dest, javaPlugin);
        CommentedConfiguration settings = CommentedConfiguration.loadConfiguration(config.getConfigFile());

        if (alwaysSync || (config.isWasCreated() && syncOnCreation)) {
            try {
                if (settings == null) {
                    LibraryPlugin.getInstance().sendDebug("CoreConfigMenu", "CommentedConfiguration 'settings' was null... (" + dest + ")");
                    return null;
                }

                settings.syncWithConfig(config.getConfigFile(), javaPlugin.getResource(dest + ".yml"), dontSync);
                LibraryPlugin.getInstance().sendDebug("CoreConfigMenu", "Sync'd " + dest + ".yml" + " with file.");
            } catch (Exception exception) {
                LibraryPlugin.getInstance().sendDebug("CoreConfigMenu", "Unable to sync " + dest + ".yml" + " with file.");
                exception.printStackTrace();
            }
            return settings;
        }

        return settings;
    }

    public ConfigMenuData getShopByName(String name) {
        return menus.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ConfigMenuData getBaseShopMenu() {
        return menus.stream().filter(m -> m.getName().equalsIgnoreCase(this.baseMenu)).findFirst().orElse(null);
    }

    public void openBaseMenu(Player player) {
        new CoreConfigMenu(this, getBaseShopMenu()).openMenu(player);
    }

    public void openMenu(Player player, ConfigMenuData data) {
        new CoreConfigMenu(this, data).openMenu(player);
    }

    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //    //

    private static class CoreConfigMenu extends Menu {

        private final CoreMenu coreMenu;
        private final ConfigMenuData data;

        public CoreConfigMenu(CoreMenu coreMenu, ConfigMenuData data) {
            this.coreMenu = coreMenu;
            this.data = data;

            setPlaceholder(data.isFillGlass()); // glass
            setHideItemAttributes(data.isHideItemAttributes());
            setUpdateAfterClick(data.isUpdateOnClick());
            setAutoUpdate(data.isAutoUpdate());
        }

        @Override
        public String getTitle(Player bukkitPlayer) {
            return data.getTitle();
        }

        @Override
        public int size(Map<Integer, Button> buttons) {
            return data.getSize();
        }

        @Override
        public Map<Integer, Button> getButtons(Player bukkitPlayer) {
            Map<Integer, Button> buttons = new HashMap<>();

            List<ConfigButtonData> coreButtons = data.getButtons();
            coreButtons.forEach(coreButton -> {
                if (!coreButton.hasPermission(bukkitPlayer)) return;

                buttons.put(coreButton.getSlot(), new CoreButton(coreButton, (player, slot, clickType) -> {
                    CoreButtonProcessor processor = new CoreButtonProcessor(this.coreMenu, coreButton, player);
                    boolean conditionsMet = processor.matches();
                    if (!conditionsMet) {
                        player.sendMessage(CC.RED + "You did not meet all the conditions to click this button.");
                        return;
                    }

                    processor.execute();
                }));
            });

            return buttons;
        }
    }

}
