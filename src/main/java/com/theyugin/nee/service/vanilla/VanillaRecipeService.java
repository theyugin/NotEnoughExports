package com.theyugin.nee.service.vanilla;

import com.theyugin.nee.data.vanilla.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.theyugin.nee.service.AbstractRecipeService;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

public class VanillaRecipeService extends AbstractRecipeService {
    private final PreparedStatement insertRecipeStmt;

    @SneakyThrows
    public VanillaRecipeService(@NonNull Connection conn) {
        super(conn);
        insertRecipeStmt =
                conn.prepareStatement("insert or ignore into crafting_table_recipe (id, shaped) values (?, ?)");
    }

    @SneakyThrows
    public CraftingTableRecipe createCraftingRecipe(boolean shaped) {
        val recipe = new CraftingTableRecipe(createNew("crafting", "crafting_table_recipe"), shaped);
        insertRecipeStmt.setInt(1, recipe.getId());
        insertRecipeStmt.setBoolean(2, shaped);
        insertRecipeStmt.executeUpdate();
        return recipe;
    }
}
