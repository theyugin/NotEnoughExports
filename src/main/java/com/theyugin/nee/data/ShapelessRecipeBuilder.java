package com.theyugin.nee.data;

import com.theyugin.nee.util.ItemInputMap;
import com.theyugin.nee.util.OreInputMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ShapelessRecipeBuilder implements ICraftingTableRecipeBuilder<ShapelessRecipe> {
    private final Item output;
    private final ItemInputMap itemInputMap = new ItemInputMap();
    private final OreInputMap oreInputMap = new OreInputMap();

    private ShapelessRecipeBuilder(Item output) {
        this.output = output;
    }

    public static ShapelessRecipeBuilder fromOutput(Item output) {
        return new ShapelessRecipeBuilder(output);
    }

    public ShapelessRecipeBuilder addItemInput(Item item, int slot) {
        itemInputMap.accumulate(slot, item);
        return this;
    }

    public ShapelessRecipeBuilder addOreInput(Ore ore, int slot) {
        oreInputMap.accumulate(slot, ore);
        return this;
    }

    public ShapelessRecipe save(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into shapelessRecipe (output) values (?)");
        stmt.setString(1, output.unlocalizedName);
        stmt.executeUpdate();

        ResultSet rs = conn.prepareStatement("select last_insert_rowid()").executeQuery();
        int recipeId = rs.getInt(1);

        stmt = conn.prepareStatement("insert or ignore into shapelessRecipeInputItem (recipe, item) values (?, ?)");
        for (Map.Entry<Integer, List<Item>> integerListEntry : itemInputMap.entrySet()) {
            for (Item item : integerListEntry.getValue()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, item.unlocalizedName);
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();

        stmt = conn.prepareStatement("insert or ignore into shapelessRecipeInputOre (recipe, ore) values (?, ?)");
        for (Map.Entry<Integer, List<Ore>> integerListEntry : oreInputMap.entrySet()) {
            for (Ore ore : integerListEntry.getValue()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ore.name);
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();
        return new ShapelessRecipe(itemInputMap, oreInputMap, output);
    }
}
