package com.theyugin.nee.component.export;

import com.google.inject.Inject;
import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.component.service.*;
import com.theyugin.nee.persistence.thaumcraft.CrucibleRecipe;
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
    private final CrucibleRecipeService crucibleRecipeService;
    private final OreService oreService;
    private final InfusionRecipeService infusionRecipeService;
    private final ArcaneRecipeService arcaneRecipeService;

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
            recipe = crucibleRecipeService.createRecipe(
                    itemService.processItemStack((ItemStack) crucibleRecipe.catalyst), output);
        } else if (crucibleRecipe.catalyst instanceof ArrayList) {
            recipe = crucibleRecipeService.createRecipe(
                    oreService.process((List<ItemStack>) crucibleRecipe.catalyst), output);
        } else {
            NotEnoughExports.warn(String.format(
                    "Unknown crucible recipe input type : %s",
                    crucibleRecipe.catalyst.getClass().getCanonicalName()));
            return;
        }
        for (val aspect : crucibleRecipe.aspects.aspects.entrySet()) {
            crucibleRecipeService.addAspect(recipe, aspectService.processAspect(aspect.getKey()), aspect.getValue());
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
        val recipe = infusionRecipeService.createRecipe(
                itemService.processItemStack(input),
                itemService.processItemStack(output),
                infusionRecipe.getResearch(),
                infusionRecipe.getInstability());
        for (val component : infusionRecipe.getComponents()) {
            infusionRecipeService.addComponent(recipe, itemService.processItemStack(component));
        }
        for (val aspect : infusionRecipe.getAspects().aspects.entrySet()) {
            infusionRecipeService.addAspect(recipe, aspectService.processAspect(aspect.getKey()), aspect.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void processArcaneRecipe(List<?> inputs, ItemStack output, AspectList aspectList, boolean shaped) {
        val recipe = arcaneRecipeService.createRecipe(itemService.processItemStack(output), shaped);
        val inputsIterator = inputs.listIterator();
        while (inputsIterator.hasNext()) {
            val slot = inputsIterator.nextIndex();
            val input = inputsIterator.next();
            if (input instanceof ItemStack) {
                arcaneRecipeService.addInput(recipe, itemService.processItemStack((ItemStack) input), slot);
            } else if (input instanceof List<?>) {
                arcaneRecipeService.addInput(recipe, oreService.process((List<ItemStack>) input), slot);
            }
        }
        for (val aspect : aspectList.aspects.entrySet()) {
            arcaneRecipeService.addAspect(recipe, aspectService.processAspect(aspect.getKey()), aspect.getValue());
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

    @Inject
    public ThaumcraftExporter(
            AspectService aspectService,
            ItemService itemService,
            OreService oreService,
            CrucibleRecipeService crucibleRecipeService,
            InfusionRecipeService infusionRecipeService,
            ArcaneRecipeService arcaneRecipeService) {
        this.aspectService = aspectService;
        this.itemService = itemService;
        this.oreService = oreService;
        this.crucibleRecipeService = crucibleRecipeService;
        this.infusionRecipeService = infusionRecipeService;
        this.arcaneRecipeService = arcaneRecipeService;
        total = ThaumcraftApi.getCraftingRecipes().size();
    }
}
