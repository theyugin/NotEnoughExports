package com.theyugin.nee.persistence.general;

import lombok.*;

@Data
@Builder
public class Item {
    private String registryName;
    private String displayName;
    private String nbt;
    private byte[] icon;
}
