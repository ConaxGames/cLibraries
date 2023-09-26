package com.conaxgames.libraries.redis.subscription.generator;

import com.conaxgames.libraries.redis.subscription.model.JedisSubscriptionGenerator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;

public class JsonJedisSubscriptionGenerator implements JedisSubscriptionGenerator<JsonObject> {

    @Override
    public JsonObject generateSubscription(String message) {
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(message));
            jsonReader.setLenient(true);
            return new JsonParser().parse(jsonReader).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JsonObject();
    }
}
