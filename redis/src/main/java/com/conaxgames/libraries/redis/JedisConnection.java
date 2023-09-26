package com.conaxgames.libraries.redis;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class JedisConnection {

    @Getter
    public static JedisConnection instance;
    public final JavaPlugin plugin;
    public final String instanceId;

    public JedisConnection(JavaPlugin plugin, String instanceId) {
        instance = this;

        this.plugin = plugin;
        this.instanceId = instanceId;
    }

    public void toConsole(String message) {
        plugin.getLogger().info("[cLib-Jedis] " + message);
    }

}
