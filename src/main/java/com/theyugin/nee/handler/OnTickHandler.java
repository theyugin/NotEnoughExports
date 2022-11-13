package com.theyugin.nee.handler;

import com.theyugin.nee.Config;
import com.theyugin.nee.component.ExporterRunner;
import com.theyugin.nee.render.RenderState;
import com.theyugin.nee.render.RenderType;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.util.StackUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;
import net.minecraft.client.renderer.entity.Render;

public class OnTickHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderIconsHandler(TickEvent.RenderTickEvent event) {
        if (Config.exportIcons()
                && ExporterRunner.isRunning()
                && !RenderState.isRenderQueueEmpty()
                && (event.phase == TickEvent.Phase.END)) {
            StackRenderer.initialize();
            for (int i = 0; i < 512; i++) {
                RenderState.render();
            }
            StackRenderer.uninitialize();
        }
    }
}
