package com.theyugin.nee.ui;

import com.theyugin.nee.config.ExportConfigOption;
import com.theyugin.nee.export.ExporterRunner;
import com.theyugin.nee.util.NEEUtils;
import cpw.mods.fml.client.GuiScrollingList;
import lombok.val;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

public class ExportGuiScreen extends GuiScreen {
    private static GuiButton exportButton;
    private GuiScrollingList scrollingList;

    private static String exportLabel() {
        return "Run export";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        int width = 300;
        val optionList = new ExportGuiOptionList(this, width, (this.width - width) / 2);
        for (val option : ExportConfigOption.values()) {
            if (option.shown()) {
                optionList.addOption(new ExportGuiOption(option.description, option::toggle, option::get));
            }
        }
        scrollingList = optionList;
        exportButton = new GuiButton(1, 10, height - 50, exportLabel());
        this.buttonList.add(exportButton);
        super.initGui();
    }

    @Override
    public void drawScreen(int mx, int my, float partTicks) {
        drawDefaultBackground();
        drawRect(0, 0, width, 60, 0xFF222222);
        drawRect(0, height - 60, width, height, 0xFF222222);

        exportButton.xPosition = (width / 2) - (exportButton.width / 2);
        exportButton.yPosition = height - 50;
        scrollingList.drawScreen(mx, my, partTicks);

        super.drawScreen(mx, my, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.equals(exportButton)) {
            NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Started export");
            new ExporterRunner().run();
        } else super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
