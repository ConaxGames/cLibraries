package com.conaxgames.libraries.util;

import org.json.JSONObject;

/**
 * @deprecated This class is marked for removal.
 */
@Deprecated(forRemoval = true)
public class JsonUtils {

    public static int getJSONInteger(JSONObject jsonObject, String key) {
        return getObjectInteger(jsonObject.get(key));
    }

    public static int getObjectInteger(Object object) {
        if (object instanceof Integer) {
            return (int) object;
        } else if (object instanceof Long) {
            return (int) ((long) object);
        }

        throw new RuntimeException("Invalid integer given " + object);
    }

}
