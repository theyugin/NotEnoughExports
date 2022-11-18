package com.theyugin.nee.component.export;

import gregtech.api.util.GTPP_Recipe;
import gregtech.api.util.GT_Recipe;
import lombok.val;

public class GTPlusPlusExporter extends AbstractExporter {
    private final int total;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "GTPlusPlus recipes";
    }

    public GTPlusPlusExporter() {
        total = GTPP_Recipe.GTPP_Recipe_Map_Internal.sMappingsEx.size();
    }

    @Override
    public void run() {
        for (val sMappingsEx : GTPP_Recipe.GTPP_Recipe_Map_Internal.sMappingsEx) {
            val mappingName = sMappingsEx.mUnlocalizedName;
            for (GT_Recipe gt_recipe : sMappingsEx.mRecipeList) {}
        }
    }
}
