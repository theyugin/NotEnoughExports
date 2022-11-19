package com.theyugin.nee.persistence.thaumcraft;

import com.theyugin.nee.persistence.general.Item;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArcaneRecipe {
    private Integer id;
    private Boolean shaped;
    private Item output;
}
