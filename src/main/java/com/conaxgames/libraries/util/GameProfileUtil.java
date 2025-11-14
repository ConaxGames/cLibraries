package com.conaxgames.libraries.util;

import com.mojang.authlib.GameProfile;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * @deprecated This class is marked for removal.
 * @since 10/9/2017
 */
@Deprecated(forRemoval = true)
public class GameProfileUtil {

	public static GameProfile clone(GameProfile gameProfile) {
		GameProfile newProfile = new GameProfile(gameProfile.getId(), gameProfile.getName());
		newProfile.getProperties().putAll(gameProfile.getProperties());
		return newProfile;
	}

	public static GameProfile setName(GameProfile gameProfile, String newName) {
		try {
			Field nameField = GameProfile.class.getDeclaredField("name");

			nameField.setAccessible(true);

			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle setter = lookup.unreflectSetter(nameField);

			setter.invoke(gameProfile, newName);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return gameProfile;
	}
}

