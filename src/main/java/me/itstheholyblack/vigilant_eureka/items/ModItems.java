package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.armor.InvisCap;
import me.itstheholyblack.vigilant_eureka.items.baubles.ItemTime;
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
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":bismite")
    public static ItemBismite bismite;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":ley_key")
    public static ItemLeyKey leyKey;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":ley_rune")
    public static ItemLeyRune leyRune;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":debug_stick")
    public static DebugStick debugStick;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":throwing_card")
    public static ItemCard itemCard;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":time")
    public static ItemTime itemTime;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("vigilantEureka") {
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
        itemTime.initModel();
        bismite.initModel();
        leyKey.initModel();
        debugStick.initModel();
        itemCard.initModel();
    }
}
