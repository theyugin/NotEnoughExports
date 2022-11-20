package com.theyugin.nee.ui;

import cpw.mods.fml.client.GuiScrollingList;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;

public class ExportGuiOptionList extends GuiScrollingList {
    private final GuiScreen parent;
    private final List<ExportGuiOption> options = new ArrayList<>();
    private boolean buttonPressed = false;

    public ExportGuiOptionList(GuiScreen parent, int width, int left) {
        super(parent.mc, width, parent.height, 60, parent.height - 60, left, 24);
        this.parent = parent;
    }

    public void addOption(ExportGuiOption option) {
        this.options.add(option);
    }

    @Override
    protected int getSize() {
        return options.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {}

    @Override
    protected boolean isSelected(int index) {
        return false;
    }

    @Override
    protected void drawBackground() {}

    @Override
    protected void drawSlot(int listIndex, int slotX, int slotY, int var4, Tessellator tesselator) {
        val item = options.get(listIndex);
        parent.drawString(parent.mc.fontRenderer, item.name(), left + 3, slotY, 0xFFFFFFFF);
        item.drawButton(parent.mc, left + listWidth - 30, slotY, mouseX, mouseY);
        if (item.button.mousePressed(parent.mc, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !buttonPressed) {
                item.toggle();
            }
            buttonPressed = Mouse.isButtonDown(0);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
    }
}
