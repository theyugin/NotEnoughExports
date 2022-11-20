package com.theyugin.nee.data.thaumcraft;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Aspect {
    private String tag;
    private String name;
    private byte[] icon;
}
