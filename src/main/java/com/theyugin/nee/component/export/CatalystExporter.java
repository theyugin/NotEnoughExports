package com.theyugin.nee.component.export;

import codechicken.nei.recipe.RecipeCatalysts;
import com.google.inject.Inject;
import com.theyugin.nee.component.service.CatalystService;
import com.theyugin.nee.component.service.ItemService;
import lombok.val;

public class CatalystExporter implements IExporter {
    private int progress = 0;
    private int total = 0;
    private boolean running = true;

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
        return "catalysts";
    }

    @Override
    public boolean running() {
        return running;
    }

    private final CatalystService catalystService;
    private final ItemService itemService;

    @Inject
    public CatalystExporter(CatalystService catalystService, ItemService itemService) {
        this.catalystService = catalystService;
        this.itemService = itemService;
    }

    @Override
    public void run() {
        val catalystMap = RecipeCatalysts.getPositionedRecipeCatalystMap();
        total = catalystMap.size();
        for (val stringListEntry : catalystMap.entrySet()) {
            val catalyst = catalystService.getOrCreate(stringListEntry.getKey());

            for (val positionedStack : stringListEntry.getValue()) {
                for (val itemStack : positionedStack.items) {
                    catalystService.addItem(catalyst, itemService.processItemStack(itemStack));
                }
            }
            progress++;
        }
        running = false;
    }
}
