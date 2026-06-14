package com.conaxgames.libraries.message.center;

import com.conaxgames.libraries.message.CC;
import org.bukkit.entity.Player;

public final class Center {

    private static final int CENTER_PX = 154;
    private static final int SPACE_WIDTH = DefaultFontInfo.SPACE.getLength() + 1;

    private Center() {}

    public static String getCentered(String message) {
        if (message == null || message.isEmpty()) return message;

        var translated = CC.translate(message);
        int messagePx = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (int i = 0; i < translated.length(); i++) {
            char c = translated.charAt(i);
            if (c == '\u00a7') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                var font = DefaultFontInfo.getDefaultFontInfo(c);
                messagePx += isBold ? font.getBoldLength() : font.getLength();
                messagePx++;
            }
        }

        int padding = (CENTER_PX - messagePx / 2) / SPACE_WIDTH;
        return " ".repeat(Math.max(0, padding)) + translated;
    }

    public static void sendCenteredMessage(Player player, String message) {
        player.sendMessage(getCentered(message));
    }
}
