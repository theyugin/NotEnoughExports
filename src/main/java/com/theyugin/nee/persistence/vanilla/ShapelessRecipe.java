package com.theyugin.nee.persistence.vanilla;

import com.theyugin.nee.persistence.general.Item;
import lombok.*;

@Data
@Builder
public class ShapelessRecipe implements ICraftingTableRecipe {
    private int id;
    private Item outputItem;
}
