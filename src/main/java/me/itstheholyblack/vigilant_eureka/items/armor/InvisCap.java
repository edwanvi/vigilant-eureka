package me.itstheholyblack.vigilant_eureka.items.armor;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InvisCap extends ItemArmor {
    public InvisCap() {
        super(ArmorTypes.BUGGER_ME_MAT, 0, EntityEquipmentSlot.HEAD);
        setUnlocalizedName(Reference.MOD_ID + "." + "invis_cap");
        setRegistryName("invis_cap");
        setCreativeTab(ModItems.CREATIVETAB);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
