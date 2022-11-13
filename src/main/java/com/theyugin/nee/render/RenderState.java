package com.theyugin.nee.render;

import lombok.val;

import java.util.concurrent.*;

public class RenderState {
    public static final ConcurrentMap<RenderQuery, byte[]> renderCache = new ConcurrentHashMap<>();
    public static final BlockingQueue<RenderQuery> renderQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<byte[]> resultItemQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<byte[]> resultFluidQueue = new LinkedBlockingQueue<>();

    public static boolean isRenderQueueEmpty() {
        return renderQueue.isEmpty();
    }

    public static void render() {
        val renderQuery = getRenderQuery();
        if (renderQuery != null) {
            if (hasResultCached(renderQuery)) {
                switch (renderQuery.renderType()) {
                    case ITEM:
                        putItemRenderResult(getCachedResult(renderQuery));
                        break;
                    case FLUID:
                        putFluidRenderResult(getCachedResult(renderQuery));
                        break;
                }
            } else {
                byte[] result;
                switch (renderQuery.renderType()) {
                    case ITEM:
                        result = StackRenderer.renderIcon(renderQuery.getItemStack());
                        cacheResult(renderQuery, result);
                        putItemRenderResult(result);
                        break;
                    case FLUID:
                        result = StackRenderer.renderIcon(renderQuery.getFluidStack());
                        cacheResult(renderQuery, result);
                        putFluidRenderResult(result);
                        break;
                }
            }
        }
    }

    public static RenderQuery getRenderQuery() {
        return renderQueue.poll();
    }

    public static boolean hasResultCached(RenderQuery key) {
        return renderCache.containsKey(key);
    }

    public static byte[] getCachedResult(RenderQuery key) {
        return renderCache.get(key);
    }

    public static void putItemRenderResult(byte[] result) {
        try {
            resultItemQueue.put(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void putFluidRenderResult(byte[] result) {
        try {
            resultFluidQueue.put(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void queueRender(RenderQuery query) {
        try {
            renderQueue.put(query);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getItemRenderResult() {
        try {
            return resultItemQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static byte[] getFluidRenderResult() {
        try {
            return resultFluidQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static void cacheResult(RenderQuery key, byte[] value) {
        renderCache.put(key, value);
    }
}
