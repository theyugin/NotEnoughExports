package com.theyugin.nee.ui;

import com.theyugin.nee.ExporterRunner;
import com.theyugin.nee.export.IExporter;
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
        exportButton = new GuiButton(1, 10, height - 50, exportLabel());
        exportIconsButton = new GuiButton(2, 10, 40, exportIconsLabel());
        this.buttonList.add(exportButton);
        this.buttonList.add(exportIconsButton);
        super.initGui();
    }

    public static boolean exporting() {
        return exporterThread != null && exporterThread.isAlive();
    }

    @Override
    public void drawScreen(int mx, int my, float partTicks) {
        drawDefaultBackground();
        int middle = (width / 2) - (exportButton.width / 2);

        exportButton.xPosition = middle;
        exportButton.yPosition = height - 50;
        exportButton.enabled = !exporting();

        exportIconsButton.xPosition = middle;
        exportIconsButton.enabled = !exporting();

        if (exporting() && ExporterRunner.isRunning()) {
            int labelPosition = 60;
            for (IExporter exporter : ExporterRunner.loadedExporters) {
                String status;
                EnumChatFormatting color;

                if (exporter.running()) {
                    status = String.format("Exporting %s: %d/%d", exporter.name(), exporter.progress(), exporter.total());
                    color = EnumChatFormatting.BLUE;
                } else {
                    status = String.format("Exporting %s: done %d", exporter.name(), exporter.total());
                    color = EnumChatFormatting.GREEN;
                }

                mc.fontRenderer.drawString(color + status, middle, labelPosition, 0xFFFFFF, true);
                labelPosition += 10;
            }
        }

        super.drawScreen(mx, my, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.equals(exportButton)) {
            NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Started export");
            exporterThread = new Thread(new ExporterRunner());
            exporterThread.start();
            if (EXPORT_ICONS) {
                NEEUtils.sendPlayerMessage(EnumChatFormatting.GREEN + "Exporting icons...");
            }
        } else if (button.equals(exportIconsButton)) {
            EXPORT_ICONS = !EXPORT_ICONS;
            StackRenderer.toggleEnabled();
            button.displayString = exportIconsLabel();
        } else if (exporting()) {
        } else super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
