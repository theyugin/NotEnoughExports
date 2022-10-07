package com.theyugin.nee.proxy;

import com.theyugin.nee.NotEnoughExports;
import cpw.mods.fml.common.event.*;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NotEnoughExports.warn("This mod is client-side only.");
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}
}
