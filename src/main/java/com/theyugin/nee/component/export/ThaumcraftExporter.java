package com.theyugin.nee.component.export;

import com.google.inject.Inject;
import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.component.service.AspectService;
import java.util.HashSet;
import lombok.val;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.*;

public class ThaumcraftExporter extends AbstractExporter {
    private final int total;
    private final AspectService aspectService;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "thaumcraft";
    }

    @Override
    public void run() {
        val recipeTypes = new HashSet<String>();
        ThaumcraftApiHelper.getAllAspects(10);
        for (Aspect value : Aspect.aspects.values()) {
            aspectService.processAspect(value);
        }

        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            progress++;
            logProgress();
            if (recipe instanceof InfusionRecipe) {
                val infusionRecipe = (InfusionRecipe) recipe;
            } else if (recipe instanceof InfusionEnchantmentRecipe) {
                val infusionEnchantmentRecipe = (InfusionEnchantmentRecipe) recipe;
            } else if (recipe instanceof CrucibleRecipe) {
                val crucibleRecipe = (CrucibleRecipe) recipe;
            } else if (recipe instanceof ShapedArcaneRecipe) {
                val shapedArcaneRecipe = (ShapedArcaneRecipe) recipe;
            } else if (recipe instanceof ShapelessArcaneRecipe) {
                val shapelessArcaneRecipe = (ShapelessArcaneRecipe) recipe;
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
        total = ThaumcraftApi.getCraftingRecipes().size();
    }
}
