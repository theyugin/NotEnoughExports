package com.theyugin.nee.data;

import com.theyugin.nee.util.ItemInputMap;
import com.theyugin.nee.util.OreInputMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ShapedRecipeBuilder implements ICraftingTableRecipeBuilder<ShapedRecipe> {
    private final Item output;
    private final ItemInputMap itemInputMap = new ItemInputMap();
    private final OreInputMap oreInputMap = new OreInputMap();

    private ShapedRecipeBuilder(Item output) {
        this.output = output;
    }

    public static ShapedRecipeBuilder fromOutput(Item output) {
        return new ShapedRecipeBuilder(output);
    }

    public ShapedRecipeBuilder addItemInput(Item item, int slot) {
        itemInputMap.accumulate(slot, item);
        return this;
    }

    public ShapedRecipeBuilder addOreInput(Ore ore, int slot) {
        oreInputMap.accumulate(slot, ore);
        return this;
    }

    public ShapedRecipe save(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into shapedRecipe (output) values (?)");
        stmt.setString(1, output.unlocalizedName);
        stmt.executeUpdate();
        ResultSet rs = conn.prepareStatement("select last_insert_rowid()").executeQuery();
        int recipeId = rs.getInt(1);

        stmt = conn.prepareStatement("insert or ignore into shapedRecipeInputItem (recipe, item, slot) values (?, ?, ?)");
        for (Map.Entry<Integer, List<Item>> integerListEntry : itemInputMap.entrySet()) {
            for (Item item : integerListEntry.getValue()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, item.unlocalizedName);
                stmt.setInt(3, integerListEntry.getKey());
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();

        stmt = conn.prepareStatement("insert or ignore into shapedRecipeInputOre (recipe, ore, slot) values (?, ?, ?)");
        for (Map.Entry<Integer, List<Ore>> integerListEntry : oreInputMap.entrySet()) {
            for (Ore ore : integerListEntry.getValue()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ore.name);
                stmt.setInt(3, integerListEntry.getKey());
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();
        return new ShapedRecipe(itemInputMap, oreInputMap, output);
    }
}
