package me.itstheholyblack.vigilant_eureka;

import me.itstheholyblack.vigilant_eureka.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class Eureka {

    @SidedProxy(clientSide = "me.itstheholyblack.vigilant_eureka.proxy.ClientProxy", serverSide = "me.itstheholyblack.vigilant_eureka.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static Eureka instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
