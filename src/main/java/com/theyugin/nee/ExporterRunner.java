package com.theyugin.nee;

import com.theyugin.nee.export.CraftingTableExporter;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class ExporterRunner implements Runnable {
    private static final List<String> tableDefs = new ArrayList<String>() {{
        add("create table item (unlocalizedName text primary key, localizedName text)");
        add("create table fluid (unlocalizedName text primary key, localizedName text)");
        add("create table ore (name text primary key)");
        add("create table oreItem (item text references item, name text references ore, primary key (item, name))");

        add("create table catalystType (name text primary key)");
        add("create table catalystTypeItem (name text references catalystType, item text references item, primary key (name, item))");

        add("create table shapedRecipe (id integer primary key, output text references item)");
        add("create table shapedRecipeInputItem (recipe integer references shapedRecipe, item text references item, slot int, primary key (recipe, item, slot))");
        add("create table shapedRecipeInputOre (recipe integer references shapedRecipe, ore text references ore, slot int, primary key(recipe, ore, slot))");

        add("create table shapelessRecipe (id integer primary key, output text references item)");
        add("create table shapelessRecipeInputItem (recipe integer references shapelessRecipe, item text references item, primary key (recipe, item))");
        add("create table shapelessRecipeInputOre (recipe integer references shapelessRecipe, ore text references ore, primary key(recipe, ore))");

        add("create table gregtechRecipe (id integer primary key, eut integer, duration integer, config integer, catalyst text references catalystType)");
        add("create table gregtechRecipeInputItem (recipe integer references gregtechRecipe, item text references item, slot integer, primary key (recipe, item, slot))");
        add("create table gregtechRecipeInputFluid (recipe integer references gregtechRecipe, fluid text references fluid, slot integer, primary key (recipe, fluid, slot))");
        add("create table gregtechRecipeInputOre (recipe integer references gregtechRecipe, ore text references ore, slot integer, primary key (recipe, ore, slot))");
        add("create table gregtechRecipeOutputItem (recipe integer references gregtechRecipe, item text references item, slot integer, primary key (recipe, item, slot))");
        add("create table gregtechRecipeOutputFluid (recipe integer references gregtechRecipe, fluid text references fluid, slot integer, primary key (recipe, fluid, slot))");
    }};

    public void run() {

        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.enforceForeignKeys(true);
        sqLiteConfig.setJournalMode(SQLiteConfig.JournalMode.WAL);
        sqLiteConfig.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        sqLiteConfig.setTempStore(SQLiteConfig.TempStore.MEMORY);

        SQLiteDataSource ds = new SQLiteDataSource(sqLiteConfig);

        File dbFile = new File("nee.sqlite");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        ds.setUrl("jdbc:sqlite:nee.sqlite");

        try (Connection conn = ds.getConnection()) {
            Statement stmt = conn.createStatement();
            for (String tableDef : tableDefs) {
                stmt.addBatch(tableDef);
            }
            stmt.executeBatch();
            CraftingTableExporter.run(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
