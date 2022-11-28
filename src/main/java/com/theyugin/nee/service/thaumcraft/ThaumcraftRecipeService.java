package com.theyugin.nee.service.thaumcraft;

import com.theyugin.nee.data.thaumcraft.ArcaneRecipe;
import com.theyugin.nee.data.thaumcraft.CrucibleRecipe;
import com.theyugin.nee.data.thaumcraft.InfusionRecipe;
import com.theyugin.nee.service.AbstractRecipeService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;

public class ThaumcraftRecipeService extends AbstractRecipeService {
    private final PreparedStatement insertArcaneRecipeStmt;
    private final PreparedStatement insertInfusionRecipeStmt;

    @SneakyThrows
    public ThaumcraftRecipeService(Connection conn) {
        super(conn);
        insertArcaneRecipeStmt =
                conn.prepareStatement("insert or ignore into thaumcraft_arcane_recipe (id,shaped) values (?, ?)");
        insertInfusionRecipeStmt = conn.prepareStatement(
                "insert or ignore into thaumcraft_infusion_recipe (id, instability, research) values (?, ?, ?)");
    }

    public CrucibleRecipe createCrucibleRecipe() {
        return new CrucibleRecipe(
                createNew("com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler", ""));
    }

    @SneakyThrows
    public ArcaneRecipe createArcaneRecipe(boolean shaped) {
        val catalyst = shaped
                ? "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler"
                : "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapelessRecipeHandler";
        val recipe = new ArcaneRecipe(createNew(catalyst, "thaumcraft_arcane_recipe"), shaped);
        insertArcaneRecipeStmt.setInt(1, recipe.getId());
        insertArcaneRecipeStmt.setBoolean(2, shaped);
        insertArcaneRecipeStmt.executeUpdate();
        return recipe;
    }

    @SneakyThrows
    public InfusionRecipe createInfusionRecipe(String research, int instability) {
        val recipe = new InfusionRecipe(
                createNew(
                        "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.InfusionRecipeHandler",
                        "thaumcraft_infusion_recipe"),
                research,
                instability);
        insertInfusionRecipeStmt.setInt(1, recipe.getId());
        insertInfusionRecipeStmt.setInt(2, instability);
        insertInfusionRecipeStmt.setString(3, research);
        insertInfusionRecipeStmt.executeUpdate();
        return recipe;
    }
}
