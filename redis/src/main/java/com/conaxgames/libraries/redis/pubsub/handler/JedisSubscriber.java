package com.conaxgames.libraries.redis.pubsub.handler;

import com.conaxgames.libraries.redis.JedisConnection;
import com.conaxgames.libraries.redis.JedisCredentials;
import com.conaxgames.libraries.redis.pubsub.SubscribeObject;
import com.conaxgames.libraries.redis.subscription.generator.JsonJedisSubscriptionGenerator;
import com.conaxgames.libraries.redis.subscription.model.JedisSubscriptionGenerator;
import com.conaxgames.libraries.redis.subscription.model.JedisSubscriptionHandler;
import com.google.gson.JsonObject;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JedisSubscriber<K> {

    private static final Map<Class<?>, JedisSubscriptionGenerator<?>> GENERATORS = new HashMap<>();

    static {
        GENERATORS.put(JsonObject.class, new JsonJedisSubscriptionGenerator());
    }

    protected final String channel;
    private final JedisConnection connection;
    private final Class<K> typeParameter;
    private final JedisCredentials jedisSettings;
    private final Jedis jedis;

    @Getter
    private JedisPubSub pubSub;

    private JedisSubscriptionHandler<K> jedisSubscriptionHandler;

    /**
     * Requires the {@link JedisCredentials} and a channel to listen to.
     */
    public JedisSubscriber(JedisConnection connection, JedisCredentials jedisSettings, String channel, Class<K> typeParameter,
                           JedisSubscriptionHandler<K> jedisSubscriptionHandler) {
        this.connection = connection;
        this.jedisSettings = jedisSettings;
        this.channel = channel;
        this.typeParameter = typeParameter;
        this.jedisSubscriptionHandler = jedisSubscriptionHandler;

        this.pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                JedisSubscriptionGenerator<K> jedisSubscriptionGenerator = (JedisSubscriptionGenerator<K>) GENERATORS.get(typeParameter);

                if (jedisSubscriptionGenerator != null) {
                    K object = jedisSubscriptionGenerator.generateSubscription(message);
                    if (object instanceof JsonObject) {
                        JedisSubscriber.this.jedisSubscriptionHandler.subscribe(object, new SubscribeObject().from((JsonObject) object));
                    } else {
                        JedisConnection.getInstance().toConsole("JedisSubscriber: Received object is not of type JsonObject");
                    }
                } else {
                    JedisConnection.getInstance().toConsole("JedisSubscriber: Jedis GENERATOR Type was invalid");
                }
            }
        };

        this.jedis = new Jedis(this.jedisSettings.getAddress(), this.jedisSettings.getPort());
        this.authenticate();
        this.attemptConnect();
    }

    /**
     * Checks the {@link JedisCredentials} if there is a password, and if there is it will authenticate with the password
     * that is given.
     */
    private void authenticate() {
        if (this.jedisSettings.hasPassword()) {
            this.jedis.auth(this.jedisSettings.getPassword());
        }
    }

    /**
     * Creates the thread for the {@link JedisPubSub} to be subscribed to on the channel which is targeted.
     */
    private void attemptConnect() {
        new Thread(() -> {
            try {
                this.jedis.subscribe(this.pubSub, this.channel);
                this.connection.toConsole("JedisSubscriber: Jedis is now reading on " + this.channel);
            } catch (Exception e) {
                e.printStackTrace();
                if (this.connection.isEnabled()) { // can't reconnect if plugin is disabled
                    this.connection.toConsole("JedisSubscriber: Jedis channel (" + this.channel + ") has lost connection...");

                    try {
                        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                        scheduler.scheduleAtFixedRate(() -> {
                            JedisConnection jedisConnection = JedisConnection.getInstance();
                            jedisConnection.toConsole("JedisSubscriber: Attempting to reconnect JedisSubscriber (" + channel + ")");

                            this.closeConnection();
                            this.attemptConnect();

                            if (jedis != null && pubSub.isSubscribed()) {
                                jedisConnection.toConsole("JedisSubscriber: JedisSubscriber (" + channel + ") has reconnected");
                                scheduler.shutdown();
                            } else {
                                jedisConnection.toConsole("JedisSubscriber: JedisSubscriber (" + channel + ") will attempt a reconnect in 15 seconds...");
                            }
                        }, 10, 10, TimeUnit.SECONDS);
                    } catch (IllegalArgumentException exception) {
                        this.connection.toConsole("Unable to define thread pool, can't start jedis-reconnect task...");
                    }
                }
            }
        }).start();
    }

    /**
     * Closes the {@link JedisPubSub} connection and it will close the {@link Jedis} connection
     */
    public void closeConnection() {
        try {
            if (this.pubSub != null) {
                this.pubSub.unsubscribe();
            }

            if (this.jedis != null) {
                this.jedis.close();
            }
        } catch (Exception e) {
            boolean pubSubConnected = (pubSub != null && pubSub.isSubscribed());
            this.connection.toConsole("JedisSubscriber: Unable to close connection for (" + this.channel + ")... is it still connected? " + pubSubConnected);
        }
    }

}