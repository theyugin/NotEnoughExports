package com.theyugin.nee.persistence.thaumcraft;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Aspect {
    private String tag;
    private String name;
    private byte[] icon;
}
