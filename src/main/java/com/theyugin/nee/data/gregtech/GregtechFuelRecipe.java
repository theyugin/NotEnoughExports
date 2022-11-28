package com.theyugin.nee.data.gregtech;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GregtechFuelRecipe implements IGregtechRecipe {
    private final int id;
    private final int fuelValue;
    private final int fuelMultiplier;
}
