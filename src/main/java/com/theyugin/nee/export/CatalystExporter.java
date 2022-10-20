package com.theyugin.nee.export;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.RecipeCatalysts;
import com.theyugin.nee.sql.CatalystTypeBuilder;
import com.theyugin.nee.sql.ItemBuilder;
import com.theyugin.nee.util.ItemUtils;
import com.theyugin.nee.util.StackRenderer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class CatalystExporter {
    public static void run(Connection conn) throws SQLException {
        Map<String, List<PositionedStack>> catalystMap = RecipeCatalysts.getPositionedRecipeCatalystMap();
        for (Map.Entry<String, List<PositionedStack>> stringListEntry : catalystMap.entrySet()) {
            CatalystTypeBuilder catalystTypeBuilder = new CatalystTypeBuilder().setName(stringListEntry.getKey());
            for (PositionedStack positionedStack : stringListEntry.getValue()) {
                for (ItemStack itemStack : positionedStack.items) {
                    catalystTypeBuilder.addItem(new ItemBuilder()
                            .setUnlocalizedName(ItemUtils.getUnlocalizedNameSafe(itemStack))
                            .setLocalizedName(ItemUtils.getLocalizedNameSafe(itemStack))
                            .setIcon(StackRenderer.renderIcon(itemStack))
                            .save(conn));
                }
                catalystTypeBuilder.save(conn);
            }
        }
    }
}
