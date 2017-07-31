package me.itstheholyblack.vigilant_eureka.proxy;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.ModBlocks;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.client.Keybinds;
import me.itstheholyblack.vigilant_eureka.client.LeyLineTileRenderer;
import me.itstheholyblack.vigilant_eureka.core.InputHandler;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(value = {Side.CLIENT}, modid = Reference.MOD_ID)
public class ClientProxy extends CommonProxy {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
        ModItems.initModels();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        Keybinds.initWarpKey();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new InputHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(LeyLineTile.class, new LeyLineTileRenderer());
    }
}
