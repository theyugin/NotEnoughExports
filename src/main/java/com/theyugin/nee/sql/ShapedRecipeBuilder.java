package com.theyugin.nee.sql;

import com.theyugin.nee.data.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ShapedRecipeBuilder implements ICraftingTableRecipeBuilder<ShapedRecipe> {
    private Item output;
    private final ItemStackMap itemStackMap = new ItemStackMap();
    private final OreStackMap oreStackMap = new OreStackMap();

    public ShapedRecipeBuilder setOutput(Item output) {
        this.output = output;
        return this;
    }

    public ShapedRecipeBuilder addItemInput(Item item, int slot) {
        itemStackMap.accumulate(slot, item);
        return this;
    }

    public ShapedRecipeBuilder addOreInput(Ore ore, int slot) {
        oreStackMap.accumulate(slot, ore);
        return this;
    }

    public ShapedRecipe save(Connection conn) throws SQLException {
        if (this.output == null) {
            throw new SQLException("unset parameters");
        }

        PreparedStatement stmt = conn.prepareStatement("insert into shapedRecipe (output) values (?)");
        stmt.setString(1, output.unlocalizedName);
        stmt.executeUpdate();
        ResultSet rs = conn.prepareStatement("select last_insert_rowid()").executeQuery();
        int recipeId = rs.getInt(1);

        stmt = conn.prepareStatement(
                "insert or ignore into shapedRecipeInputItem (recipe, item, slot) values (?, ?, ?)");
        for (Map.Entry<Integer, IStack<Item>> integerListEntry : itemStackMap.entrySet()) {
            for (Item item : integerListEntry.getValue().contents()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, item.unlocalizedName);
                stmt.setInt(3, integerListEntry.getKey());
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();

        stmt = conn.prepareStatement("insert or ignore into shapedRecipeInputOre (recipe, ore, slot) values (?, ?, ?)");
        for (Map.Entry<Integer, IStack<Ore>> integerListEntry : oreStackMap.entrySet()) {
            for (Ore ore : integerListEntry.getValue().contents()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ore.name);
                stmt.setInt(3, integerListEntry.getKey());
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();
        return new ShapedRecipe(itemStackMap, oreStackMap, output);
    }
}
