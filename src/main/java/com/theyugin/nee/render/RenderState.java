package com.theyugin.nee.render;

import java.util.concurrent.*;

public class RenderState {
    public static final ConcurrentMap<String, byte[]> renderCache = new ConcurrentHashMap<>();
    public static final BlockingQueue<String> renderQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<byte[]> resultQueue = new LinkedBlockingQueue<>();

    public static synchronized boolean isRenderQueueEmpty() {
        return renderQueue.isEmpty();
    }

    public static String takeItemToRender() {
        return renderQueue.poll();
    }

    public static void putRenderResult(byte[] result) {
        try {
            resultQueue.put(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void queueRender(String itemStack) {
        try {
            renderQueue.put(itemStack);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getRenderResult() {
        try {
            return resultQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
