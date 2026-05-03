package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.JedisConnection;
import com.conaxgames.libraries.redis.JedisCredentials;
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

    public JedisListener(JedisCredentials jedisSettings, String channel, Object parameter) {
        this.jedisSettings = jedisSettings;
        this.channel = channel;
        this.parameter = parameter != null ? parameter : new JsonObject();
        this.listen();
    }

    public JedisListener(JedisCredentials jedisSettings, String channel) {
        this(jedisSettings, channel, null);
    }

    public abstract void respond(String channel, Object data);

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
                            Thread.sleep(1000);
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
                listenerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
