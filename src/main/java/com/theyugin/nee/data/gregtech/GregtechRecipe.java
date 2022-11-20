package com.theyugin.nee.data.gregtech;

import com.theyugin.nee.data.general.Catalyst;
import lombok.*;

@Data
@Builder
public class GregtechRecipe {
    private Integer id;
    private Integer voltage;
    private Integer amperage;
    private Integer duration;
    private Integer config;
    private Integer fuelValue;
    private Integer fuelMultiplier;
    private Boolean fuelRecipe;
    private Catalyst catalystName;
}
