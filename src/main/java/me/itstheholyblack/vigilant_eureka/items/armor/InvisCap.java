package me.itstheholyblack.vigilant_eureka.items.armor;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class InvisCap extends ItemArmor {
    public InvisCap() {
        super(ArmorTypes.BUGGER_ME_MAT, 0, EntityEquipmentSlot.HEAD);
        setUnlocalizedName(Reference.MOD_ID + "." + "invis_cap");
        setRegistryName("invis_cap");
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("mouseovertext.invis_cap"));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        player.setInvisible(true);
    }
}
