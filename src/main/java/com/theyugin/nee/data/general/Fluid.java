package com.theyugin.nee.data.general;

import lombok.*;

@Data
@Builder
public class Fluid {
    private String registryName;
    private String displayName;
    private String nbt;
    private byte[] icon;
}
