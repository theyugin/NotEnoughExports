package com.theyugin.nee.export.exporter;

import com.theyugin.nee.data.gregtech.IGregtechRecipe;
import com.theyugin.nee.service.general.FluidService;
import com.theyugin.nee.service.general.ItemService;
import com.theyugin.nee.service.gregtech.GregtechRecipeService;
import com.theyugin.nee.service.vanilla.CatalystService;
import com.theyugin.nee.util.StackUtils;
import gregtech.api.util.GT_Recipe;
import java.util.*;
import java.util.stream.Collectors;
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

    void processRecipe(GT_Recipe gtRecipe, IGregtechRecipe recipe) {
        val itemInputsIterator = Arrays.stream(gtRecipe.mInputs)
                .filter((itemStack -> !StackUtils.isGtConfigCircuit(itemStack)))
                .collect(Collectors.toList())
                .listIterator();
        while (itemInputsIterator.hasNext()) {
            val slot = itemInputsIterator.nextIndex();
            val itemStack = itemInputsIterator.next();
            if (itemStack == null) {
                continue;
            }
            gregtechRecipeService.addInput(recipe, itemService.processItemStack(itemStack), slot, itemStack.stackSize);
        }

        val fluidInputsIterator = Arrays.asList(gtRecipe.mFluidInputs).listIterator();
        while (fluidInputsIterator.hasNext()) {
            val slot = fluidInputsIterator.nextIndex();
            val fluidStack = fluidInputsIterator.next();
            if (fluidStack == null) {
                continue;
            }
            gregtechRecipeService.addInput(recipe, fluidService.processFluidStack(fluidStack), slot, fluidStack.amount);
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

        val fluidOutputsIterator = Arrays.asList(gtRecipe.mFluidOutputs).listIterator();
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

    void processRecipe(GT_Recipe gtRecipe, GT_Recipe.GT_Recipe_Map gtRecipeMap) {
        if (gtRecipeMap instanceof GT_Recipe.GT_Recipe_Map_Fuel) {
            processRecipe(
                    gtRecipe,
                    gregtechRecipeService.createFuelRecipe(
                            catalystService.getOrCreate(gtRecipeMap.mUnlocalizedName),
                            gtRecipe.mSpecialValue,
                            gtRecipeMap.mNEISpecialValueMultiplier));
        } else {
            processRecipe(
                    gtRecipe,
                    gregtechRecipeService.createRecipe(
                            catalystService.getOrCreate(gtRecipeMap.mUnlocalizedName),
                            gtRecipe.mEUt,
                            StackUtils.findGtConfig(gtRecipe.mInputs),
                            gtRecipe.mDuration,
                            gtRecipeMap.mAmperage));
        }
    }

    @Override
    public void run() {
        val gtRecipeMaps = GT_Recipe.GT_Recipe_Map.sMappings;
        for (val gtRecipeMap : gtRecipeMaps) {
            for (val gtRecipe : gtRecipeMap.mRecipeList) {
                progress++;
                logProgress();
                if (!gtRecipe.mFakeRecipe && gtRecipe.mEnabled) {
                    processRecipe(gtRecipe, gtRecipeMap);
                }
            }
        }
    }
}
