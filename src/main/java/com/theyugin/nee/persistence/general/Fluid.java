package com.theyugin.nee.persistence.general;

import lombok.*;

@Data
@Builder
public class Fluid {
    private String registryName;
    private String displayName;
    private byte[] icon;
}
