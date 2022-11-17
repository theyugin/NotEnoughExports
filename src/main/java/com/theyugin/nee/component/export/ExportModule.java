package com.theyugin.nee.component.export;

import static com.theyugin.nee.LoadedMods.*;

import com.google.inject.AbstractModule;

public class ExportModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CatalystExporter.class);
        bind(CraftingTableExporter.class);
        if (GREGTECH.isLoaded()) bind(GregTechExporter.class);
        if (THAUMCRAFT.isLoaded()) bind(ThaumcraftExporter.class);
    }
}
