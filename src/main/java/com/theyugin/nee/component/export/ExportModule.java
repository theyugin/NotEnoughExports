package com.theyugin.nee.component.export;

import com.google.inject.AbstractModule;

public class ExportModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CatalystExporter.class);
        bind(CraftingTableExporter.class);
        bind(GregTechExporter.class);
    }
}
