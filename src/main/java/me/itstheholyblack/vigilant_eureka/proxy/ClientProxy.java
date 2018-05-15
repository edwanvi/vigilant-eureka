package me.itstheholyblack.vigilant_eureka.proxy;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.ModBlocks;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.client.Icons;
import me.itstheholyblack.vigilant_eureka.client.Keybinds;
import me.itstheholyblack.vigilant_eureka.client.LeyLineTileRenderer;
import me.itstheholyblack.vigilant_eureka.client.renderer.CustomBipedArmor;
import me.itstheholyblack.vigilant_eureka.core.InputHandler;
import me.itstheholyblack.vigilant_eureka.entity.ModEntities;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.ListIterator;

@Mod.EventBusSubscriber(value = {Side.CLIENT}, modid = Reference.MOD_ID)
public class ClientProxy extends CommonProxy {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
        ModItems.initModels();
        ModEntities.initModels();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(Icons.INSTANCE);
        Keybinds.initWarpKey();
    }

    /**
     * Init method.
     * Crazy one-liner courtesy of Paul Fulham.
     *
     * @author Paul Fulham
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new InputHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(LeyLineTile.class, new LeyLineTileRenderer());
        Minecraft.getMinecraft().getRenderManager().getSkinMap().values().forEach(r -> {
            List<LayerRenderer<EntityLivingBase>> layers =
                    ReflectionHelper.getPrivateValue(RenderLivingBase.class, r, "field_177097_h", "layerRenderers");
            ListIterator<LayerRenderer<EntityLivingBase>> iter = layers.listIterator();
            while (iter.hasNext()) {
                LayerRenderer<EntityLivingBase> layer = iter.next();
                if (layer instanceof LayerBipedArmor) {
                    iter.set(new CustomBipedArmor((LayerBipedArmor) layer));
                }
            }
        });
    }
}
