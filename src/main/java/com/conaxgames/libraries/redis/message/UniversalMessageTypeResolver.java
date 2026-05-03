package com.conaxgames.libraries.redis.message;

import java.util.HashMap;
import java.util.Map;

public class UniversalMessageTypeResolver implements MessageTypeResolver {

    private final Map<String, MessageTypeInterface> messageTypeMap = new HashMap<>();

    public UniversalMessageTypeResolver(Class<? extends MessageTypeInterface>... enums) {

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
