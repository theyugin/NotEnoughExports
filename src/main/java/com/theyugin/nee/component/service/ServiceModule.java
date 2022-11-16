package com.theyugin.nee.component.service;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CatalystService.class);
        bind(CraftingRecipeService.class);
        bind(FluidService.class);
        bind(GregtechRecipeService.class);
        bind(ItemService.class);
        bind(OreService.class);
        bind(AspectService.class);
    }
}
