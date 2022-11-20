package com.theyugin.nee.proxy;

import com.theyugin.nee.LoadedMods;
import com.theyugin.nee.config.Config;
import com.theyugin.nee.handler.KeyBindingsHandler;
import com.theyugin.nee.input.KeyBindings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;

public class ClientProxy extends CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration();
        FMLCommonHandler.instance().bus().register(new KeyBindingsHandler());
    }

    public void init(FMLInitializationEvent event) {
        KeyBindings.register();
    }

    public void postInit(FMLPostInitializationEvent event) {
        LoadedMods.checkMods();
    }
}
