package com.theyugin.nee.handler;

import com.theyugin.nee.ExporterRunner;
import com.theyugin.nee.render.RenderState;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.util.StackUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class OnTickHandler {
    @SubscribeEvent
    public void renderIconsHandler(TickEvent.RenderTickEvent event) throws InterruptedException {
        if (StackRenderer.isEnabled() && ExporterRunner.isRunning() && !RenderState.renderQueue.isEmpty()) {
            StackRenderer.initialize();
            for (int i = 0; i < 256; i++) {
                String itemDef = RenderState.takeItemToRender();
                if (itemDef != null) {
                    byte[] result = StackRenderer.renderIcon(StackUtils.itemStackFromName(itemDef));
                    RenderState.renderCache.put(itemDef, result);
                    RenderState.putRenderResult(result);
                }
            }
            StackRenderer.uninitialize();
        }
    }
}
