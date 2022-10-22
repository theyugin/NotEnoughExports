package com.theyugin.nee.export;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.RecipeCatalysts;
import com.theyugin.nee.data.Item;
import com.theyugin.nee.sql.CatalystTypeDAO;
import com.theyugin.nee.sql.ItemDAO;
import com.theyugin.nee.util.StackUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.ItemStack;

public class CatalystExporter implements IExporter {
    private int progress = 0;
    private int total = 0;
    private boolean running = true;

    public int progress() {
        return progress;
    }

    public int total() {
        return total;
    }

    public String name() {
        return "catalysts";
    }

    @Override
    public boolean running() {
        return running;
    }

    private final ItemDAO itemDAO;
    private final CatalystTypeDAO catalystTypeDAO;

    public CatalystExporter(Connection conn) {
        itemDAO = new ItemDAO(conn);
        catalystTypeDAO = new CatalystTypeDAO(conn);
    }

    public void run() throws SQLException {
        Map<String, List<PositionedStack>> catalystMap = RecipeCatalysts.getPositionedRecipeCatalystMap();
        total = catalystMap.size();
        for (Map.Entry<String, List<PositionedStack>> stringListEntry : catalystMap.entrySet()) {
            progress++;
            Set<Item> catalystItems = new HashSet<>();
            for (PositionedStack positionedStack : stringListEntry.getValue()) {
                for (ItemStack itemStack : positionedStack.items) {
                    Item item = StackUtils.createFromStack(itemDAO, itemStack);
                    catalystItems.add(item);
                }
                catalystTypeDAO.create(stringListEntry.getKey(), catalystItems);
            }
        }
        running = false;
    }
}
