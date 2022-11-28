package com.theyugin.nee.data.thaumcraft;

import com.theyugin.nee.data.IRecipe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArcaneRecipe implements IRecipe {
    private final int id;
    private final boolean shaped;
}
