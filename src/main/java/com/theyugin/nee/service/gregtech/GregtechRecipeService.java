package com.theyugin.nee.service.gregtech;

import com.theyugin.nee.data.general.Catalyst;
import com.theyugin.nee.data.gregtech.*;
import com.theyugin.nee.service.AbstractRecipeService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

public class GregtechRecipeService extends AbstractRecipeService {
    private final PreparedStatement insertRecipeStmt;
    private final PreparedStatement insertFuelStmt;

    @SneakyThrows
    public GregtechRecipeService(@NonNull Connection conn) {
        super(conn);
        insertRecipeStmt = conn.prepareStatement(
                "insert or ignore into gregtech_recipe (id, amperage, config, duration, voltage) values (?, ?, ?, ?, ?)");
        insertFuelStmt = conn.prepareStatement(
                "insert or ignore into gregtech_fuel_recipe (id, fuel_value, fuel_multiplier) values (?, ?, ?)");
    }

    @SneakyThrows
    public GregtechRecipe createRecipe(Catalyst catalyst, int voltage, int config, int duration, int amperage) {
        val recipe = new GregtechRecipe(
                createNew(catalyst.getName(), "gregtech_recipe"), voltage, amperage, duration, config);
        insertRecipeStmt.setInt(1, recipe.getId());
        insertRecipeStmt.setInt(2, amperage);
        insertRecipeStmt.setInt(3, config);
        insertRecipeStmt.setInt(4, duration);
        insertRecipeStmt.setInt(5, voltage);
        insertRecipeStmt.executeUpdate();
        return recipe;
    }

    @SneakyThrows
    public GregtechFuelRecipe createFuelRecipe(Catalyst catalyst, int value, int multiplier) {
        val recipe = new GregtechFuelRecipe(createNew(catalyst.getName(), "gregtech_fuel_recipe"), value, multiplier);
        insertFuelStmt.setInt(1, recipe.getId());
        insertFuelStmt.setInt(2, value);
        insertFuelStmt.setInt(3, multiplier);
        insertFuelStmt.executeUpdate();
        return recipe;
    }
}
