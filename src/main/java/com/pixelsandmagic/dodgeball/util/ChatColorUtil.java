package com.pixelsandmagic.dodgeball.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Giovanni on 5/5/2023
 */
public class ChatColorUtil {

    private static final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}");

    public static String color(String message) {
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, ChatColor.of(color) + "");
            matcher = hexPattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
