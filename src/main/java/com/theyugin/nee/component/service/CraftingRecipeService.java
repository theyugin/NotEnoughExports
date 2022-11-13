package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.persistence.general.Ore;
import com.theyugin.nee.persistence.vanilla.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

@Singleton
public class CraftingRecipeService {
    private final PreparedStatement insertShapedStmt;
    private final PreparedStatement insertShapelessStmt;
    private final PreparedStatement insertShapedInputItemStmt;
    private final PreparedStatement insertShapedInputOreStmt;
    private final PreparedStatement insertShapelessInputItemStmt;
    private final PreparedStatement insertShapelessInputOreStmt;

    @Inject
    @SneakyThrows
    public CraftingRecipeService(@NonNull Connection conn) {
        insertShapedStmt =
                conn.prepareStatement("insert or ignore into shaped_recipe (output_item_registry_name) values (?)");
        insertShapelessStmt =
                conn.prepareStatement("insert or ignore into shapeless_recipe (output_item_registry_name) values (?)");
        insertShapedInputItemStmt = conn.prepareStatement(
                "insert or ignore into shaped_recipe_input_item (item_registry_name, shaped_recipe_id, slot) values (?, ?, ?)");
        insertShapedInputOreStmt = conn.prepareStatement(
                "insert or ignore into shaped_recipe_input_ore (ore_name, shaped_recipe_id, slot) values (?, ?, ?)");
        insertShapelessInputItemStmt = conn.prepareStatement(
                "insert or ignore into shapeless_recipe_input_item (item_registry_name, shapeless_recipe_id, slot) values (?, ?, ?)");
        insertShapelessInputOreStmt = conn.prepareStatement(
                "insert or ignore into shapeless_recipe_input_ore (ore_name, shapeless_recipe_id, slot) values (?, ?, ?)");
    }

    @SneakyThrows
    public ICraftingTableRecipe createRecipe(Item output, boolean shaped) {
        ICraftingTableRecipe recipe;
        PreparedStatement stmt;
        if (shaped) {
            stmt = insertShapedStmt;
            recipe = ShapedRecipe.builder().outputItem(output).build();
        } else {
            stmt = insertShapelessStmt;
            recipe = ShapelessRecipe.builder().outputItem(output).build();
        }
        stmt.setString(1, output.getRegistryName());
        stmt.executeUpdate();
        val rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            recipe.setId(rs.getInt(1));
        }
        return recipe;
    }

    @SneakyThrows
    public void addRecipeInput(ICraftingTableRecipe recipe, Item input, int slot) {
        PreparedStatement stmt;
        if (recipe instanceof ShapedRecipe) {
            stmt = insertShapedInputItemStmt;
        } else {
            stmt = insertShapelessInputItemStmt;
        }
        stmt.setString(1, input.getRegistryName());
        stmt.setInt(2, recipe.getId());
        stmt.setInt(3, slot);
        stmt.executeUpdate();
    }

    @SneakyThrows
    public void addRecipeInput(ICraftingTableRecipe recipe, Ore input, int slot) {
        PreparedStatement stmt;
        if (recipe instanceof ShapedRecipe) {
            stmt = insertShapedInputOreStmt;
        } else {
            stmt = insertShapelessInputOreStmt;
        }
        stmt.setString(1, input.getName());
        stmt.setInt(2, recipe.getId());
        stmt.setInt(3, slot);
        stmt.executeUpdate();
    }
}
