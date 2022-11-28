package com.theyugin.nee.data.vanilla;

import com.theyugin.nee.data.IRecipe;
import lombok.*;

@Data
@AllArgsConstructor
public class CraftingTableRecipe implements IRecipe {
    private final int id;
    private final boolean shaped;
}
