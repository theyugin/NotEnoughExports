package com.theyugin.nee.export.exporter;

import com.theyugin.nee.service.gregtech.GregtechRecipeService;
import gregtech.api.util.GTPP_Recipe;
import gregtech.api.util.GT_Recipe;
import lombok.val;

public class GTPlusPlusExporter extends AbstractExporter {
    private final int total;
    private final GregTechExporter gregTechExporter;
    private final GregtechRecipeService gregtechRecipeService;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "GTPlusPlus recipes";
    }

    public GTPlusPlusExporter(GregTechExporter gregTechExporter, GregtechRecipeService gregtechRecipeService) {
        this.gregTechExporter = gregTechExporter;
        this.gregtechRecipeService = gregtechRecipeService;
        total = GTPP_Recipe.GTPP_Recipe_Map_Internal.sMappingsEx.size();
    }

    @Override
    public void run() {
        for (val gtppRecipeMap : GTPP_Recipe.GTPP_Recipe_Map_Internal.sMappingsEx) {
            for (GT_Recipe gtRecipe : gtppRecipeMap.mRecipeList) {
                gregTechExporter.processRecipe(gtRecipe, gtppRecipeMap);
            }
        }
    }
}
