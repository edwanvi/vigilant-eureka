package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":dim_key")
    public static DimKey dimKey;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        dimKey.initModel();
    }
}
