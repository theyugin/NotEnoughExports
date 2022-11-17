package com.theyugin.nee.component.service;

import static com.theyugin.nee.LoadedMods.*;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CatalystService.class);
        bind(CraftingRecipeService.class);
        bind(FluidService.class);
        if (GREGTECH.isLoaded()) bind(GregtechRecipeService.class);
        bind(ItemService.class);
        bind(OreService.class);
        if (THAUMCRAFT.isLoaded()) {
            bind(AspectService.class);
            bind(CrucibleRecipeService.class);
        }
    }
}
