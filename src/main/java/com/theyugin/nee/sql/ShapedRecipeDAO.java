package com.theyugin.nee.sql;

import com.theyugin.nee.data.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ShapedRecipeDAO extends DAO {
    public ShapedRecipeDAO(Connection conn) {
        super(conn);
    }

    public ShapedRecipe create(ItemStackMap inputItemStackMap, OreStackMap inputOreStackMap, Item output)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into shapedRecipe (output) values (?)");
        stmt.setString(1, output.unlocalizedName);
        stmt.executeUpdate();
        ResultSet rs = conn.prepareStatement("select last_insert_rowid()").executeQuery();
        int recipeId = rs.getInt(1);

        stmt = conn.prepareStatement(
                "insert or ignore into shapedRecipeInputItem (recipe, item, slot) values (?, ?, ?)");
        for (Map.Entry<Integer, IStack<Item>> integerListEntry : inputItemStackMap.entrySet()) {
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
        for (Map.Entry<Integer, IStack<Ore>> integerListEntry : inputOreStackMap.entrySet()) {
            for (Ore ore : integerListEntry.getValue().contents()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ore.name);
                stmt.setInt(3, integerListEntry.getKey());
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();
        return new ShapedRecipe(inputItemStackMap, inputOreStackMap, output);
    }
}
