package com.theyugin.nee.proxy;

import com.theyugin.nee.ExporterRunner;
import com.theyugin.nee.LoadedMods;
import com.theyugin.nee.input.KeyBindings;
import com.theyugin.nee.input.KeyBindingsHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;

public class ClientProxy extends CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new KeyBindingsHandler());
    }

    public void init(FMLInitializationEvent event) {
        KeyBindings.register();
    }

    public void postInit(FMLPostInitializationEvent event) {
        LoadedMods.GREGTECH = Loader.isModLoaded("gregtech");
        LoadedMods.AE2 = Loader.isModLoaded("appliedenergistics2");
        LoadedMods.IC2 = Loader.isModLoaded("IC2");
        LoadedMods.EIO = Loader.isModLoaded("enderio");
        LoadedMods.FORESTRY = Loader.isModLoaded("Forestry");
    }
}
