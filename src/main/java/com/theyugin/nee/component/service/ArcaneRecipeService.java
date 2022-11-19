package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.persistence.general.Ore;
import com.theyugin.nee.persistence.thaumcraft.ArcaneRecipe;
import com.theyugin.nee.persistence.thaumcraft.Aspect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;

public class ArcaneRecipeService {
    private final PreparedStatement insertStmt;
    private final PreparedStatement insertInputItemStmt;
    private final PreparedStatement insertInputOreStmt;
    private final PreparedStatement insertAspectStmt;

    @Inject
    @SneakyThrows
    public ArcaneRecipeService(Connection conn) {
        insertStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_arcane_recipe (shaped, output_registry_name, output_nbt) values (?, ?, ?)");
        insertInputItemStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_arcane_recipe_input_item (thaumcraft_arcane_recipe_id, item_registry_name, item_nbt, slot) values (?, ?, ?, ?)");
        insertAspectStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_arcane_recipe_aspect (thaumcraft_arcane_recipe_id, aspect_tag, amount) values (?, ?, ?)");
        insertInputOreStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_arcane_recipe_input_ore (thaumcraft_arcane_recipe_id, ore_name, slot) values (?, ?, ?)");
    }

    @SneakyThrows
    public ArcaneRecipe createRecipe(Item output, Boolean shaped) {
        val recipe = ArcaneRecipe.builder().shaped(shaped).output(output).build();
        insertStmt.setBoolean(1, shaped);
        insertStmt.setString(2, output.getRegistryName());
        insertStmt.setString(3, output.getNbt());
        insertStmt.executeUpdate();
        val ids = insertStmt.getGeneratedKeys();
        while (ids.next()) {
            recipe.setId(ids.getInt(1));
        }
        return recipe;
    }

    @SneakyThrows
    public void addInput(ArcaneRecipe recipe, Item input, Integer slot) {
        insertInputItemStmt.setInt(1, recipe.getId());
        insertInputItemStmt.setString(2, input.getRegistryName());
        insertInputItemStmt.setString(3, input.getNbt());
        insertInputItemStmt.setInt(4, slot);
        insertInputItemStmt.executeUpdate();
    }

    @SneakyThrows
    public void addInput(ArcaneRecipe recipe, Ore input, Integer slot) {
        insertInputOreStmt.setInt(1, recipe.getId());
        insertInputOreStmt.setString(2, input.getName());
        insertInputOreStmt.setInt(3, slot);
        insertInputOreStmt.executeUpdate();
    }

    @SneakyThrows
    public void addAspect(ArcaneRecipe recipe, Aspect aspect, Integer amount) {
        insertAspectStmt.setInt(1, recipe.getId());
        insertAspectStmt.setString(2, aspect.getTag());
        insertAspectStmt.setInt(3, amount);
        insertAspectStmt.executeUpdate();
    }
}
