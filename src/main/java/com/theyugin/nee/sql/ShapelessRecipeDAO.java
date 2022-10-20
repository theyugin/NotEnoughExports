package com.theyugin.nee.sql;

import com.theyugin.nee.data.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ShapelessRecipeDAO extends DAO {
    public ShapelessRecipeDAO(Connection conn) {
        super(conn);
    }

    public ShapelessRecipe create(ItemStackMap inputItemStackMap, OreStackMap inputOreStackMap, Item output)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into shapelessRecipe (output) values (?)");
        stmt.setString(1, output.unlocalizedName);
        stmt.executeUpdate();

        ResultSet rs = conn.prepareStatement("select last_insert_rowid()").executeQuery();
        int recipeId = rs.getInt(1);

        stmt = conn.prepareStatement("insert or ignore into shapelessRecipeInputItem (recipe, item) values (?, ?)");
        for (Map.Entry<Integer, IStack<Item>> integerListEntry : inputItemStackMap.entrySet()) {
            for (Item item : integerListEntry.getValue().contents()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, item.unlocalizedName);
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();

        stmt = conn.prepareStatement("insert or ignore into shapelessRecipeInputOre (recipe, ore) values (?, ?)");
        for (Map.Entry<Integer, IStack<Ore>> integerListEntry : inputOreStackMap.entrySet()) {
            for (Ore ore : integerListEntry.getValue().contents()) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ore.name);
                stmt.addBatch();
                stmt.clearParameters();
            }
        }
        stmt.executeBatch();
        return new ShapelessRecipe(inputItemStackMap, inputOreStackMap, output);
    }
}
