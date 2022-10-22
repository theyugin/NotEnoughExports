package com.theyugin.nee.handler;

import static com.theyugin.nee.input.KeyBindings.guiKey;

import com.theyugin.nee.ui.ExportGuiScreen;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

public class KeyBindingsHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(InputEvent.KeyInputEvent event) {
        if (guiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ExportGuiScreen());
        }
    }
}
