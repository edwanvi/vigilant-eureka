package me.itstheholyblack.vigilant_eureka.items.armor;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.util.RayTraceHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WarpBoots extends ItemArmor {
    public WarpBoots() {
        super(ArmorMaterial.CHAIN, 0, EntityEquipmentSlot.FEET);
        setUnlocalizedName(Reference.MOD_ID + "." + "warp_boots");
        setRegistryName("warp_boots");
        setCreativeTab(ModItems.CREATIVETAB);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && player.isSprinting()) {
            BlockPos look;
            try {
                look = RayTraceHelper.tracePath(world, player, 25, 1, null).getBlockPos();
            } catch (NullPointerException e) {
                look = player.getPosition();
            }
            if (look.equals(player.getPosition()) || player.getCooldownTracker().hasCooldown(this)) {
                return;
            } else {
                player.setSprinting(false);
                player.getCooldownTracker().setCooldown(this, 20);
                player.setPositionAndUpdate(look.getX(), look.up().getY(), look.getZ());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}