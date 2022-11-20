package com.theyugin.nee.data.thaumcraft;

import com.theyugin.nee.data.general.Item;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArcaneRecipe {
    private Integer id;
    private Boolean shaped;
    private Item output;
}
