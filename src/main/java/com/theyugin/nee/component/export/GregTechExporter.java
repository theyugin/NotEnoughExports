package com.theyugin.nee.component.export;

import com.google.inject.Inject;
import com.theyugin.nee.component.service.CatalystService;
import com.theyugin.nee.component.service.FluidService;
import com.theyugin.nee.component.service.GregtechRecipeService;
import com.theyugin.nee.component.service.ItemService;
import com.theyugin.nee.persistence.gregtech.GregtechRecipe;
import com.theyugin.nee.util.StackUtils;
import gregtech.api.util.GT_Recipe;
import java.util.*;
import lombok.val;

public class GregTechExporter extends AbstractExporter {
    private final int total;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "GregTech recipes";
    }

    private final GregtechRecipeService gregtechRecipeService;
    private final ItemService itemService;
    private final FluidService fluidService;
    private final CatalystService catalystService;

    @Inject
    public GregTechExporter(
            GregtechRecipeService gregtechRecipeService,
            ItemService itemService,
            FluidService fluidService,
            CatalystService catalystService) {
        total = countTotalRecipes();
        this.gregtechRecipeService = gregtechRecipeService;
        this.itemService = itemService;
        this.fluidService = fluidService;
        this.catalystService = catalystService;
    }

    private int countTotalRecipes() {
        int acc = 0;
        for (val gtRecipeMap : GT_Recipe.GT_Recipe_Map.sMappings) {
            acc += gtRecipeMap.mRecipeList.size();
        }
        return acc;
    }

    @Override
    public void run() {
        val gtRecipeMaps = GT_Recipe.GT_Recipe_Map.sMappings;
        for (val gtRecipeMap : gtRecipeMaps) {
            for (val gtRecipe : gtRecipeMap.mRecipeList) {
                progress++;
                logProgress();
                if (!gtRecipe.mFakeRecipe && gtRecipe.mEnabled) {
                    GregtechRecipe recipe;
                    if (gtRecipeMap instanceof GT_Recipe.GT_Recipe_Map_Fuel) {
                        recipe = gregtechRecipeService.createFuelRecipe(
                                catalystService.getOrCreate(gtRecipeMap.mUnlocalizedName),
                                gtRecipe.mSpecialValue,
                                gtRecipeMap.mNEISpecialValueMultiplier);
                    } else {
                        recipe = gregtechRecipeService.createRecipe(
                                catalystService.getOrCreate(gtRecipeMap.mUnlocalizedName),
                                gtRecipe.mEUt,
                                StackUtils.findGtConfig(gtRecipe.mInputs),
                                gtRecipe.mDuration,
                                gtRecipeMap.mAmperage);
                    }
                    val itemInputsIterator = Arrays.asList(gtRecipe.mInputs).listIterator();
                    while (itemInputsIterator.hasNext()) {
                        val slot = itemInputsIterator.nextIndex();
                        val itemStack = itemInputsIterator.next();
                        if (itemStack == null) {
                            continue;
                        }
                        if (StackUtils.isGtConfigCircuit(itemStack)) {
                            continue;
                        }
                        gregtechRecipeService.addInput(
                                recipe, itemService.processItemStack(itemStack), slot, itemStack.stackSize);
                    }

                    val fluidInputsIterator =
                            Arrays.asList(gtRecipe.mFluidInputs).listIterator();
                    while (fluidInputsIterator.hasNext()) {
                        val slot = fluidInputsIterator.nextIndex();
                        val fluidStack = fluidInputsIterator.next();
                        if (fluidStack == null) {
                            continue;
                        }
                        gregtechRecipeService.addInput(
                                recipe, fluidService.processFluidStack(fluidStack), slot, fluidStack.amount);
                    }

                    val itemOutputsIterator = Arrays.asList(gtRecipe.mOutputs).listIterator();
                    while (itemOutputsIterator.hasNext()) {
                        val slot = itemOutputsIterator.nextIndex();
                        val itemStack = itemOutputsIterator.next();
                        if (itemStack == null) {
                            continue;
                        }
                        val chance = gtRecipe.mChances[slot];
                        gregtechRecipeService.addOutput(
                                recipe, itemService.processItemStack(itemStack), slot, itemStack.stackSize, chance);
                    }

                    val fluidOutputsIterator =
                            Arrays.asList(gtRecipe.mFluidOutputs).listIterator();
                    while (fluidOutputsIterator.hasNext()) {
                        val slot = fluidOutputsIterator.nextIndex();
                        val fluidStack = fluidOutputsIterator.next();
                        if (fluidStack == null) {
                            continue;
                        }
                        gregtechRecipeService.addOutput(
                                recipe, fluidService.processFluidStack(fluidStack), slot, fluidStack.amount);
                    }
                }
            }
        }
    }
}
