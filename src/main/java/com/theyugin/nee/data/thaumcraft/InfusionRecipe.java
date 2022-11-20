package com.theyugin.nee.data.thaumcraft;

import com.theyugin.nee.data.general.Item;
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
