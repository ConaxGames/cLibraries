package com.conaxgames.libraries.redis.message;

import java.util.HashMap;
import java.util.Map;

/**
 * A single MessageTypeResolver that can resolve action strings into MessageTypeInterface enums.
 */
public class UniversalMessageTypeResolver implements MessageTypeResolver {

    private final Map<String, MessageTypeInterface> messageTypeMap = new HashMap<>();

    public UniversalMessageTypeResolver(Class<? extends MessageTypeInterface>... enums) {
        // Populate the resolver with all available message types from provided enums
        for (Class<? extends MessageTypeInterface> enumClass : enums) {
            for (MessageTypeInterface enumConstant : enumClass.getEnumConstants()) {
                messageTypeMap.put(enumConstant.name(), enumConstant);
            }
        }
    }

    @Override
    public MessageTypeInterface resolve(String action) {
        return messageTypeMap.getOrDefault(action, MessageTypeEnum.UNKNOWN);
    }
}
