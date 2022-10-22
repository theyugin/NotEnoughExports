package com.theyugin.nee.export;

import static com.theyugin.nee.LoadedMods.*;

import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.data.*;
import com.theyugin.nee.sql.*;
import com.theyugin.nee.util.StackUtils;
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
    private static boolean exporting = false;
    private static String status = "";

    public static boolean isExporting() {
        return exporting;
    }

    public static String getStatus() {
        return status;
    }

    private final ItemDAO itemDAO;
    private final OreDAO oreDAO;
    private final ShapedRecipeDAO shapedRecipeDAO;
    private final ShapelessRecipeDAO shapelessRecipeDAO;

    public CraftingTableExporter(Connection conn) {
        itemDAO = new ItemDAO(conn);
        oreDAO = new OreDAO(conn);
        shapedRecipeDAO = new ShapedRecipeDAO(conn);
        shapelessRecipeDAO = new ShapelessRecipeDAO(conn);
    }

    private void assignOreDict(List<ItemStack> itemStacks, OreStackMap oreStackMap, int slot) throws SQLException {
        String oreName = StackUtils.getOreDictValue(new HashSet<>(itemStacks));
        if (oreName != null) {
            Set<Item> items = new HashSet<>();
            for (ItemStack itemStack : itemStacks) {
                Item item = StackUtils.createFromStack(itemDAO, itemStack);
                items.add(item);
            }
            Ore ore = oreDAO.create(oreName, items);
            oreStackMap.accumulate(slot, ore);
        }
    }

    @SuppressWarnings("unchecked")
    private void processInput(ItemStackMap itemStackMap, OreStackMap oreStackMap, int slot, Object oInput)
            throws SQLException {
        if (oInput instanceof ItemStack) {
            Item item = StackUtils.createFromStack(itemDAO, (ItemStack) oInput);
            itemStackMap.accumulate(slot, item);
        } else if (oInput instanceof String) {
            Set<Item> oreItems = new HashSet<>();
            for (ItemStack itemStack : StackUtils.getOreItemStacks((String) oInput)) {
                Item item = StackUtils.createFromStack(itemDAO, itemStack);
                oreItems.add(item);
            }
            Ore ore = oreDAO.create((String) oInput, oreItems);
            oreStackMap.accumulate(slot, ore);
        } else if (oInput instanceof ArrayList<?>) {
            if (((ArrayList<?>) oInput).stream().allMatch(StackUtils::isItemStack)) {
                assignOreDict((List<ItemStack>) oInput, oreStackMap, slot);

            } else if (((ArrayList<?>) oInput).stream().allMatch(StackUtils::isIC2InputItemStack)) {
                List<ItemStack> itemStacks = ((ArrayList<?>) oInput)
                        .stream()
                                .map(v -> ((ic2.api.recipe.RecipeInputItemStack) v).input)
                                .collect(Collectors.toList());
                assignOreDict(itemStacks, oreStackMap, slot);
            }

        } else if (oInput instanceof ItemStack[]) {
            assignOreDict(Arrays.asList((ItemStack[]) oInput), oreStackMap, slot);

        } else if (IC2.isLoaded() && oInput instanceof ic2.api.recipe.IRecipeInput) {
            assignOreDict(((ic2.api.recipe.IRecipeInput) oInput).getInputs(), oreStackMap, slot);

        } else if (AE2.isLoaded() && oInput instanceof appeng.api.recipes.IIngredient) {
            try {
                processInput(
                        itemStackMap, oreStackMap, slot, ((appeng.api.recipes.IIngredient) oInput).getItemStackSet());
            } catch (Exception ex) {
                // useless exceptions from appeng
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

    private void processInputs(List<Object> o, ItemStackMap itemStackMap, OreStackMap oreStackMap) throws SQLException {
        if (o != null) {
            ListIterator<Object> oIterator = o.listIterator();
            while (oIterator.hasNext()) {
                int slot = oIterator.nextIndex();
                Object oInput = oIterator.next();
                processInput(itemStackMap, oreStackMap, slot, oInput);
            }
        }
    }

    private void processInputs(Object[] o, ItemStackMap itemStackMap, OreStackMap oreStackMap) throws SQLException {
        if (o != null) processInputs(Arrays.asList(o), itemStackMap, oreStackMap);
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

    public void run() throws SQLException {
        exporting = true;
        Set<String> unhandledRecipes = new HashSet<>();
        List<Object> recipeList = CraftingManager.getInstance().getRecipeList();
        int recipeCount = recipeList.size();
        int counter = 0;
        for (Object recipe : recipeList) {
            counter++;
            status = String.format("Exporting recipe %d/%d", counter, recipeCount);
            if (recipe instanceof IRecipe
                    && ((IRecipe) recipe).getRecipeOutput() != null
                    && ((IRecipe) recipe).getRecipeOutput().getItem() != null) {
                Object[] shapedInputs = getShapedInputs((IRecipe) recipe);
                Object[] shapelessInputs = getShapelessInputs((IRecipe) recipe);

                if (shapelessInputs == null && shapedInputs == null) {
                    unhandledRecipes.add(recipe.getClass().getCanonicalName());
                } else {
                    ItemStackMap itemStackMap = new ItemStackMap();
                    OreStackMap oreStackMap = new OreStackMap();
                    ItemStack outputItemStack = ((IRecipe) recipe).getRecipeOutput();
                    Item output = StackUtils.createFromStack(itemDAO, outputItemStack);

                    if (shapedInputs != null) {
                        processInputs(shapedInputs, itemStackMap, oreStackMap);
                        shapedRecipeDAO.create(itemStackMap, oreStackMap, output);
                    } else {
                        processInputs(shapelessInputs, itemStackMap, oreStackMap);
                        shapelessRecipeDAO.create(itemStackMap, oreStackMap, output);
                    }
                }
            }
        }
        if (unhandledRecipes.size() > 0) {
            NotEnoughExports.warn("Unhandled recipe types: " + unhandledRecipes);
        }
        exporting = false;
    }
}
