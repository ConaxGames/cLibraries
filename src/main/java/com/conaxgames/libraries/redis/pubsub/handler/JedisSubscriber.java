package com.conaxgames.libraries.redis.pubsub.handler;

import com.conaxgames.libraries.redis.JedisConnection;
import com.conaxgames.libraries.redis.JedisCredentials;
import com.conaxgames.libraries.redis.message.MessageTypeEnum;
import com.conaxgames.libraries.redis.message.UniversalMessageTypeResolver;
import com.conaxgames.libraries.redis.message.MessageTypeResolver;
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
    private volatile Jedis jedis;

    @Getter
    private JedisPubSub pubSub;

    private JedisSubscriptionHandler<K> jedisSubscriptionHandler;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean isShutdown = false;

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
                try {
                    JedisSubscriptionGenerator<K> jedisSubscriptionGenerator = (JedisSubscriptionGenerator<K>) GENERATORS.get(typeParameter);

                    if (jedisSubscriptionGenerator != null) {
                        K object = jedisSubscriptionGenerator.generateSubscription(message);
                        if (object instanceof JsonObject) {
                            MessageTypeResolver resolver = new UniversalMessageTypeResolver(MessageTypeEnum.class);
                            JedisSubscriber.this.jedisSubscriptionHandler.subscribe(
                                    object,
                                    new SubscribeObject().from((JsonObject) object, resolver)
                            );
                        } else {
                            JedisConnection.getInstance().toConsole("JedisSubscriber: Received object is not of type JsonObject");
                        }
                    } else {
                        JedisConnection.getInstance().toConsole("JedisSubscriber: Jedis GENERATOR Type was invalid");
                    }
                } catch (Exception e) {
                    JedisConnection.getInstance().toConsole("JedisSubscriber: Error processing message: " + e.getMessage());
                }
            }
        };

        this.createNewConnection();
        this.attemptConnect();
    }

    /**
     * Creates a new Jedis connection and authenticates if password is provided.
     */
    private void createNewConnection() {
        try {
            // Close existing connection if it exists
            if (this.jedis != null) {
                try {
                    this.jedis.close();
                } catch (Exception e) {
                    // Ignore close errors
                }
            }
            
            // Create new connection
            this.jedis = new Jedis(this.jedisSettings.getAddress(), this.jedisSettings.getPort());
            
            // Authenticate if password is provided
            if (this.jedisSettings.hasPassword()) {
                this.jedis.auth(this.jedisSettings.getPassword());
                // Verify authentication worked by testing a command
                this.jedis.ping();
                this.connection.toConsole("JedisSubscriber: Authentication successful for (" + this.channel + ")");
            } else {
                // Test the connection even without password
                this.jedis.ping();
            }
            
        } catch (Exception e) {
            this.connection.toConsole("JedisSubscriber: Failed to create new connection for (" + this.channel + "): " + e.getMessage());
            // Clean up failed connection
            if (this.jedis != null) {
                try {
                    this.jedis.close();
                } catch (Exception cleanupError) {
                    // Ignore cleanup errors
                }
                this.jedis = null;
            }
            throw e;
        }
    }

    /**
     * Creates the thread for the {@link JedisPubSub} to be subscribed to on the channel which is targeted.
     */
    private void attemptConnect() {
        if (isShutdown) return;
        
        new Thread(() -> {
            try {
                // Ensure we have a valid connection before subscribing
                if (jedis == null || !jedis.isConnected()) {
                    throw new IllegalStateException("Jedis connection is null or not connected");
                }
                
                // Test connection and authentication with ping before subscribing
                jedis.ping();
                
                // Additional authentication verification if password is required
                if (this.jedisSettings.hasPassword()) {
                    // Try a simple operation to verify authentication is still valid
                    try {
                        jedis.clientId(); // This requires authentication to work
                    } catch (Exception authTest) {
                        // Re-authenticate if the test fails
                        this.connection.toConsole("JedisSubscriber: Re-authenticating connection for (" + this.channel + ")");
                        jedis.auth(this.jedisSettings.getPassword());
                        jedis.ping(); // Verify auth worked
                    }
                }
                
                this.connection.toConsole("JedisSubscriber: Subscribing to channel (" + this.channel + ")");
                
                // Subscribe to the channel
                this.jedis.subscribe(this.pubSub, this.channel);
            } catch (Exception e) {
                this.connection.toConsole("JedisSubscriber: Connection failed for (" + this.channel + "): " + e.getMessage());
                if (!isShutdown) {
                    scheduleReconnect();
                }
            }
        }).start();
    }

    private void scheduleReconnect() {
        if (isShutdown) return;
        
        try {
            scheduler.scheduleAtFixedRate(() -> {
                if (isShutdown) {
                    scheduler.shutdown();
                    return;
                }

                JedisConnection jedisConnection = JedisConnection.getInstance();
                jedisConnection.toConsole("JedisSubscriber: Attempting to reconnect JedisSubscriber (" + channel + ")");

                try {
                    this.closeConnection();
                    this.createNewConnection(); // Create new connection with authentication
                    
                    // Wait a moment for the connection to be fully established
                    Thread.sleep(1000);
                    
                    // Verify connection is still valid before attempting to connect
                    if (jedis != null && jedis.isConnected()) {
                        // Test authentication again before subscribing
                        if (this.jedisSettings.hasPassword()) {
                            try {
                                jedis.auth(this.jedisSettings.getPassword());
                                jedis.ping();
                            } catch (Exception authError) {
                                jedisConnection.toConsole("JedisSubscriber: Authentication failed during reconnect for (" + channel + "): " + authError.getMessage());
                                return; // Skip this reconnect attempt
                            }
                        }
                        
                        this.attemptConnect();
                        
                        // Give the connection attempt time to complete
                        Thread.sleep(2000);
                        
                        // Check if connection succeeded
                        if (jedis != null && pubSub.isSubscribed()) {
                            jedisConnection.toConsole("JedisSubscriber: JedisSubscriber (" + channel + ") has reconnected");
                            scheduler.shutdown();
                            return;
                        }
                    }
                } catch (Exception e) {
                    jedisConnection.toConsole("JedisSubscriber: Failed to reconnect (" + channel + "): " + e.getMessage());
                }

                jedisConnection.toConsole("JedisSubscriber: JedisSubscriber (" + channel + ") will attempt a reconnect in 10 seconds...");
            }, 10, 10, TimeUnit.SECONDS);
        } catch (IllegalArgumentException exception) {
            this.connection.toConsole("Unable to define thread pool, can't start jedis-reconnect task...");
        }
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

    public void shutdown() {
        isShutdown = true;
        closeConnection();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}