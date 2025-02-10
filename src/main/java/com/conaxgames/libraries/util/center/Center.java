package com.conaxgames.libraries.util.center;

import com.conaxgames.libraries.util.CC;
import org.bukkit.entity.Player;

public class Center {

    private final static int CENTER_PX = 154;

    public static String getCentered(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        // translate first so that the spacing can be accounted for
        message = CC.translate(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if(c == '§'){
                previousCode = true;
            } else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;
    }

    public static void sendCenteredMessage(Player player, String message) {
        player.sendMessage(getCentered(message));
    }
}
