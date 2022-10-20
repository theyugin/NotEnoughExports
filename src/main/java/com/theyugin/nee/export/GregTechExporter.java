package com.theyugin.nee.export;

import com.theyugin.nee.sql.FluidBuilder;
import com.theyugin.nee.sql.GregTechRecipeBuilder;
import com.theyugin.nee.sql.ItemBuilder;
import com.theyugin.nee.util.ItemUtils;
import com.theyugin.nee.util.StackRenderer;
import gregtech.api.util.GT_Recipe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class GregTechExporter {
    public static void run(Connection conn) throws SQLException {
        for (GT_Recipe.GT_Recipe_Map gtRecipeMap : GT_Recipe.GT_Recipe_Map.sMappings) {
            for (GT_Recipe gtRecipe : gtRecipeMap.mRecipeList) {
                if (!gtRecipe.mFakeRecipe && gtRecipe.mEnabled) {
                    GregTechRecipeBuilder gregTechRecipeBuilder = new GregTechRecipeBuilder()
                            .setAmperage(gtRecipeMap.mAmperage)
                            .setDuration(gtRecipe.mDuration)
                            .setVoltage(gtRecipe.mEUt)
                            .setMachineType(gtRecipeMap.mUnlocalizedName);
                    int circuit = 0;

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
                            circuit = itemStack.getItemDamage();
                            continue;
                        }
                        gregTechRecipeBuilder.addItemInput(
                                new ItemBuilder()
                                        .setUnlocalizedName(ItemUtils.getUnlocalizedNameSafe(itemStack))
                                        .setLocalizedName(ItemUtils.getLocalizedNameSafe(itemStack))
                                        .setIcon(StackRenderer.renderIcon(itemStack))
                                        .save(conn),
                                slot,
                                itemStack.stackSize);
                    }

                    ListIterator<FluidStack> fluidInputsIterator =
                            Arrays.asList(gtRecipe.mFluidInputs).listIterator();
                    while (fluidInputsIterator.hasNext()) {
                        int slot = fluidInputsIterator.nextIndex();
                        FluidStack fluidStack = fluidInputsIterator.next();
                        if (fluidStack == null) {
                            continue;
                        }
                        gregTechRecipeBuilder.addFluidInput(
                                new FluidBuilder()
                                        .setUnlocalizedName(fluidStack.getUnlocalizedName())
                                        .setLocalizedName(fluidStack.getLocalizedName())
                                        .save(conn),
                                slot,
                                fluidStack.amount);
                    }

                    ListIterator<ItemStack> itemOutputsIterator =
                            Arrays.asList(gtRecipe.mOutputs).listIterator();
                    while (itemOutputsIterator.hasNext()) {
                        int slot = itemOutputsIterator.nextIndex();
                        ItemStack itemStack = itemOutputsIterator.next();
                        if (itemStack == null) {
                            continue;
                        }
                        gregTechRecipeBuilder.addItemOutput(
                                new ItemBuilder()
                                        .setUnlocalizedName(ItemUtils.getUnlocalizedNameSafe(itemStack))
                                        .setLocalizedName(ItemUtils.getLocalizedNameSafe(itemStack))
                                        .setIcon(StackRenderer.renderIcon(itemStack))
                                        .save(conn),
                                slot,
                                itemStack.stackSize,
                                gtRecipe.getOutputChance(slot));
                    }

                    ListIterator<FluidStack> fluidOutputsIterator =
                            Arrays.asList(gtRecipe.mFluidOutputs).listIterator();
                    while (fluidOutputsIterator.hasNext()) {
                        int slot = fluidOutputsIterator.nextIndex();
                        FluidStack fluidStack = fluidOutputsIterator.next();
                        if (fluidStack == null) {
                            continue;
                        }
                        gregTechRecipeBuilder.addFluidOutput(
                                new FluidBuilder()
                                        .setUnlocalizedName(fluidStack.getUnlocalizedName())
                                        .setLocalizedName(fluidStack.getLocalizedName())
                                        .save(conn),
                                slot,
                                fluidStack.amount);
                    }
                    gregTechRecipeBuilder.setConfig(circuit);
                    gregTechRecipeBuilder.save(conn);
                }
            }
        }
    }
}
