package com.theyugin.nee;

import cpw.mods.fml.common.Loader;

public enum LoadedMods {
    GREGTECH("gregtech"),
    IC2("IC2"),
    AE2("appliedenergistics2"),
    FORESTRY("Forestry"),
    ENDERIO("enderio"),
    GTPLUSLUS("miscutils"),
    ;
    private final String modId;
    private boolean isLoaded = false;

    LoadedMods(String modId) {
        this.modId = modId;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public static void checkMods() {
        for (LoadedMods mod : LoadedMods.values()) {
            if (Loader.isModLoaded(mod.modId)) mod.isLoaded = true;
        }
    }
}
