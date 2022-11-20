package com.theyugin.nee.data.vanilla;

import com.theyugin.nee.data.general.Item;
import lombok.*;

@Data
@Builder
public class CraftingTableRecipe {
    private int id;
    private boolean shaped;
    private final Item outputItem;
}
