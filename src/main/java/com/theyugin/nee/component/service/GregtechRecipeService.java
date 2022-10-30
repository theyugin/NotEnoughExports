package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Catalyst;
import com.theyugin.nee.persistence.general.Fluid;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.persistence.gregtech.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;
import lombok.val;

@Singleton
public class GregtechRecipeService {
    private final PreparedStatement gregtechRecipeStmt;
    private final PreparedStatement gregtechRecipeInputItemStmt;
    private final PreparedStatement gregtechRecipeInputFluidStmt;
    private final PreparedStatement gregtechRecipeOutputItemStmt;
    private final PreparedStatement gregtechRecipeOutputFluidStmt;

    @Inject
    public GregtechRecipeService(@NonNull Connection conn) throws SQLException {
        gregtechRecipeStmt = conn.prepareStatement(
                "insert or ignore into gregtech_recipe (amperage, config, duration, voltage, catalyst_name) values (?, ?, ?, ?, ?)");
        gregtechRecipeInputItemStmt = conn.prepareStatement(
                "insert or ignore into gregtech_recipe_input_item (gregtech_recipe_id, item_registry_name, slot, amount) values (?, ?, ?, ?)");
        gregtechRecipeInputFluidStmt = conn.prepareStatement(
                "insert or ignore into gregtech_recipe_input_fluid (gregtech_recipe_id, fluid_registry_name, slot, amount) values (?, ?, ?, ?)");
        gregtechRecipeOutputItemStmt = conn.prepareStatement(
                "insert or ignore into gregtech_recipe_output_item (gregtech_recipe_id, item_registry_name, slot, amount, chance) values (?, ?, ?, ?, ?)");
        gregtechRecipeOutputFluidStmt = conn.prepareStatement(
                "insert or ignore into gregtech_recipe_output_fluid (gregtech_recipe_id, fluid_registry_name, slot, amount) values (?, ?, ?, ?)");
    }

    public GregtechRecipe createRecipe(Catalyst catalyst, int voltage, int config, int duration, int amperage)
            throws SQLException {
        val recipe = GregtechRecipe.builder()
                .config(config)
                .duration(duration)
                .amperage(amperage)
                .voltage(voltage)
                .catalystName(catalyst)
                .build();
        gregtechRecipeStmt.setInt(1, amperage);
        gregtechRecipeStmt.setInt(2, config);
        gregtechRecipeStmt.setInt(3, duration);
        gregtechRecipeStmt.setInt(4, voltage);
        gregtechRecipeStmt.setString(5, catalyst.getName());
        gregtechRecipeStmt.executeUpdate();
        val rs = gregtechRecipeStmt.getGeneratedKeys();
        while (rs.next()) {
            recipe.setId(rs.getInt(1));
        }
        return recipe;
    }

    private void add(PreparedStatement stmt, Integer recipeId, String item, int slot, int amount, Integer chance)
            throws SQLException {
        stmt.setInt(1, recipeId);
        stmt.setString(2, item);
        stmt.setInt(3, slot);
        stmt.setInt(4, amount);
        stmt.executeUpdate();
        if (chance != null) {
            stmt.setInt(5, chance);
        }
    }

    public void addInput(GregtechRecipe recipe, Item input, int slot, int amount) throws SQLException {
        add(gregtechRecipeInputItemStmt, recipe.getId(), input.getRegistryName(), slot, amount, null);
    }

    public void addInput(GregtechRecipe recipe, Fluid input, int slot, int amount) throws SQLException {
        add(gregtechRecipeInputFluidStmt, recipe.getId(), input.getRegistryName(), slot, amount, null);
    }

    public void addOutput(GregtechRecipe recipe, Item input, int slot, int amount, int chance) throws SQLException {
        add(gregtechRecipeOutputItemStmt, recipe.getId(), input.getRegistryName(), slot, amount, chance);
    }

    public void addOutput(GregtechRecipe recipe, Fluid input, int slot, int amount) throws SQLException {
        add(gregtechRecipeOutputFluidStmt, recipe.getId(), input.getRegistryName(), slot, amount, null);
    }
}
