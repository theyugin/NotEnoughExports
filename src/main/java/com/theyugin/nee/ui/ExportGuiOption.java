package com.theyugin.nee.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;

import java.util.function.Supplier;

public class ExportGuiOption {
    private final String name;
    private final Supplier<Boolean> valueSupplier;

    public Boolean isEnabled() {
        return valueSupplier.get();
    }

    final GuiButton button;
    public final Runnable toggleCallback;
    public ExportGuiOption(String name, Runnable toggleCallback, Supplier<Boolean> valueSupplier) {
        this.name = name;
        this.toggleCallback = toggleCallback;
        this.valueSupplier = valueSupplier;
        button = new GuiButton(100,0, 0, 14, 12, "");
    }
    public void drawButton(Minecraft mc, int x, int y, int mX, int mY) {
        button.xPosition = x;
        button.yPosition = y;
        button.displayString = buttonLabel();
        button.drawButton(mc, mX, mY);
    }
    public String buttonLabel() {
        return color() + (isEnabled() ? "✔" : "✘");
    }
    public String color() {
        return isEnabled() ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString();
    }
    public void toggle() {
        toggleCallback.run();
    }
    public String name() {
        return name;
    }
}
