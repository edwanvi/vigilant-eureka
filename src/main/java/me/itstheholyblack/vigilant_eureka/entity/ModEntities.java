package me.itstheholyblack.vigilant_eureka.entity;

import me.itstheholyblack.vigilant_eureka.Eureka;
import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
    public static void init() {
        int id = 1;
        EntityRegistry.registerModEntity(
                new ResourceLocation("thrown_card"),
                EntityCard.class,
                Reference.MOD_ID + ":thrown_card",
                id++,
                Eureka.instance,
                64,
                1,
                true
        );
    }
}
