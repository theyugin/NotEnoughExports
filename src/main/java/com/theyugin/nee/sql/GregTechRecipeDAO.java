package com.theyugin.nee.sql;

import com.theyugin.nee.data.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class GregTechRecipeDAO extends DAO {
    public GregTechRecipeDAO(Connection conn) {
        super(conn);
    }

    public GregTechRecipe create(
            String machineType,
            int voltage,
            int duration,
            int amperage,
            int config,
            ItemStackMap inputItemStackMap,
            FluidStackMap inputFluidStackMap,
            ItemStackMap outputItemStackMap,
            FluidStackMap outputFluidStackMap)
            throws SQLException {
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
                outputItemStackMap,
                outputFluidStackMap,
                duration,
                amperage,
                voltage,
                config);
    }
}
