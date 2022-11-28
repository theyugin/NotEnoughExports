package com.theyugin.nee.export.exporter;

import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.data.thaumcraft.CrucibleRecipe;
import com.theyugin.nee.service.general.ItemService;
import com.theyugin.nee.service.general.OreService;
import com.theyugin.nee.service.thaumcraft.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class ThaumcraftExporter extends AbstractExporter {
    private final int total;
    private final AspectService aspectService;
    private final ItemService itemService;
    private final OreService oreService;
    private final ThaumcraftRecipeService thaumcraftRecipeService;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "thaumcraft";
    }

    @SuppressWarnings("unchecked")
    private void processCrucibleRecipe(thaumcraft.api.crafting.CrucibleRecipe crucibleRecipe) {
        val output = itemService.processItemStack(crucibleRecipe.getRecipeOutput());
        CrucibleRecipe recipe;
        if (crucibleRecipe.catalyst instanceof ItemStack) {
            recipe = thaumcraftRecipeService.createCrucibleRecipe();
            thaumcraftRecipeService.addInput(recipe, itemService.processItemStack((ItemStack) crucibleRecipe.catalyst));
            thaumcraftRecipeService.addOutput(recipe, output);
        } else if (crucibleRecipe.catalyst instanceof ArrayList) {
            recipe = thaumcraftRecipeService.createCrucibleRecipe();
            thaumcraftRecipeService.addInput(recipe, oreService.process((List<ItemStack>) crucibleRecipe.catalyst));
        } else {
            NotEnoughExports.warn(String.format(
                    "Unknown crucible recipe input type : %s",
                    crucibleRecipe.catalyst.getClass().getCanonicalName()));
            return;
        }
        for (val aspect : crucibleRecipe.aspects.aspects.entrySet()) {
            thaumcraftRecipeService.addInput(recipe, aspectService.processAspect(aspect.getKey()), aspect.getValue());
        }
    }

    private void processInfusionRecipe(thaumcraft.api.crafting.InfusionRecipe infusionRecipe) {
        val outputObject = infusionRecipe.getRecipeOutput();
        val input = infusionRecipe.getRecipeInput();
        ItemStack output;
        if (outputObject instanceof Object[]) {
            val tagLabel = (String) ((Object[]) outputObject)[0];
            val tagValue = (NBTBase) ((Object[]) outputObject)[1];
            output = input.copy();
            output.setTagInfo(tagLabel, tagValue);
        } else if (outputObject instanceof ItemStack) {
            output = ((ItemStack) outputObject).copy();
        } else {
            if (outputObject == null) {
                NotEnoughExports.warn(String.format(
                        "Null infusion output for %s type",
                        infusionRecipe.getClass().getCanonicalName()));
            } else {
                NotEnoughExports.warn(String.format(
                        "Unknown infusion output type: %s",
                        outputObject.getClass().getCanonicalName()));
            }
            return;
        }
        val recipe = thaumcraftRecipeService.createInfusionRecipe(
                infusionRecipe.getResearch(), infusionRecipe.getInstability());
        thaumcraftRecipeService.addInput(recipe, itemService.processItemStack(infusionRecipe.getRecipeInput()));
        thaumcraftRecipeService.addOutput(recipe, itemService.processItemStack(output));
        val components = Arrays.asList(infusionRecipe.getComponents()).listIterator();
        while (components.hasNext()) {
            val slot = components.nextIndex() + 1;
            thaumcraftRecipeService.addInput(recipe, itemService.processItemStack(components.next()), slot);
        }
        for (val aspect : infusionRecipe.getAspects().aspects.entrySet()) {
            thaumcraftRecipeService.addInput(recipe, aspectService.processAspect(aspect.getKey()), aspect.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void processArcaneRecipe(List<?> inputs, ItemStack output, AspectList aspectList, boolean shaped) {
        val recipe = thaumcraftRecipeService.createArcaneRecipe(shaped);
        thaumcraftRecipeService.addOutput(recipe, itemService.processItemStack(output));
        val inputsIterator = inputs.listIterator();
        while (inputsIterator.hasNext()) {
            val slot = inputsIterator.nextIndex();
            val input = inputsIterator.next();
            if (input instanceof ItemStack) {
                thaumcraftRecipeService.addInput(recipe, itemService.processItemStack((ItemStack) input), slot);
            } else if (input instanceof List<?>) {
                thaumcraftRecipeService.addInput(recipe, oreService.process((List<ItemStack>) input), slot);
            }
        }
        for (val aspect : aspectList.aspects.entrySet()) {
            thaumcraftRecipeService.addInput(recipe, aspectService.processAspect(aspect.getKey()), aspect.getValue());
        }
    }

    public void processArcaneRecipe(thaumcraft.api.crafting.ShapedArcaneRecipe recipe) {
        processArcaneRecipe(Arrays.asList(recipe.getInput()), recipe.getRecipeOutput(), recipe.aspects, true);
    }

    public void processArcaneRecipe(thaumcraft.api.crafting.ShapelessArcaneRecipe recipe) {
        processArcaneRecipe(recipe.getInput(), recipe.getRecipeOutput(), recipe.aspects, false);
    }

    @Override
    public void run() {
        val uncheckedTypes = new HashSet<String>();

        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            progress++;
            logProgress();
            if (recipe instanceof thaumcraft.api.crafting.InfusionRecipe) {
                processInfusionRecipe((thaumcraft.api.crafting.InfusionRecipe) recipe);
            } else if (recipe instanceof thaumcraft.api.crafting.InfusionEnchantmentRecipe) {
                val infusionEnchantmentRecipe = (thaumcraft.api.crafting.InfusionEnchantmentRecipe) recipe;
            } else if (recipe instanceof thaumcraft.api.crafting.CrucibleRecipe) {
                processCrucibleRecipe((thaumcraft.api.crafting.CrucibleRecipe) recipe);
            } else if (recipe instanceof thaumcraft.api.crafting.ShapedArcaneRecipe) {
                processArcaneRecipe((ShapedArcaneRecipe) recipe);
            } else if (recipe instanceof thaumcraft.api.crafting.ShapelessArcaneRecipe) {
                processArcaneRecipe((ShapelessArcaneRecipe) recipe);
            } else {
                uncheckedTypes.add(recipe.getClass().getCanonicalName());
            }
        }
        // TODO: export also ThaumcraftApi.objectTags
        NotEnoughExports.info(uncheckedTypes.toString());
    }

    public ThaumcraftExporter(
            AspectService aspectService,
            ItemService itemService,
            OreService oreService,
            ThaumcraftRecipeService thaumcraftRecipeService) {
        this.aspectService = aspectService;
        this.itemService = itemService;
        this.oreService = oreService;
        this.thaumcraftRecipeService = thaumcraftRecipeService;
        total = ThaumcraftApi.getCraftingRecipes().size();
    }
}
