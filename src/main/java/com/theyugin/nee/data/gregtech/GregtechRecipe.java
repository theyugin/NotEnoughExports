package com.theyugin.nee.data.gregtech;

import lombok.*;

@Data
@AllArgsConstructor
public class GregtechRecipe implements IGregtechRecipe {
    private final int id;
    private final int voltage;
    private final int amperage;
    private final int duration;
    private final int config;
}
