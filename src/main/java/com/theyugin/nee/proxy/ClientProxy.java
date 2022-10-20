package com.theyugin.nee.proxy;

import com.theyugin.nee.LoadedMods;
import com.theyugin.nee.input.KeyBindings;
import com.theyugin.nee.input.KeyBindingsHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;

public class ClientProxy extends CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new KeyBindingsHandler());
    }

    public void init(FMLInitializationEvent event) {
        KeyBindings.register();
    }

    public void postInit(FMLPostInitializationEvent event) {
        LoadedMods.checkMods();
    }
}
