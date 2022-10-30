package com.theyugin.nee.component.export;

import forestry.api.recipes.*;
import java.sql.Connection;

public class ForestryExporter implements IExporter {
    private int progress = 0;
    private int total = 0;
    private boolean running = true;

    public ForestryExporter(Connection conn) {}

    private int calculateTotal() {
        return RecipeManagers.carpenterManager.recipes().size()
                + RecipeManagers.centrifugeManager.recipes().size()
                + RecipeManagers.fermenterManager.recipes().size()
                + RecipeManagers.moistenerManager.recipes().size()
                + RecipeManagers.squeezerManager.recipes().size()
                + RecipeManagers.stillManager.recipes().size()
                + RecipeManagers.fabricatorManager.recipes().size()
                + RecipeManagers.fabricatorSmeltingManager.recipes().size();
    }

    public void run() {
        total = calculateTotal();
        for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
            IDescriptiveRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();
            craftingGridRecipe.getIngredients();
            craftingGridRecipe.getRecipeOutput();

            recipe.getBox();
            recipe.getFluidResource();
            recipe.getPackagingTime();
        }
        for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
            recipe.getInput();
            recipe.getAllProducts();

            recipe.getProcessingTime();
        }
        for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
            recipe.getResource();
            recipe.getFluidResource();
            recipe.getFermentationValue();
            recipe.getModifier();
            recipe.getOutput();
        }
        for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.recipes()) {
            recipe.getResource();
            recipe.getProduct();
            recipe.getTimePerItem();
        }
        for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
            recipe.getFluidOutput();
            recipe.getProcessingTime();
            recipe.getRemnants();
            recipe.getResources();
            recipe.getRemnantsChance();
        }
        for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
            recipe.getOutput();
            recipe.getInput();
            recipe.getCyclesPerUnit();
        }
        for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
            recipe.getIngredients();
            recipe.getPlan();
            recipe.getRecipeOutput();
            recipe.getLiquid();
        }
        for (IFabricatorSmeltingRecipe recipe : RecipeManagers.fabricatorSmeltingManager.recipes()) {
            recipe.getResource();
            recipe.getProduct();
            recipe.getMeltingPoint();
        }
    }

    public int progress() {
        return progress;
    }

    public int total() {
        return total;
    }

    public String name() {
        return "Forestry stuff";
    }

    public boolean running() {
        return running;
    }
}
