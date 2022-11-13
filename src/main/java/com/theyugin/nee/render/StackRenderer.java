package com.theyugin.nee.render;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class StackRenderer {
    private static final int imageDim = 64;
    private static Framebuffer framebuffer = null;

    public static void initialize() {
        framebuffer = new Framebuffer(imageDim, imageDim, true);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, 1.0, 1.0, 0.0, -100.0, 100.0);
        GL11.glPushMatrix();

        double scaleFactor = 1 / 16.0;
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        FloatBuffer ambientColour = BufferUtils.createFloatBuffer(4);
        ambientColour.put(new float[] {0.1f, 0.1f, 0.1f, 1f}).flip();
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, ambientColour);

        FloatBuffer diffuseColour = BufferUtils.createFloatBuffer(4);
        diffuseColour.put(new float[] {1f, 1f, 1f, 1f}).flip();
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, diffuseColour);

        IntBuffer lightPosition = BufferUtils.createIntBuffer(4);
        lightPosition.put(new int[] {-1, 6, -1, 0}).flip();
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glShadeModel(GL11.GL_FLAT);

        framebuffer.bindFramebuffer(true);
        OpenGlHelper.func_153171_g(GL30.GL_READ_FRAMEBUFFER, framebuffer.framebufferObject);
    }

    public static byte[] renderIcon(ItemStack itemStack) {
        clearBuffer();
        GuiContainerManager.drawItem(0, 0, itemStack);
        return readPixels();
    }

    public static byte[] renderIcon(FluidStack fluidStack) {
        if (fluidStack == null) {
            return null;
        }
        Fluid fluid = fluidStack.getFluid();
        IIcon icon = fluid.getIcon();
        if (icon == null) {
            return new byte[] {};
        }
        // Some fluids don't set their icon colour, so we have to blend in the colour.
        int colour = fluid.getColor();
        GL11.glColor3ub(
                (byte) ((colour & 0xFF0000) >> 16), (byte) ((colour & 0x00FF00) >> 8), (byte) (colour & 0x0000FF));

        GuiDraw.changeTexture(TextureMap.locationBlocksTexture);
        GuiDraw.gui.drawTexturedModelRectFromIcon(0, 0, icon, 16, 16);

        // Reset colour blending.
        GL11.glColor4f(1f, 1f, 1f, 1f);
        return readPixels();
    }

    private static byte[] readPixels() {
        ByteBuffer imageByteBuffer = BufferUtils.createByteBuffer(4 * imageDim * imageDim);
        GL11.glReadPixels(0, 0, imageDim, imageDim, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, imageByteBuffer);

        int[] pixels = new int[imageDim * imageDim];
        imageByteBuffer.asIntBuffer().get(pixels);
        int[] flippedPixels = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int x = i % imageDim;
            int y = imageDim - (i / imageDim + 1);
            flippedPixels[i] = pixels[x + imageDim * y];
        }

        BufferedImage image = new BufferedImage(imageDim, imageDim, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, imageDim, imageDim, flippedPixels, 0, imageDim);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return os.toByteArray();
    }

    private static void clearBuffer() {
        GL11.glClearColor(0f, 0f, 0f, 0f);
        GL11.glClearDepth(1D);
        GL11.glClear(16384 | 256);
    }

    public static void uninitialize() {
        framebuffer.unbindFramebuffer();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        framebuffer.deleteFramebuffer();
        framebuffer = null;
    }
}
