package com.theyugin.nee.ui;

import com.theyugin.nee.Config;
import com.theyugin.nee.LoadedMods;
import com.theyugin.nee.component.ExporterRunner;
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
        val optionList = new ExportGuiOptionList(
                this,
                width,
                (this.width - width) / 2,
                new ExportGuiOption("export icons", Config::toggleExportIcons, Config::exportIcons),
                new ExportGuiOption(
                        "export crafting table", Config::toggleExportCraftingTable, Config::exportCraftingTable),
                new ExportGuiOption("export catalysts", Config::toggleExportCatalysts, Config::exportCatalysts));
        if (LoadedMods.GREGTECH.isLoaded())
            optionList.addOption(
                    new ExportGuiOption("export gregtech", Config::toggleExportGregtech, Config::exportGregtech));
        if (LoadedMods.THAUMCRAFT.isLoaded())
            optionList.addOption(
                    new ExportGuiOption("export thaumcraft", Config::toggleExportThaumcraft, Config::exportThaumcraft));
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
