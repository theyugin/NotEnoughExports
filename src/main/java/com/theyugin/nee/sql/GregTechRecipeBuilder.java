package com.theyugin.nee.sql;

import com.theyugin.nee.data.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class GregTechRecipeBuilder implements IMachineRecipeBuilder<GregTechRecipe> {
    private String machineType;

    private ItemStackMap inputItemStackMap = new ItemStackMap();
    private OreStackMap inputOreStackMap = new OreStackMap();
    private FluidStackMap inputFluidStackMap = new FluidStackMap();

    private ItemStackMap outputItemStackMap = new ItemStackMap();
    private FluidStackMap outputFluidStackMap = new FluidStackMap();

    private int duration;
    private int voltage;
    private int amperage;
    private int config;

    public GregTechRecipeBuilder setMachineType(String type) {
        machineType = type;
        return this;
    }

    public GregTechRecipeBuilder addItemInput(Item item, int slot, int amount) {
        inputItemStackMap.accumulate(slot, item, amount);
        return this;
    }

    public GregTechRecipeBuilder addItemInput(Item item, int slot) {
        return this;
    }

    public GregTechRecipeBuilder addOreInput(Ore ore, int slot, int amount) {
        inputOreStackMap.accumulate(slot, ore, amount);
        return this;
    }

    public GregTechRecipeBuilder addOreInput(Ore item, int slot) {
        return this;
    }

    public GregTechRecipeBuilder addFluidInput(Fluid fluid, int slot, int amount) {
        inputFluidStackMap.accumulate(slot, fluid, amount);
        return this;
    }

    public GregTechRecipeBuilder addItemOutput(Item item, int slot, int amount) {
        outputItemStackMap.accumulate(slot, item, amount);
        return this;
    }

    @Override
    public ICraftingTableRecipeBuilder<GregTechRecipe> addItemOutput(Item item, int slot, int amount, int chance) {
        outputItemStackMap.accumulate(slot, item, amount, chance);
        return null;
    }

    public GregTechRecipeBuilder addFluidOutput(Fluid fluid, int slot, int amount) {
        outputFluidStackMap.accumulate(slot, fluid, amount);
        return this;
    }

    public GregTechRecipeBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public GregTechRecipeBuilder setAmperage(int amperage) {
        this.amperage = amperage;
        return this;
    }

    public GregTechRecipeBuilder setVoltage(int voltage) {
        this.voltage = voltage;
        return this;
    }

    public GregTechRecipeBuilder setConfig(int config) {
        this.config = config;
        return this;
    }

    @Override
    public GregTechRecipe save(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert or ignore into catalystType (name) values (?)");
        stmt.setString(1, machineType);
        stmt.executeUpdate();

        stmt = conn.prepareStatement(
                "insert or ignore into gregtechRecipe (voltage, duration, amperage, config, catalyst) values (?, ?, ?, ?, ?)");
        stmt.setInt(1, voltage);
        stmt.setInt(2, duration);
        stmt.setInt(3, amperage);
        stmt.setInt(4, config);
        stmt.setString(5, machineType);
        stmt.executeUpdate();

        ResultSet rs = conn.createStatement().executeQuery("select last_insert_rowid()");
        int recipeId = rs.getInt(1);

        if (!inputItemStackMap.isEmpty()) {
            stmt = conn.prepareStatement(
                    "insert or ignore into gregtechRecipeInputItem (recipe, item, slot, amount) values (?, ?, ?, ?)");
            for (Map.Entry<Integer, IStack<Item>> inputISEntry : inputItemStackMap.entrySet()) {
                int slot = inputISEntry.getKey();
                IStack<Item> stack = inputISEntry.getValue();
                for (Item item : stack.contents()) {
                    stmt.setInt(1, recipeId);
                    stmt.setString(2, item.unlocalizedName);
                    stmt.setInt(3, slot);
                    stmt.setInt(4, stack.amount());
                    stmt.addBatch();
                    stmt.clearParameters();
                }
            }
            stmt.executeBatch();
        }

        if (!inputFluidStackMap.isEmpty()) {
            stmt = conn.prepareStatement(
                    "insert or ignore into gregtechRecipeInputFluid (recipe, fluid, slot, amount) values (?, ?, ?, ?)");
            for (Map.Entry<Integer, IStack<Fluid>> inputFSEntry : inputFluidStackMap.entrySet()) {
                int slot = inputFSEntry.getKey();
                IStack<Fluid> stack = inputFSEntry.getValue();
                for (Fluid fluid : stack.contents()) {
                    stmt.setInt(1, recipeId);
                    stmt.setString(2, fluid.unlocalizedName);
                    stmt.setInt(3, slot);
                    stmt.setInt(4, stack.amount());
                    stmt.addBatch();
                    stmt.clearParameters();
                }
            }
            stmt.executeBatch();
        }

        if (!outputItemStackMap.isEmpty()) {
            stmt = conn.prepareStatement(
                    "insert or ignore into gregtechRecipeOutputItem (recipe, item, slot, amount, chance) values (?, ?, ?, ?, ?)");
            for (Map.Entry<Integer, IStack<Item>> outputISEntry : outputItemStackMap.entrySet()) {
                int slot = outputISEntry.getKey();
                IStack<Item> stack = outputISEntry.getValue();
                for (Item item : stack.contents()) {
                    stmt.setInt(1, recipeId);
                    stmt.setString(2, item.unlocalizedName);
                    stmt.setInt(3, slot);
                    stmt.setInt(4, stack.amount());
                    stmt.setInt(5, stack.chance());
                    stmt.addBatch();
                    stmt.clearParameters();
                }
            }
            stmt.executeBatch();
        }

        if (!outputFluidStackMap.isEmpty()) {
            stmt = conn.prepareStatement(
                    "insert or ignore into gregtechRecipeOutputFluid (recipe, fluid, slot, amount) values (?, ?, ?, ?)");
            for (Map.Entry<Integer, IStack<Fluid>> outputFSEntry : outputFluidStackMap.entrySet()) {
                int slot = outputFSEntry.getKey();
                IStack<Fluid> stack = outputFSEntry.getValue();
                for (Fluid fluid : stack.contents()) {
                    stmt.setInt(1, recipeId);
                    stmt.setString(2, fluid.unlocalizedName);
                    stmt.setInt(3, slot);
                    stmt.setInt(4, stack.amount());
                    stmt.addBatch();
                    stmt.clearParameters();
                }
            }
            stmt.executeBatch();
        }

        return new GregTechRecipe(
                machineType,
                inputItemStackMap,
                inputFluidStackMap,
                inputOreStackMap,
                outputItemStackMap,
                outputFluidStackMap,
                duration,
                amperage,
                voltage,
                config);
    }
}
