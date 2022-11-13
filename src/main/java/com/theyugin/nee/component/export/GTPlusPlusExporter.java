package com.theyugin.nee.component.export;

import gregtech.api.util.GTPP_Recipe;
import gregtech.api.util.GT_Recipe;
import lombok.val;

public class GTPlusPlusExporter implements IExporter {
    private int progress = 0;
    private int total = 0;
    private boolean running = false;

    @Override
    public void run() {
        for (val sMappingsEx : GTPP_Recipe.GTPP_Recipe_Map_Internal.sMappingsEx) {
            val mappingName = sMappingsEx.mUnlocalizedName;
            for (GT_Recipe gt_recipe : sMappingsEx.mRecipeList) {
            }
        }
    }

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
        return "GTPlusPlus recipes";
    }

    @Override
    public boolean running() {
        return running;
    }
}
