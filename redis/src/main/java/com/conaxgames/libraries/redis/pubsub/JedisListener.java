package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.JedisCredentials;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class JedisListener {

    private final JedisCredentials jedisSettings;
    private final String channel;
    private final Object parameter;

    /**
     * Creates an instance with {@link JedisCredentials} and the targeted channel to listen to.
     *
     * @param jedisSettings
     * @param channel
     */
    public JedisListener(JedisCredentials jedisSettings, String channel, Object parameter) {
        this.jedisSettings = jedisSettings;
        this.channel = channel;
        if (parameter == null) {
            this.parameter = new JsonObject();
        } else {
            this.parameter = parameter;
        }

        this.listen();
    }

    public JedisListener(JedisCredentials jedisSettings, String channel) {
        this(jedisSettings, channel, null);
    }

    /**
     * After the {@link JedisListener} reads a message it will be parsed through this method and converts the message to
     * some sort of object, but it'll only support JsonObject which is used by default, or Object which will need to be
     * converted to a valid data type such as String.
     *
     * @param channel
     * @param data
     */
    public abstract void respond(String channel, Object data);

    /**
     * Starts listening to the channel.
     */
    private void listen() {
        new Thread(() -> {
            while (true) {
                Jedis jedis = null;
                try {
                    jedis = JedisListener.this.jedisSettings.getJedisPool().getResource();

                    try {
                        List<String> messages = jedis.blpop(0, JedisListener.this.channel);

                        if (this.parameter instanceof JsonObject) {
                            this.respond(messages.get(0), new JsonParser().parse(messages.get(1)).getAsJsonObject());
                        } else {
                            this.respond(messages.get(0), messages.get(1));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        }).start();
    }
}
