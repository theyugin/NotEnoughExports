package com.theyugin.nee.input;

import com.theyugin.nee.Tags;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public final class KeyBindings {
    public static final KeyBinding guiKey = new KeyBinding("Open export UI", Keyboard.KEY_LBRACKET, Tags.MODNAME);

    public static void register() {
        ClientRegistry.registerKeyBinding(guiKey);
    }
}
