package com.theyugin.nee.component.export;

import com.google.inject.Inject;
import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.component.service.AspectService;
import lombok.val;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.*;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import java.util.HashSet;


public class ThaumcraftExporter implements IExporter {
    private static int progress = 0;
    private static int total = 0;
    private static boolean running = false;
    private final AspectService aspectService;
    @Override
    public int progress() {
        return progress;
    }

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "thaumcraft";
    }

    @Override
    public boolean running() {
        return running;
    }

    @Override
    public void run() {
        val recipeTypes = new HashSet<String>();
        ThaumcraftApiHelper.getAllAspects(10);
        for (Aspect value : Aspect.aspects.values()) {
            aspectService.processAspect(value);
        }

        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof InfusionRecipe) {
                val infusionRecipe = (InfusionRecipe)recipe;
            } else if (recipe instanceof InfusionEnchantmentRecipe) {
                val infusionEnchantmentRecipe = (InfusionEnchantmentRecipe)recipe;
            } else if (recipe instanceof CrucibleRecipe) {
                val crucibleRecipe = (CrucibleRecipe)recipe;
            } else if (recipe instanceof ShapedArcaneRecipe) {
                val shapedArcaneRecipe = (ShapedArcaneRecipe)recipe;
            } else if (recipe instanceof ShapelessArcaneRecipe) {
                val shapelessArcaneRecipe = (ShapelessArcaneRecipe)recipe;
            }
            recipeTypes.add(recipe.getClass().getCanonicalName());
        }
//        for (val entry : ThaumcraftApi.objectTags.entrySet()) {
//            val source =entry.getKey();
//            val aspects = entry.getValue();
//            for (val aspect : aspects.aspects.entrySet()) {
//                aspect.getKey().getImage();
//            }
//        }
        NotEnoughExports.info(recipeTypes.toString());
    }
    @Inject
    public ThaumcraftExporter(AspectService aspectService) {
        this.aspectService = aspectService;
    }
}
