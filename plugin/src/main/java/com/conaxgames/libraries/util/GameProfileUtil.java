package com.conaxgames.libraries.util;

import com.mojang.authlib.GameProfile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @since 10/9/2017
 */
public class GameProfileUtil {

	public static GameProfile clone(GameProfile gameProfile) {
		GameProfile newProfile = new GameProfile(gameProfile.getId(), gameProfile.getName());
		newProfile.getProperties().putAll(gameProfile.getProperties());
		return newProfile;
	}


	// todo: this wont work anymore witrh jdk 16
	public static GameProfile setName(GameProfile gameProfile, String newName) {
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");

			// wrapping setAccessible
			AccessController.doPrivileged((PrivilegedAction) () -> {
				modifiersField.setAccessible(true);
				return null;
			});

			Field nameField = GameProfile.class.getDeclaredField("name");
			modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
			nameField.setAccessible(true);
			nameField.set(gameProfile, newName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameProfile;
	}
}
