package com.theyugin.nee.component.export;

import codechicken.nei.recipe.RecipeCatalysts;
import com.google.inject.Inject;
import com.theyugin.nee.component.service.CatalystService;
import com.theyugin.nee.component.service.ItemService;
import lombok.val;

public class CatalystExporter extends AbstractExporter {
    private final int total;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "catalysts";
    }

    private final CatalystService catalystService;
    private final ItemService itemService;

    @Inject
    public CatalystExporter(CatalystService catalystService, ItemService itemService) {
        this.catalystService = catalystService;
        this.itemService = itemService;
        total = RecipeCatalysts.getPositionedRecipeCatalystMap().size();
    }

    @Override
    public void run() {
        val catalystMap = RecipeCatalysts.getPositionedRecipeCatalystMap();
        for (val stringListEntry : catalystMap.entrySet()) {
            progress++;
            logProgress();
            val catalyst = catalystService.getOrCreate(stringListEntry.getKey());

            for (val positionedStack : stringListEntry.getValue()) {
                for (val itemStack : positionedStack.items) {
                    catalystService.addItem(catalyst, itemService.processItemStack(itemStack));
                }
            }
        }
    }
}
