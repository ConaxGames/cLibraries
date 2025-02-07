package com.conaxgames.libraries.config.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ConfigMenuData {

    public final String name;
    public final String title;
    public final int size;
    public final boolean fillGlass;
    public final boolean hideItemAttributes;
    public final boolean updateOnClick;
    public final boolean autoUpdate;

    public final String back; // Back can be null.
    public final List<ConfigButtonData> buttons;

}
