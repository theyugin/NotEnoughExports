package com.theyugin.nee.ui;

import com.theyugin.nee.ExporterRunner;
import com.theyugin.nee.export.CraftingTableExporter;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.util.NEEUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

public class ExportGuiScreen extends GuiScreen {
    private static Thread exporterThread = null;
    private static GuiButton exportButton;
    private static GuiButton exportIconsButton;
    private static boolean EXPORT_ICONS = false;

    private static String exportIconsLabel() {
        return String.format("Export item icons: %b", EXPORT_ICONS);
    }

    private static String exportLabel() {
        return "Run export";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        exportButton = new GuiButton(1, 10, 40, exportLabel());
        exportIconsButton = new GuiButton(2, 10, 60, exportIconsLabel());
        this.buttonList.add(exportButton);
        this.buttonList.add(exportIconsButton);
        super.initGui();
    }

    public static boolean notExporting() {
        return exporterThread == null || !exporterThread.isAlive();
    }

    @Override
    public void drawScreen(int mx, int my, float partTicks) {
        drawDefaultBackground();
        int middle = (width / 2) - (exportButton.width / 2);
        exportButton.xPosition = middle;
        exportButton.yPosition = height - 50;
        exportIconsButton.xPosition = middle;
        exportIconsButton.enabled = notExporting();
        exportButton.enabled = notExporting();

        if (!notExporting()) {
            exportButton.displayString = CraftingTableExporter.getStatus();
        } else {
            exportButton.displayString = exportLabel();
        }

        super.drawScreen(mx, my, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.equals(exportButton)) {
            if (notExporting()) {
                NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Started export");
                ExporterRunner.startRunning();
                exporterThread = new Thread(new ExporterRunner());
                exporterThread.start();
                if (EXPORT_ICONS) {
                    NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Exporting icons...");
                }
            } else {
                NEEUtils.sendPlayerMessage(EnumChatFormatting.RED + "Export already running!");
            }
        } else if (button.equals(exportIconsButton)) {
            EXPORT_ICONS = !EXPORT_ICONS;
            StackRenderer.toggleEnabled();
            button.displayString = exportIconsLabel();
        } else super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
