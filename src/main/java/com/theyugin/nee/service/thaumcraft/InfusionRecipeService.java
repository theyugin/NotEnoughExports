package com.theyugin.nee.service.thaumcraft;

import com.google.inject.Inject;
import com.theyugin.nee.data.general.Item;
import com.theyugin.nee.data.thaumcraft.Aspect;
import com.theyugin.nee.data.thaumcraft.InfusionRecipe;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;

public class InfusionRecipeService {
    private final PreparedStatement insertStmt;
    private final PreparedStatement insertComponentStmt;
    private final PreparedStatement insertAspectStmt;

    @Inject
    @SneakyThrows
    public InfusionRecipeService(Connection conn) {
        insertStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_infusion_recipe (instability, research, output_item_registry_name, output_item_nbt, input_item_registry_name, input_item_nbt)  values (?, ?, ?, ?, ?, ?)");
        insertComponentStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_infusion_recipe_component (thaumcraft_infusion_recipe_id, item_registry_name, item_nbt) values (?, ?, ?)");
        insertAspectStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_infusion_recipe_aspect (thaumcraft_infusion_recipe_id, aspect_tag, amount) values (?, ?, ?)");
    }

    @SneakyThrows
    public InfusionRecipe createRecipe(Item input, Item output, String research, Integer instability) {
        val recipe = InfusionRecipe.builder()
                .research(research)
                .instability(instability)
                .input(input)
                .output(output)
                .build();
        insertStmt.setInt(1, instability);
        insertStmt.setString(2, research);
        insertStmt.setString(3, output.getRegistryName());
        insertStmt.setString(4, output.getNbt());
        insertStmt.setString(5, input.getRegistryName());
        insertStmt.setString(6, input.getNbt());
        insertStmt.executeUpdate();
        val ids = insertStmt.getGeneratedKeys();
        while (ids.next()) {
            recipe.setId(ids.getInt(1));
        }
        return recipe;
    }

    @SneakyThrows
    public void addComponent(InfusionRecipe recipe, Item item) {
        insertComponentStmt.setInt(1, recipe.getId());
        insertComponentStmt.setString(2, item.getRegistryName());
        insertComponentStmt.setString(3, item.getNbt());
        insertComponentStmt.executeUpdate();
    }

    @SneakyThrows
    public void addAspect(InfusionRecipe recipe, Aspect aspect, Integer amount) {
        insertAspectStmt.setInt(1, recipe.getId());
        insertAspectStmt.setString(2, aspect.getTag());
        insertAspectStmt.setInt(3, amount);
        insertAspectStmt.executeUpdate();
    }
}
