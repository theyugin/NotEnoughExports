package com.theyugin.nee.persistence.thaumcraft;

import com.theyugin.nee.persistence.general.Item;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfusionRecipe {
    private Integer id;
    private String research;
    private Integer instability;
    private Item input;
    private Item output;
}
