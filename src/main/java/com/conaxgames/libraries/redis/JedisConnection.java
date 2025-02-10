package com.conaxgames.libraries.redis;

import lombok.Getter;

import java.util.function.Supplier;
import java.util.logging.Logger;

@Getter
public class JedisConnection {

    @Getter
    public static JedisConnection instance;
    public final Logger logger;
    public final String instanceId;
    public boolean enabled;

    public JedisConnection(Logger logger, String instanceId, Supplier<Boolean> isEnabled) {
        instance = this;

        this.logger = logger;
        this.instanceId = instanceId;
        this.enabled = isEnabled.get();
    }

    public void toConsole(String message) {
        this.logger.info("[cLib-Jedis] " + message);
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
