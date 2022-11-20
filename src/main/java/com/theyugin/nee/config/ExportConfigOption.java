package com.theyugin.nee.config;

import com.theyugin.nee.LoadedMods;
import net.minecraftforge.common.config.Property;

public enum ExportConfigOption {
    ICONS("exportIcons", "export icons"),
    CATALYSTS("exportCatalysts", "export catalysts"),
    CRAFTING_TABLE("exportCraftingTable", "export crafting table recipes"),
    THAUMCRAFT("exportThaumcraft", "export thaumcraft recipes"),
    GREGTECH("exportGregtech", "export gregtech recipes"),
    GTPLUSPLUS("exportGTPlusPlus", "export gtplusplus recipes"),
    ;

    ExportConfigOption(String key, String description) {
        this.key = key;
        this.description = description;
    }

    private final String key;
    public final String description;
    private Boolean enabled = false;
    private Property property;

    public Boolean get() {
        return shown() && enabled;
    }

    void refresh() {
        property = Config.configuration.get(Config.Categories.general, key, true, description);
        enabled = property.getBoolean();
    }

    public Boolean shown() {
        if (this == ICONS || this == CATALYSTS || this == CRAFTING_TABLE) return true;
        if (this == THAUMCRAFT && LoadedMods.THAUMCRAFT.isLoaded()) return true;
        if (this == GREGTECH && LoadedMods.GREGTECH.isLoaded()) return true;
        if (this == GTPLUSPLUS && LoadedMods.GTPLUSPLUS.isLoaded()) return true;
        return false;
    }

    public void toggle() {
        enabled = !enabled;
        property.set(enabled);
        Config.saveConfiguration();
    }
}
