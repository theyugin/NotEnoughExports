package com.theyugin.nee.persistence.general;

import lombok.*;

@Data
@Builder
public class OreItem {
    private Ore ore;
    private Item item;
}
