package com.theyugin.nee.config;

import com.theyugin.nee.LoadedMods;
import java.util.function.Supplier;
import net.minecraftforge.common.config.Property;

public enum ExportConfigOption {
    ICONS("exportIcons", "export icons"),
    CATALYSTS("exportCatalysts", "export catalysts"),
    VANILLA("exportVanilla", "export vanilla recipes"),
    THAUMCRAFT("exportThaumcraft", "export thaumcraft recipes", LoadedMods.THAUMCRAFT::isLoaded),
    GREGTECH("exportGregtech", "export gregtech recipes", LoadedMods.GREGTECH::isLoaded),
    GTPLUSPLUS("exportGTPlusPlus", "export gtplusplus recipes", LoadedMods.GREGTECH::isLoaded),
    ;

    ExportConfigOption(String key, String description, Supplier<Boolean> shown) {
        this.key = key;
        this.description = description;
        this.shown = shown;
    }

    ExportConfigOption(String key, String description) {
        this(key, description, () -> true);
    }

    private final Supplier<Boolean> shown;

    public boolean shown() {
        return shown.get();
    }

    private final String key;
    public final String description;
    private boolean enabled = false;
    private Property property;

    public boolean get() {
        return shown() && enabled;
    }

    void refresh() {
        property = Config.configuration.get(Config.Categories.general, key, true, description);
        enabled = property.getBoolean();
    }

    public void toggle() {
        enabled = !enabled;
        property.set(enabled);
        Config.saveConfiguration();
    }
}
