package com.theyugin.nee.persistence.vanilla;

import com.theyugin.nee.persistence.general.Item;
import lombok.*;

@Data
@Builder
public class CraftingTableRecipe {
    private int id;
    private boolean shaped;
    private final Item outputItem;
}
