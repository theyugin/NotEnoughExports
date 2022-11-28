package com.theyugin.nee.data.general;

import lombok.*;

@Data
@AllArgsConstructor
public class Fluid {
    private final String registryName;
    private final String displayName;
    private final String nbt;
}
