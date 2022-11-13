package com.theyugin.nee.ui;

import com.theyugin.nee.Config;
import com.theyugin.nee.component.ExporterRunner;
import com.theyugin.nee.component.export.IExporter;
import com.theyugin.nee.util.NEEUtils;
import cpw.mods.fml.client.GuiScrollingList;
import lombok.var;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

public class ExportGuiScreen extends GuiScreen {
    private static GuiButton exportButton;
    private GuiScrollingList scrollingList;
    public static final ExportGuiOption exportIcons = new ExportGuiOption("export icons", Config::toggleExportIcons, Config::exportIcons);
    public static final ExportGuiOption exportGregTechOption = new ExportGuiOption("export gregtech", Config::toggleExportGregtech, Config::exportGregtech);
    public static final ExportGuiOption exportCatalysts = new ExportGuiOption("export catalysts", Config::toggleExportCatalysts, Config::exportCatalysts);
    public static final ExportGuiOption exportCraftingTable = new ExportGuiOption("export crafting table", Config::toggleExportCraftingTable, Config::exportCraftingTable);


    private static String exportLabel() {
        return "Run export";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        int width = 300;
        scrollingList = new ExportGuiOptionList(this, width, (this.width - width) / 2,
            exportIcons, exportCatalysts, exportGregTechOption, exportCraftingTable);
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
                if (exportIcons.isEnabled()) {
                    NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Exporting icons...");
                }
            }
        } else super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
