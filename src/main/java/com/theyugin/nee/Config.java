package com.theyugin.nee;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
    private static final Configuration configuration = new Configuration(new File("config", Tags.MODID + ".cfg"));

    private static class Defaults {
        public static final Boolean exportIcons = true;
        public static final Boolean exportCatalysts = true;
        public static final Boolean exportCraftingTable = true;
        public static final Boolean exportGregtech = true;
        public static final Boolean exportThaumcraft = true;
    }

    private static class Properties {
        private static Property exportIconsProperty;
        private static Property exportCatalystsProperty;
        private static Property exportCraftingTableProperty;
        private static Property exportGregtechProperty;
        private static Property exportThaumcraftProperty;

        private static void load() {
            exportIconsProperty = configuration.get(
                    Categories.general, "exportIcons", Defaults.exportIcons, "export item and fluid icons");
            exportCatalystsProperty = configuration.get(
                    Categories.general, "exportCatalysts", Defaults.exportCatalysts, "export NEI catalysts");
            exportCraftingTableProperty = configuration.get(
                    Categories.general,
                    "exportCraftingTable",
                    Defaults.exportCraftingTable,
                    "export crafting table recipes");
            exportGregtechProperty = configuration.get(
                    Categories.general, "exportGregtech", Defaults.exportGregtech, "export gregtech recipes");
            exportThaumcraftProperty = configuration.get(
                Categories.general, "exportThaumcraft", Defaults.exportThaumcraft, "export thaumcraft recipes"
            );
        }
    }

    private static class Categories {
        public static final String general = "general";
    }

    private static Boolean exportIcons = Defaults.exportIcons;
    private static Boolean exportCatalysts = Defaults.exportCatalysts;
    private static Boolean exportCraftingTable = Defaults.exportCraftingTable;
    private static Boolean exportGregtech = Defaults.exportGregtech;
    private static Boolean exportThaumcraft = Defaults.exportThaumcraft;

    public static Boolean exportIcons() {
        return exportIcons;
    }

    public static Boolean exportCatalysts() {
        return exportCatalysts;
    }

    public static Boolean exportCraftingTable() {
        return exportCraftingTable;
    }

    public static Boolean exportGregtech() {
        return exportGregtech;
    }

    public static Boolean exportThaumcraft() {
        return exportThaumcraft;
    }

    public static void toggleExportIcons() {
        exportIcons = !exportIcons;
        Properties.exportIconsProperty.set(exportIcons);
        saveConfiguration();
    }

    public static void toggleExportCatalysts() {
        exportCatalysts = !exportCatalysts;
        Properties.exportCatalystsProperty.set(exportCatalysts);
        saveConfiguration();
    }

    public static void toggleExportCraftingTable() {
        exportCraftingTable = !exportCraftingTable;
        Properties.exportCraftingTableProperty.set(exportCraftingTable);
        saveConfiguration();
    }

    public static void toggleExportGregtech() {
        exportGregtech = !exportGregtech;
        Properties.exportGregtechProperty.set(exportGregtech);
        saveConfiguration();
    }
    public static void toggleExportThaumcraft() {
        exportThaumcraft = !exportThaumcraft;
        Properties.exportThaumcraftProperty.set(exportThaumcraft);
        saveConfiguration();
    }

    private static void saveConfiguration() {
        configuration.save();
    }

    public static void synchronizeConfiguration() {
        configuration.load();
        // have to get properties after configuration load to have them associated with our Configuration instance
        Properties.load();

        exportIcons = Properties.exportIconsProperty.getBoolean();
        exportCatalysts = Properties.exportCatalystsProperty.getBoolean();
        exportCraftingTable = Properties.exportCraftingTableProperty.getBoolean();
        exportGregtech = Properties.exportGregtechProperty.getBoolean();
        exportThaumcraft = Properties.exportThaumcraftProperty.getBoolean();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
