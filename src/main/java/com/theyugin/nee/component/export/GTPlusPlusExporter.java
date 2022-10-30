package com.theyugin.nee.component.export;

import gregtech.api.util.GTPP_Recipe;

public class GTPlusPlusExporter implements IExporter {
    private int progress = 0;
    private int total = 0;
    private boolean running = false;

    @Override
    public void run() {
        for (GTPP_Recipe.GTPP_Recipe_Map_Internal sMappingsEx : GTPP_Recipe.GTPP_Recipe_Map_Internal.sMappingsEx) {}
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
