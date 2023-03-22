package com.conaxgames.libraries.nms.v1_19_R3.management.utility;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityNMSManager extends com.conaxgames.libraries.nms.management.utility.UtilityNMSManager {

    private static final Pattern PATTERN = Pattern.compile("&(#\\w{6})");

    @Override
    public String translateHex(String message) {
        Matcher matcher = PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', message));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
        }

        return matcher.appendTail(buffer).toString();
    }
}
