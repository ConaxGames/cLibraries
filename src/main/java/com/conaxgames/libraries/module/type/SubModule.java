package com.conaxgames.libraries.module.type;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public abstract class SubModule extends Module {

    /**
     * Constructor for {@link SubModule}
     */
    public SubModule(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    /**
     * The parent {@link Module} of this {@link SubModule}.
     */
    public abstract Module getParent();
}
