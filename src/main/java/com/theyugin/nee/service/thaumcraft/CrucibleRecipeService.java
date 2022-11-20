package com.theyugin.nee.service.thaumcraft;

import com.google.inject.Inject;
import com.theyugin.nee.data.general.Item;
import com.theyugin.nee.data.general.Ore;
import com.theyugin.nee.data.thaumcraft.Aspect;
import com.theyugin.nee.data.thaumcraft.CrucibleRecipe;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;

public class CrucibleRecipeService {
    private final PreparedStatement crucibleInsertStmt;
    private final PreparedStatement crucibleInsertAspectStmt;
    private final PreparedStatement crucibleInsertCatalystItem;
    private final PreparedStatement crucibleInsertCatalystOre;

    @Inject
    @SneakyThrows
    public CrucibleRecipeService(Connection conn) {
        crucibleInsertStmt = conn.prepareStatement(
                "insert into thaumcraft_crucible_recipe (output_registry_name, output_nbt) values (?, ?)");
        crucibleInsertAspectStmt = conn.prepareStatement(
                "insert into thaumcraft_crucible_recipe_aspect (thaumcraft_crucible_recipe_id, aspect_tag, amount) values (?, ?, ?)");
        crucibleInsertCatalystItem = conn.prepareStatement(
                "insert into thaumcraft_crucible_recipe_catalyst_item (thaumcraft_crucible_recipe_id, item_registry_name, item_nbt) values (?, ?, ?)");
        crucibleInsertCatalystOre = conn.prepareStatement(
                "insert into thaumcraft_crucible_recipe_catalyst_ore (thaumcraft_crucible_recipe_id, ore_name) values (?, ?)");
    }

    @SneakyThrows
    private CrucibleRecipe createRecipe(Item output) {
        val recipe = CrucibleRecipe.builder().output(output).build();
        crucibleInsertStmt.setString(1, output.getRegistryName());
        crucibleInsertStmt.setString(2, output.getNbt());
        crucibleInsertStmt.executeUpdate();
        val ids = crucibleInsertStmt.getGeneratedKeys();
        while (ids.next()) {
            recipe.setId(ids.getInt(1));
        }
        crucibleInsertStmt.clearParameters();
        return recipe;
    }

    @SneakyThrows
    public CrucibleRecipe createRecipe(Ore catalyst, Item output) {
        val recipe = createRecipe(output);
        crucibleInsertCatalystOre.setInt(1, recipe.getId());
        crucibleInsertCatalystOre.setString(2, catalyst.getName());
        crucibleInsertCatalystOre.executeUpdate();
        return recipe;
    }

    @SneakyThrows
    public CrucibleRecipe createRecipe(Item catalyst, Item output) {
        val recipe = createRecipe(output);
        crucibleInsertCatalystItem.setInt(1, recipe.getId());
        crucibleInsertCatalystItem.setString(2, catalyst.getRegistryName());
        crucibleInsertCatalystItem.setString(3, catalyst.getNbt());
        crucibleInsertCatalystItem.executeUpdate();
        return recipe;
    }

    @SneakyThrows
    public void addAspect(CrucibleRecipe crucibleRecipe, Aspect aspect, int amount) {
        crucibleInsertAspectStmt.setInt(1, crucibleRecipe.getId());
        crucibleInsertAspectStmt.setString(2, aspect.getTag());
        crucibleInsertAspectStmt.setInt(3, amount);
        crucibleInsertAspectStmt.executeUpdate();
    }
}
