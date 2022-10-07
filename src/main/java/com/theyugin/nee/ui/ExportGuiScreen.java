package com.theyugin.nee.ui;

import static net.minecraft.util.StatCollector.translateToLocal;

import com.theyugin.nee.ExporterRunner;
import java.sql.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ExportGuiScreen extends GuiScreen {
    private static GuiButton exportButton;

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        exportButton = new GuiButton(1, 10, 40, translateToLocal("nee.button.export"));
        this.buttonList.add(exportButton);
        super.initGui();
    }

    @Override
    public void drawScreen(int mx, int my, float partTicks) {
        drawDefaultBackground();
        exportButton.xPosition = (width / 2) - (exportButton.width / 2);
        super.drawScreen(mx, my, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.equals(exportButton)) {
            new ExporterRunner().run();
        } else super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
