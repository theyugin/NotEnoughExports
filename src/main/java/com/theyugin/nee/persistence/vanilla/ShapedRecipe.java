package com.theyugin.nee.persistence.vanilla;

import com.theyugin.nee.persistence.general.Item;
import lombok.*;

@Data
@Builder
public class ShapedRecipe implements ICraftingTableRecipe {
    private int id;
    private final Item outputItem;
}
