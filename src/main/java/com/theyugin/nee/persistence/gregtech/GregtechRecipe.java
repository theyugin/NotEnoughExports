package com.theyugin.nee.persistence.gregtech;

import com.theyugin.nee.persistence.general.Catalyst;
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
