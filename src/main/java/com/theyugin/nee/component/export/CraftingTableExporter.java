package com.theyugin.nee.component.export;

import static com.theyugin.nee.LoadedMods.*;

import com.google.inject.Inject;
import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.component.service.CraftingRecipeService;
import com.theyugin.nee.component.service.ItemService;
import com.theyugin.nee.component.service.OreService;
import com.theyugin.nee.persistence.vanilla.CraftingTableRecipe;
import com.theyugin.nee.util.StackUtils;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import java.util.*;
import java.util.stream.Collectors;
import lombok.val;
import lombok.var;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftingTableExporter extends AbstractExporter {
    private final int total;

    @Override
    public int total() {
        return total;
    }

    @Override
    public String name() {
        return "crafting table recipes";
    }

    private final ItemService itemService;
    private final OreService oreService;
    private final CraftingRecipeService craftingRecipeService;

    @Inject
    public CraftingTableExporter(
            ItemService itemService, OreService oreService, CraftingRecipeService craftingRecipeService) {
        total = CraftingManager.getInstance().getRecipeList().size();
        this.itemService = itemService;
        this.oreService = oreService;
        this.craftingRecipeService = craftingRecipeService;
    }

    @SuppressWarnings("unchecked")
    private void processInput(CraftingTableRecipe recipe, int slot, Object oInput) {
        if (oInput instanceof ItemStack) {
            craftingRecipeService.addRecipeInput(recipe, itemService.processItemStack((ItemStack) oInput), slot);
        } else if (oInput instanceof String) {
            val ore = oreService.process((String) oInput);
            assert ore != null;
            craftingRecipeService.addRecipeInput(recipe, ore, slot);
        } else if (oInput instanceof ArrayList<?>) {
            if (((ArrayList<?>) oInput).stream().allMatch(StackUtils::isItemStack)) {
                val ore = oreService.process((List<ItemStack>) oInput);
                if (ore == null) {
                    for (val stack : (List<ItemStack>) oInput) {
                        craftingRecipeService.addRecipeInput(recipe, itemService.processItemStack(stack), slot);
                    }
                } else {
                    craftingRecipeService.addRecipeInput(recipe, ore, slot);
                }

            } else if (((ArrayList<?>) oInput).stream().allMatch(StackUtils::isIC2InputItemStack)) {
                for (val input : (ArrayList<ic2.api.recipe.IRecipeInput>) oInput) {
                    if (input instanceof ic2.api.recipe.RecipeInputOreDict) {
                        craftingRecipeService.addRecipeInput(
                                recipe, oreService.createOrGet(((RecipeInputOreDict) input).input), slot);
                    } else if (input instanceof ic2.api.recipe.RecipeInputItemStack) {
                        for (val itemStack : input.getInputs()) {
                            craftingRecipeService.addRecipeInput(recipe, itemService.processItemStack(itemStack), slot);
                        }
                    } else if (input instanceof ic2.api.recipe.RecipeInputFluidContainer) {
                        for (val itemStack : input.getInputs()) {
                            craftingRecipeService.addRecipeInput(recipe, itemService.processItemStack(itemStack), slot);
                        }
                    }
                }
            }

        } else if (oInput instanceof ItemStack[]) {
            val ore = oreService.process((ItemStack[]) oInput);
            if (ore == null) {
                for (val input : (ItemStack[]) oInput) {
                    craftingRecipeService.addRecipeInput(recipe, itemService.processItemStack(input), slot);
                }
            } else {
                craftingRecipeService.addRecipeInput(recipe, ore, slot);
            }
        } else if (IC2.isLoaded() && oInput instanceof ic2.api.recipe.IRecipeInput) {
            val ore = oreService.process(((IRecipeInput) oInput).getInputs());
            if (ore == null) {
                for (val input : ((IRecipeInput) oInput).getInputs()) {
                    craftingRecipeService.addRecipeInput(recipe, itemService.processItemStack(input), slot);
                }
            } else {
                craftingRecipeService.addRecipeInput(recipe, ore, slot);
            }
        } else if (AE2.isLoaded() && oInput instanceof appeng.api.recipes.IIngredient) {
            try {
                processInput(recipe, slot, ((appeng.api.recipes.IIngredient) oInput).getItemStackSet());
            } catch (Exception ex) {
                // useless exceptions from AE
            }

        } else if (oInput != null && oInput.getClass().isArray()) {
            val unknownArrayInput = (Object[]) oInput;
            NotEnoughExports.warn(
                    "Unknown array type: " + Arrays.stream(unknownArrayInput).collect(Collectors.toList()));
        } else {
            if (oInput != null)
                NotEnoughExports.warn(
                        "Unknown input type: " + oInput.getClass().getCanonicalName() + "\n\ttoString: " + oInput);
        }
    }

    private void processInputs(CraftingTableRecipe shapedRecipe, List<Object> o) {
        if (o != null) {
            val oIterator = o.listIterator();
            while (oIterator.hasNext()) {
                int slot = oIterator.nextIndex();
                val oInput = oIterator.next();
                processInput(shapedRecipe, slot, oInput);
            }
        }
    }

    private void processInputs(CraftingTableRecipe shapedRecipe, Object[] o) {
        if (o != null) processInputs(shapedRecipe, Arrays.asList(o));
    }

    private Object[] getInputs(IRecipe recipe) {
        if (AE2.isLoaded() && recipe instanceof appeng.recipes.game.ShapedRecipe) {
            return ((appeng.recipes.game.ShapedRecipe) recipe).getInput();
        } else if (IC2.isLoaded() && recipe instanceof ic2.core.AdvRecipe) {
            return ((ic2.core.AdvRecipe) recipe).input;
        } else if (recipe instanceof ShapedOreRecipe) {
            return ((ShapedOreRecipe) recipe).getInput();
        } else if (recipe instanceof ShapedRecipes) {
            return ((ShapedRecipes) recipe).recipeItems;
        } else if (IC2.isLoaded() && recipe instanceof ic2.core.AdvShapelessRecipe) {
            return ((ic2.core.AdvShapelessRecipe) recipe).input;
        } else if (AE2.isLoaded() && recipe instanceof appeng.recipes.game.ShapelessRecipe) {
            return ((appeng.recipes.game.ShapelessRecipe) recipe).getInput().toArray();
        } else if (recipe instanceof ShapelessOreRecipe) {
            return ((ShapelessOreRecipe) recipe).getInput().toArray();
        } else if (recipe instanceof ShapelessRecipes) {
            return ((ShapelessRecipes) recipe).recipeItems.toArray();
        }
        return null;
    }

    @Override
    public void run() {
        val unhandledRecipes = new HashSet<String>();
        val recipeList = CraftingManager.getInstance().getRecipeList();
        for (val oRecipe : recipeList) {
            progress++;
            logProgress();
            if (oRecipe instanceof IRecipe
                    && ((IRecipe) oRecipe).getRecipeOutput() != null
                    && ((IRecipe) oRecipe).getRecipeOutput().getItem() != null) {
                val inputs = getInputs((IRecipe) oRecipe);

                if (inputs == null) {
                    unhandledRecipes.add(oRecipe.getClass().getCanonicalName());
                } else {
                    val output = itemService.processItemStack(((IRecipe) oRecipe).getRecipeOutput());
                    var recipe = craftingRecipeService.createRecipe(output, true);
                    processInputs(recipe, inputs);
                }
            }
        }
        if (unhandledRecipes.size() > 0) {
            NotEnoughExports.warn("Unhandled recipe types: " + unhandledRecipes);
        }
    }
}
