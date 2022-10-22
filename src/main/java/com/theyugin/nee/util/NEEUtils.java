package com.theyugin.nee.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class NEEUtils {
    public static void sendPlayerMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
