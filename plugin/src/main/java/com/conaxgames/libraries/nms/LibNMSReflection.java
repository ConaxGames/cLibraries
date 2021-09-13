package com.conaxgames.libraries.nms;

import java.lang.reflect.Field;

public class LibNMSReflection {

    public static Field access(Class clazz, String string) {
        try {
            Field field = clazz.getDeclaredField(string);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new IllegalArgumentException(clazz.getSimpleName() + ":" + string, noSuchFieldException);
        }
    }

    public static <T> T get(Field field, Object object) {
        try {
            return (T) field.get(object);
        } catch (IllegalAccessException illegalAccessException) {
            throw new IllegalArgumentException(illegalAccessException);
        }
    }

    public static <T> void set(Field field, Object object, T t) {
        try {
            field.set(object, t);
        } catch (IllegalAccessException illegalAccessException) {
            throw new IllegalArgumentException(illegalAccessException);
        }
    }

}
