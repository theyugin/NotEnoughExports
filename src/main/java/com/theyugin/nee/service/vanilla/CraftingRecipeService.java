package com.theyugin.nee.service.vanilla;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.data.general.Item;
import com.theyugin.nee.data.general.Ore;
import com.theyugin.nee.data.vanilla.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

@Singleton
public class CraftingRecipeService {
    private final PreparedStatement insertRecipeStmt;
    private final PreparedStatement insertRecipeInputItemStmt;
    private final PreparedStatement insertRecipeInputOreStmt;

    @Inject
    @SneakyThrows
    public CraftingRecipeService(@NonNull Connection conn) {
        insertRecipeStmt = conn.prepareStatement(
                "insert or ignore into crafting_table_recipe (output_item_registry_name, output_item_nbt, shaped) values (?, ?, ?)");
        insertRecipeInputItemStmt = conn.prepareStatement(
                "insert or ignore into crafting_table_recipe_input_item (item_registry_name, item_nbt, crafting_table_recipe_id, slot) values (?, ?, ?, ?)");
        insertRecipeInputOreStmt = conn.prepareStatement(
                "insert or ignore into crafting_table_recipe_input_ore (ore_name, crafting_table_recipe_id, slot) values (?, ?, ?)");
    }

    @SneakyThrows
    public CraftingTableRecipe createRecipe(Item output, boolean shaped) {
        val recipe =
                CraftingTableRecipe.builder().outputItem(output).shaped(shaped).build();
        insertRecipeStmt.setString(1, output.getRegistryName());
        insertRecipeStmt.setString(2, output.getNbt());
        insertRecipeStmt.setBoolean(3, shaped);
        insertRecipeStmt.executeUpdate();
        val rs = insertRecipeStmt.getGeneratedKeys();
        while (rs.next()) {
            recipe.setId(rs.getInt(1));
        }
        return recipe;
    }

    @SneakyThrows
    public void addRecipeInput(CraftingTableRecipe recipe, Item input, int slot) {
        insertRecipeInputItemStmt.setString(1, input.getRegistryName());
        insertRecipeInputItemStmt.setString(2, input.getNbt());
        insertRecipeInputItemStmt.setInt(3, recipe.getId());
        insertRecipeInputItemStmt.setInt(4, slot);
        insertRecipeInputItemStmt.executeUpdate();
    }

    @SneakyThrows
    public void addRecipeInput(CraftingTableRecipe recipe, Ore input, int slot) {
        insertRecipeInputOreStmt.setString(1, input.getName());
        insertRecipeInputOreStmt.setInt(2, recipe.getId());
        insertRecipeInputOreStmt.setInt(3, slot);
        insertRecipeInputOreStmt.executeUpdate();
    }
}
