package com.conaxgames.libraries.redis.storage;

import com.conaxgames.libraries.redis.JedisCredentials;
import com.conaxgames.libraries.redis.JedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.Map;

public class JedisStorage extends JedisImplementation {

    public JedisStorage(JedisCredentials jedisSettings) {
        super(jedisSettings);
    }

    public String get(String channel, String key) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.hget(channel, key);
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisStorage: Error getting value for key " + key + " from channel " + channel + ": " + e.getMessage());
            return null;
        } finally {
            this.cleanup(jedis);
        }
    }

    public void remove(String channel, String key) {
        Jedis jedis = this.getJedis();
        try {
            jedis.hdel(channel, key);
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisStorage: Error removing key " + key + " from channel " + channel + ": " + e.getMessage());
        } finally {
            this.cleanup(jedis);
        }
    }

    public void set(String channel, String key, String value) {
        Jedis jedis = this.getJedis();
        try {
            jedis.hset(channel, key, value);
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisStorage: Error setting value for key " + key + " in channel " + channel + ": " + e.getMessage());
        } finally {
            this.cleanup(jedis);
        }
    }

    public Map<String, String> getAll(String channel) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.hgetAll(channel);
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisStorage: Error getting all values from channel " + channel + ": " + e.getMessage());
            return null;
        } finally {
            this.cleanup(jedis);
        }
    }

    public boolean exists(String channel, String key) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.hexists(channel, key);
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisStorage: Error checking existence of key " + key + " in channel " + channel + ": " + e.getMessage());
            return false;
        } finally {
            this.cleanup(jedis);
        }
    }

    public void setMulti(String channel, Map<String, String> keyValuePairs) {
        Jedis jedis = this.getJedis();
        try {
            Transaction transaction = jedis.multi();
            for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
                transaction.hset(channel, entry.getKey(), entry.getValue());
            }
            transaction.exec();
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisStorage: Error setting multiple values in channel " + channel + ": " + e.getMessage());
        } finally {
            this.cleanup(jedis);
        }
    }
}
