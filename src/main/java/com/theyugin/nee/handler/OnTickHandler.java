package com.theyugin.nee.handler;

import com.theyugin.nee.component.ExporterRunner;
import com.theyugin.nee.render.RenderState;
import com.theyugin.nee.render.RenderType;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.util.StackUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;

public class OnTickHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderIconsHandler(TickEvent.RenderTickEvent event) throws InterruptedException {
        if (StackRenderer.isEnabled()
                && ExporterRunner.isRunning()
                && !RenderState.isRenderQueueEmpty()
                && (event.phase == TickEvent.Phase.END)) {
            StackRenderer.initialize();
            for (int i = 0; i < 512; i++) {
                val renderQuery = RenderState.getRenderQuery();
                if (renderQuery != null) {
                    if (renderQuery.renderType == RenderType.ITEM) {
                        val result = StackRenderer.renderIcon(StackUtils.itemStackFromName(renderQuery.query));
                        RenderState.cacheResult(renderQuery, result);
                        RenderState.putItemRenderResult(result);
                    } else {
                        val result = StackRenderer.renderIcon(StackUtils.fluidStackFromName(renderQuery.query));
                        RenderState.cacheResult(renderQuery, result);
                        RenderState.putFluidRenderResult(result);
                    }
                }
            }
            StackRenderer.uninitialize();
        }
    }
}
