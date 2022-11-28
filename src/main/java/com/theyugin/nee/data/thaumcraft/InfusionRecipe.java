package com.theyugin.nee.data.thaumcraft;

import com.theyugin.nee.data.IRecipe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfusionRecipe implements IRecipe {
    private final int id;
    private final String research;
    private final int instability;
}
