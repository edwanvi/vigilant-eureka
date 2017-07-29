package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.armor.InvisCap;
import me.itstheholyblack.vigilant_eureka.items.armor.WarpBoots;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":dim_key")
    public static DimKey dimKey;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":warp_boots")
    public static WarpBoots warpBoots;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":invis_cap")
    public static InvisCap invisCap;

    public static final CreativeTabs CREATIVETAB = new CreativeTabs("vigilantEureka") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(dimKey);
        }
    };

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        dimKey.initModel();
        warpBoots.initModel();
        invisCap.initModel();
    }
}
