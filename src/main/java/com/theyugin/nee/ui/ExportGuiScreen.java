package com.theyugin.nee.ui;

import com.theyugin.nee.Config;
import com.theyugin.nee.LoadedMods;
import com.theyugin.nee.component.ExporterRunner;
import com.theyugin.nee.component.export.IExporter;
import com.theyugin.nee.util.NEEUtils;
import cpw.mods.fml.client.GuiScrollingList;
import lombok.val;
import lombok.var;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.world.ChunkEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

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
                new ExportGuiOption("export thaumcraft", Config::toggleExportThaumcraft, Config::exportThaumcraft)
            );
        scrollingList = optionList;
        exportButton = new GuiButton(1, 10, height - 50, exportLabel());
        this.buttonList.add(exportButton);
        super.initGui();
    }

    public boolean exporting() {
        return ExporterRunner.exporting();
    }

    @Override
    public void drawScreen(int mx, int my, float partTicks) {
        drawDefaultBackground();
        drawRect(0, 0, width, 60, 0xFF222222);
        drawRect(0, height - 60, width, height, 0xFF222222);
        int middle = (width / 2) - (exportButton.width / 2);

        exportButton.xPosition = middle;
        exportButton.yPosition = height - 50;
        exportButton.enabled = !exporting();

        if (exporting() && ExporterRunner.isRunning()) {
            drawRect((this.width - 300) / 2, 60, (this.width + 300) / 2, this.height - 60, 0x88000000);
            var labelPosition = 60;
            for (IExporter exporter : ExporterRunner.loadedExporters) {
                String status;
                EnumChatFormatting color;

                if (exporter.running()) {
                    status = String.format(
                            "Exporting %s: %d/%d", exporter.name(), exporter.progress(), exporter.total());
                    color = EnumChatFormatting.BLUE;
                } else {
                    status = String.format("Exporting %s: done %d", exporter.name(), exporter.total());
                    color = EnumChatFormatting.GREEN;
                }

                mc.fontRenderer.drawString(color + status, middle, labelPosition, 0xFFFFFF, true);
                labelPosition += 10;
            }
        } else {
            scrollingList.drawScreen(mx, my, partTicks);
        }

        super.drawScreen(mx, my, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!exporting()) {
            if (button.equals(exportButton)) {
                NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Started export");
                ExporterRunner.run();
            }
        } else super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
