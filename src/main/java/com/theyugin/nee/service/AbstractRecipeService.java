package com.theyugin.nee.service;

import com.theyugin.nee.data.IRecipe;
import com.theyugin.nee.data.general.Fluid;
import com.theyugin.nee.data.general.Item;
import com.theyugin.nee.data.general.Ore;
import com.theyugin.nee.data.thaumcraft.Aspect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;

public abstract class AbstractRecipeService {
    private final PreparedStatement createRecipeStmt;
    private final PreparedStatement addItemInputStmt;
    private final PreparedStatement addFluidInputStmt;
    private final PreparedStatement addOreInputStmt;
    private final PreparedStatement addAspectInputStmt;
    private final PreparedStatement addItemOutputStmt;
    private final PreparedStatement addFluidOutputStmt;

    @SneakyThrows
    protected AbstractRecipeService(Connection conn) {
        createRecipeStmt = conn.prepareStatement("insert into recipe (catalyst_name, info_table) values (?, ?)");
        addItemInputStmt = conn.prepareStatement(
                "insert or ignore into recipe_input_item (recipe_id, item_registry_name, item_nbt, slot, amount) values (?, ?, ?, ?, ?)");
        addFluidInputStmt = conn.prepareStatement(
                "insert or ignore into recipe_input_fluid (recipe_id, fluid_registry_name, fluid_nbt, slot, amount) values (?, ?, ?, ?, ?)");
        addOreInputStmt = conn.prepareStatement(
                "insert or ignore into recipe_input_ore (recipe_id, ore_name, slot, amount) values (?, ?, ?, ?)");
        addAspectInputStmt = conn.prepareStatement(
                "insert or ignore into recipe_input_aspect (recipe_id, aspect_tag, amount) values (?, ?, ?)");
        addItemOutputStmt = conn.prepareStatement(
                "insert or ignore into recipe_output_item (recipe_id, item_registry_name, item_nbt, slot, amount, chance) values (?, ?, ?, ?, ?, ?)");
        addFluidOutputStmt = conn.prepareStatement(
                "insert or ignore into recipe_output_fluid (recipe_id, fluid_registry_name, fluid_nbt, slot, amount) values (?, ?, ?, ?, ?)");
    }

    @SneakyThrows
    protected int createNew(String catalyst, String infoTable) {
        createRecipeStmt.setString(1, catalyst);
        createRecipeStmt.setString(2, infoTable);
        createRecipeStmt.executeUpdate();
        val rs = createRecipeStmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    @SneakyThrows
    public void addInput(IRecipe recipe, Item item, int slot, int amount) {
        addItemInputStmt.setInt(1, recipe.getId());
        addItemInputStmt.setString(2, item.getRegistryName());
        addItemInputStmt.setString(3, item.getNbt());
        addItemInputStmt.setInt(4, slot);
        addItemInputStmt.setInt(5, amount);
        addItemInputStmt.executeUpdate();
    }

    public void addInput(IRecipe recipe, Item item, int slot) {
        addInput(recipe, item, slot, 1);
    }

    public void addInput(IRecipe recipe, Item item) {
        addInput(recipe, item, 0);
    }

    @SneakyThrows
    public void addInput(IRecipe recipe, Fluid fluid, int slot, int amount) {
        addFluidInputStmt.setInt(1, recipe.getId());
        addFluidInputStmt.setString(2, fluid.getRegistryName());
        addFluidInputStmt.setString(3, fluid.getNbt());
        addFluidInputStmt.setInt(4, slot);
        addFluidInputStmt.setInt(5, amount);
        addItemInputStmt.executeUpdate();
    }

    @SneakyThrows
    public void addInput(IRecipe recipe, Aspect aspect, int amount) {
        addAspectInputStmt.setInt(1, recipe.getId());
        addAspectInputStmt.setString(2, aspect.getTag());
        addAspectInputStmt.setInt(3, amount);
        addAspectInputStmt.executeUpdate();
    }

    @SneakyThrows
    public void addInput(IRecipe recipe, Ore ore, int slot, int amount) {
        addOreInputStmt.setInt(1, recipe.getId());
        addOreInputStmt.setString(2, ore.getName());
        addOreInputStmt.setInt(3, slot);
        addOreInputStmt.setInt(4, amount);
        addOreInputStmt.executeUpdate();
    }

    public void addInput(IRecipe recipe, Ore ore, int slot) {
        addInput(recipe, ore, slot, 1);
    }

    public void addInput(IRecipe recipe, Ore ore) {
        addInput(recipe, ore, 0);
    }

    @SneakyThrows
    public void addOutput(IRecipe recipe, Item item, int slot, int amount, int chance) {
        addItemOutputStmt.setInt(1, recipe.getId());
        addItemOutputStmt.setString(2, item.getRegistryName());
        addItemOutputStmt.setString(3, item.getNbt());
        addItemOutputStmt.setInt(4, slot);
        addItemOutputStmt.setInt(5, amount);
        addItemOutputStmt.setInt(6, chance);
        addItemOutputStmt.executeUpdate();
    }

    public void addOutput(IRecipe recipe, Item item, int slot, int amount) {
        addOutput(recipe, item, slot, amount, 10000);
    }

    public void addOutput(IRecipe recipe, Item item, int slot) {
        addOutput(recipe, item, slot, 1);
    }

    public void addOutput(IRecipe recipe, Item item) {
        addOutput(recipe, item, 0);
    }

    @SneakyThrows
    public void addOutput(IRecipe recipe, Fluid fluid, int slot, int amount) {
        addFluidOutputStmt.setInt(1, recipe.getId());
        addFluidOutputStmt.setString(2, fluid.getRegistryName());
        addFluidOutputStmt.setString(3, fluid.getNbt());
        addFluidOutputStmt.setInt(4, slot);
        addFluidOutputStmt.setInt(5, amount);
        addFluidOutputStmt.executeUpdate();
    }
}
