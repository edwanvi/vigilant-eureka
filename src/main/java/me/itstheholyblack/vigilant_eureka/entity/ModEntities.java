package me.itstheholyblack.vigilant_eureka.entity;

import me.itstheholyblack.vigilant_eureka.Eureka;
import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.entity.render.RenderEntityCard;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntities {
    public static void init() {
        int id = 1;
        EntityRegistry.registerModEntity(
                new ResourceLocation("thrown_card"),
                EntityCard.class,
                Reference.MOD_ID + ":thrown_card",
                ++id,
                Eureka.instance,
                64,
                1,
                true
        );
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCard.class, RenderEntityCard.FACTORY);
    }
}
