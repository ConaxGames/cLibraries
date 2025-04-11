package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.JedisCredentials;
import com.conaxgames.libraries.redis.JedisConnection;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class JedisListener {

    private final JedisCredentials jedisSettings;
    private final String channel;
    private final Object parameter;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Thread listenerThread;

    /**
     * Creates an instance with {@link JedisCredentials} and the targeted channel to listen to.
     *
     * @param jedisSettings
     * @param channel
     */
    public JedisListener(JedisCredentials jedisSettings, String channel, Object parameter) {
        this.jedisSettings = jedisSettings;
        this.channel = channel;
        this.parameter = parameter != null ? parameter : new JsonObject();
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
        listenerThread = new Thread(() -> {
            while (isRunning.get()) {
                Jedis jedis = null;
                try {
                    jedis = this.jedisSettings.getJedisPool().getResource();
                    List<String> messages = jedis.blpop(0, this.channel);

                    if (messages != null && messages.size() >= 2) {
                        if (this.parameter instanceof JsonObject) {
                            try {
                                this.respond(messages.get(0), new JsonParser().parse(messages.get(1)).getAsJsonObject());
                            } catch (Exception e) {
                                JedisConnection.getInstance().toConsole("JedisListener: Error parsing JSON message: " + e.getMessage());
                            }
                        } else {
                            this.respond(messages.get(0), messages.get(1));
                        }
                    }
                } catch (Exception e) {
                    if (isRunning.get()) {
                        JedisConnection.getInstance().toConsole("JedisListener: Error processing message: " + e.getMessage());
                        try {
                            Thread.sleep(1000); // Wait before retrying
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        });
        listenerThread.setName("JedisListener-" + channel);
        listenerThread.start();
    }

    public void shutdown() {
        isRunning.set(false);
        if (listenerThread != null) {
            listenerThread.interrupt();
            try {
                listenerThread.join(5000); // Wait up to 5 seconds for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
