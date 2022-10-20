package com.theyugin.nee.export;

import static com.theyugin.nee.LoadedMods.*;

import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.data.*;
import com.theyugin.nee.sql.*;
import com.theyugin.nee.util.ItemUtils;
import com.theyugin.nee.util.StackRenderer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftingTableExporter {
    private static void assignOreDict(
            Connection conn, List<ItemStack> itemStacks, ICraftingTableRecipeBuilder<?> recipeBuilder, int slot)
            throws SQLException {
        String oreName = ItemUtils.getOreDictValue(itemStacks);
        if (oreName != null) {
            OreBuilder oreBuilder = new OreBuilder().setName(oreName);
            for (ItemStack itemStack : itemStacks) {
                Item item = new ItemBuilder()
                        .setLocalizedName(ItemUtils.getLocalizedNameSafe(itemStack))
                        .setUnlocalizedName(ItemUtils.getUnlocalizedNameSafe(itemStack))
                        .setIcon(StackRenderer.renderIcon(itemStack))
                        .save(conn);
                oreBuilder.addItem(item);
            }
            recipeBuilder.addOreInput(oreBuilder.save(conn), slot);
        }
    }

    @SuppressWarnings("unchecked")
    private static void processInput(
            Connection conn, ICraftingTableRecipeBuilder<?> recipeBuilder, int slot, Object oInput)
            throws SQLException {
        if (oInput instanceof ItemStack) {
            Item item = new ItemBuilder()
                    .setLocalizedName(ItemUtils.getLocalizedNameSafe((ItemStack) oInput))
                    .setUnlocalizedName(ItemUtils.getUnlocalizedNameSafe((ItemStack) oInput))
                    .setIcon(StackRenderer.renderIcon((ItemStack) oInput))
                    .save(conn);
            recipeBuilder.addItemInput(item, slot);

        } else if (oInput instanceof String) {
            Ore ore = new OreBuilder().setName((String) oInput).save(conn);
            recipeBuilder.addOreInput(ore, slot);

        } else if (oInput instanceof ArrayList<?>) {
            if (((ArrayList<?>) oInput).stream().allMatch(ItemUtils::isItemStack)) {
                assignOreDict(conn, (List<ItemStack>) oInput, recipeBuilder, slot);

            } else if (((ArrayList<?>) oInput).stream().allMatch(ItemUtils::isIC2InputItemStack)) {
                List<ItemStack> itemStacks = ((ArrayList<?>) oInput)
                        .stream()
                                .map(v -> ((ic2.api.recipe.RecipeInputItemStack) v).input)
                                .collect(Collectors.toList());
                assignOreDict(conn, itemStacks, recipeBuilder, slot);
            }

        } else if (oInput instanceof ItemStack[]) {
            assignOreDict(conn, Arrays.asList((ItemStack[]) oInput), recipeBuilder, slot);

        } else if (IC2.isLoaded() && oInput instanceof ic2.api.recipe.IRecipeInput) {
            assignOreDict(conn, ((ic2.api.recipe.IRecipeInput) oInput).getInputs(), recipeBuilder, slot);

        } else if (AE2.isLoaded() && oInput instanceof appeng.api.recipes.IIngredient) {
            try {
                processInput(conn, recipeBuilder, slot, ((appeng.api.recipes.IIngredient) oInput).getItemStackSet());
            } catch (Exception ex) {
                NotEnoughExports.warn(ex.toString());
            }

        } else if (oInput != null && oInput.getClass().isArray()) {
            Object[] unknownArrayInput = (Object[]) oInput;
            NotEnoughExports.warn(
                    "Unknown array type: " + Arrays.stream(unknownArrayInput).collect(Collectors.toList()));
        } else {
            if (oInput != null)
                NotEnoughExports.warn(
                        "Unknown input type: " + oInput.getClass().getCanonicalName() + "\n\ttoString: " + oInput);
        }
    }

    private static void processInputs(Connection conn, List<Object> o, ICraftingTableRecipeBuilder<?> recipeBuilder)
            throws SQLException {
        if (o != null) {
            ListIterator<Object> oIterator = o.listIterator();
            while (oIterator.hasNext()) {
                int slot = oIterator.nextIndex();
                Object oInput = oIterator.next();
                processInput(conn, recipeBuilder, slot, oInput);
            }
        }
    }

    private static void processInputs(Connection conn, Object[] o, ICraftingTableRecipeBuilder<?> recipeBuilder)
            throws SQLException {
        if (o != null) processInputs(conn, Arrays.asList(o), recipeBuilder);
    }

    private static Object[] getShapedInputs(IRecipe recipe) {
        if (AE2.isLoaded() && recipe instanceof appeng.recipes.game.ShapedRecipe) {
            return ((appeng.recipes.game.ShapedRecipe) recipe).getInput();
        } else if (IC2.isLoaded() && recipe instanceof ic2.core.AdvRecipe) {
            return ((ic2.core.AdvRecipe) recipe).input;
        } else if (recipe instanceof ShapedOreRecipe) {
            return ((ShapedOreRecipe) recipe).getInput();
        } else if (recipe instanceof ShapedRecipes) {
            return ((ShapedRecipes) recipe).recipeItems;
        }
        return null;
    }

    private static Object[] getShapelessInputs(IRecipe recipe) {
        if (IC2.isLoaded() && recipe instanceof ic2.core.AdvShapelessRecipe) {
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

    public static void run(Connection conn) throws SQLException {
        Set<String> unhandledRecipes = new HashSet<>();
        for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
            if (recipe instanceof IRecipe
                    && ((IRecipe) recipe).getRecipeOutput() != null
                    && ((IRecipe) recipe).getRecipeOutput().getItem() != null) {
                Object[] shapedInputs = getShapedInputs((IRecipe) recipe);
                Object[] shapelessInputs = getShapelessInputs((IRecipe) recipe);

                if (shapelessInputs == null && shapedInputs == null) {
                    unhandledRecipes.add(recipe.getClass().getCanonicalName());
                } else {
                    ItemStack outputItemStack = ((IRecipe) recipe).getRecipeOutput();
                    Item output = new ItemBuilder()
                            .setLocalizedName(ItemUtils.getLocalizedNameSafe(outputItemStack))
                            .setUnlocalizedName(ItemUtils.getUnlocalizedNameSafe(outputItemStack))
                            .setIcon(StackRenderer.renderIcon(outputItemStack))
                            .save(conn);

                    ICraftingTableRecipeBuilder<?> recipeBuilder;
                    Object[] inputs;

                    if (shapedInputs != null) {
                        recipeBuilder = new ShapedRecipeBuilder().setOutput(output);
                        inputs = shapedInputs;
                    } else {
                        recipeBuilder = new ShapelessRecipeBuilder().setOutput(output);
                        inputs = shapelessInputs;
                    }

                    processInputs(conn, inputs, recipeBuilder);
                    recipeBuilder.save(conn);
                }
            }
        }
        if (unhandledRecipes.size() > 0) {
            NotEnoughExports.warn("Unhandled recipe types: " + unhandledRecipes);
        }
    }
}
