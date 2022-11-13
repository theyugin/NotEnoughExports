package com.theyugin.nee;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
    private static final String configurationFile = Tags.MODID + ".cfg";
    private static class Defaults {
        public static final Boolean exportIcons = true;
        public static final Boolean exportCatalysts = true;
        public static final Boolean exportCraftingTable = true;
        public static final Boolean exportGregtech = true;
    }

    private static class Categories {
        public static final String general = "general";
    }
    private static final Configuration configuration = new Configuration(new File("config", configurationFile));

    private static Boolean exportIcons = Defaults.exportIcons;
    private static Property exportIconsProperty;
    private static Boolean exportCatalysts = Defaults.exportCatalysts;
    private static Property exportCatalystsProperty;
    private static Boolean exportCraftingTable = Defaults.exportCraftingTable;
    private static Property exportCraftingTableProperty;
    private static Boolean exportGregtech = Defaults.exportGregtech;
    private static Property exportGregtechProperty;
    private static void loadProperties() {
        exportIconsProperty = configuration.get(Categories.general, "exportIcons", Defaults.exportIcons, "export item and fluid icons");
        exportCatalystsProperty = configuration.get(Categories.general, "exportCatalysts", Defaults.exportCatalysts, "export NEI catalysts");
        exportCraftingTableProperty = configuration.get(Categories.general, "exportCraftingTable", Defaults.exportCraftingTable, "export crafting table recipes");
        exportGregtechProperty = configuration.get(Categories.general, "exportGregtech", Defaults.exportGregtech, "export gregtech recipes");
    }

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
    public static void toggleExportIcons() {
        exportIcons = !exportIcons;
        exportIconsProperty.set(exportIcons);
        saveConfiguration();
    }
    public static void toggleExportCatalysts() {
        exportCatalysts = !exportCatalysts;
        exportCatalystsProperty.set(exportCatalysts);
        saveConfiguration();
    }
    public static void toggleExportCraftingTable() {
        exportCraftingTable = !exportCraftingTable;
        exportCraftingTableProperty.set(exportCraftingTable);
        saveConfiguration();
    }
    public static void toggleExportGregtech() {
        exportGregtech = !exportGregtech;
        exportGregtechProperty.set(exportGregtech);
        saveConfiguration();
    }
    private static void saveConfiguration() {
        configuration.save();
    }

    public static void synchronizeConfiguration() {
        configuration.load();
        // have to get properties after configuration load to have them associated with our Configuration instance
        loadProperties();

        exportIcons = exportIconsProperty.getBoolean();
        exportCatalysts = exportCatalystsProperty.getBoolean();
        exportCraftingTable = exportCraftingTableProperty.getBoolean();
        exportGregtech = exportGregtechProperty.getBoolean();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
