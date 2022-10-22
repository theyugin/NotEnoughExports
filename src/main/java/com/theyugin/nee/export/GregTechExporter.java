package com.theyugin.nee.export;

import com.theyugin.nee.data.Fluid;
import com.theyugin.nee.data.FluidStackMap;
import com.theyugin.nee.data.Item;
import com.theyugin.nee.data.ItemStackMap;
import com.theyugin.nee.sql.FluidDAO;
import com.theyugin.nee.sql.GregTechRecipeDAO;
import com.theyugin.nee.sql.ItemDAO;
import com.theyugin.nee.util.StackUtils;
import gregtech.api.util.GT_Recipe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class GregTechExporter implements IExporter {
    private boolean running = true;
    private  int progress = 0;
    private  int total = 0;
    public  int progress() {
        return progress;
    }
    public  int total() {
        return total;
    }

    @Override
    public String name() {
        return "GregTech recipes";
    }

    public boolean running() {
        return running;
    }

    private final ItemDAO itemDAO;
    private final GregTechRecipeDAO gregTechRecipeDAO;
    private final FluidDAO fluidDAO;

    public GregTechExporter(Connection conn) {
        itemDAO = new ItemDAO(conn);
        gregTechRecipeDAO = new GregTechRecipeDAO(conn);
        fluidDAO = new FluidDAO(conn);
    }

    private int countTotalReciopes(Collection<GT_Recipe.GT_Recipe_Map> gtRecipeMaps) {
        int acc = 0;
        for (GT_Recipe.GT_Recipe_Map gtRecipeMap : gtRecipeMaps) {
            acc += gtRecipeMap.mRecipeList.size();
        }
        return acc;
    }

    public void run() throws SQLException {
        Collection<GT_Recipe.GT_Recipe_Map> gtRecipeMaps = GT_Recipe.GT_Recipe_Map.sMappings;
        total = countTotalReciopes(gtRecipeMaps);
        for (GT_Recipe.GT_Recipe_Map gtRecipeMap : gtRecipeMaps) {
            for (GT_Recipe gtRecipe : gtRecipeMap.mRecipeList) {
                progress++;
                if (!gtRecipe.mFakeRecipe && gtRecipe.mEnabled) {

                    int config = 0;
                    ItemStackMap inputItemStackMap = new ItemStackMap();
                    FluidStackMap inputFluidStackMap = new FluidStackMap();
                    ItemStackMap outputItemStackMap = new ItemStackMap();
                    FluidStackMap outputFluidStackMap = new FluidStackMap();

                    ListIterator<ItemStack> itemInputsIterator =
                            Arrays.asList(gtRecipe.mInputs).listIterator();
                    while (itemInputsIterator.hasNext()) {
                        int slot = itemInputsIterator.nextIndex();
                        ItemStack itemStack = itemInputsIterator.next();
                        if (itemStack == null) {
                            continue;
                        }
                        if (itemStack.getUnlocalizedName().equals("gt.integrated_circuit")
                                && itemStack.getItemDamage() != 0
                                && itemStack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
                            config = itemStack.getItemDamage();
                            continue;
                        }
                        Item item = StackUtils.createFromStack(itemDAO, itemStack);
                        inputItemStackMap.accumulate(slot, item, itemStack.stackSize);
                    }

                    ListIterator<FluidStack> fluidInputsIterator =
                            Arrays.asList(gtRecipe.mFluidInputs).listIterator();
                    setFluidStacks(inputFluidStackMap, fluidInputsIterator);

                    ListIterator<ItemStack> itemOutputsIterator =
                            Arrays.asList(gtRecipe.mOutputs).listIterator();
                    while (itemOutputsIterator.hasNext()) {
                        int slot = itemOutputsIterator.nextIndex();
                        ItemStack itemStack = itemOutputsIterator.next();
                        if (itemStack == null) {
                            continue;
                        }
                        Item item = StackUtils.createFromStack(itemDAO, itemStack);
                        outputItemStackMap.accumulate(slot, item, itemStack.stackSize);
                    }

                    ListIterator<FluidStack> fluidOutputsIterator =
                            Arrays.asList(gtRecipe.mFluidOutputs).listIterator();
                    setFluidStacks(outputFluidStackMap, fluidOutputsIterator);
                    gregTechRecipeDAO.create(
                            gtRecipeMap.mUnlocalizedName,
                            gtRecipe.mEUt,
                            gtRecipe.mDuration,
                            gtRecipeMap.mAmperage,
                            config,
                            inputItemStackMap,
                            inputFluidStackMap,
                            outputItemStackMap,
                            outputFluidStackMap);
                }
            }
        }
        running = false;
    }

    private void setFluidStacks(FluidStackMap outputFluidStackMap, ListIterator<FluidStack> fluidOutputsIterator)
            throws SQLException {
        while (fluidOutputsIterator.hasNext()) {
            int slot = fluidOutputsIterator.nextIndex();
            FluidStack fluidStack = fluidOutputsIterator.next();
            if (fluidStack == null) {
                continue;
            }
            Fluid fluid = StackUtils.createFromStack(fluidDAO, fluidStack);
            outputFluidStackMap.accumulate(slot, fluid, fluidStack.amount);
        }
    }
}
