package me.itstheholyblack.vigilant_eureka.items.armor;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.util.RayTraceHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WarpFeets extends ItemArmor {
    public WarpFeets() {
        super(ArmorMaterial.CHAIN, 0, EntityEquipmentSlot.FEET);
        setUnlocalizedName(Reference.MOD_ID + "." + "warp_feets");
        setRegistryName("warp_feets");
        setCreativeTab(ModItems.CREATIVETAB);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (player.isSprinting()) {
            BlockPos look;
            try {
                look = RayTraceHelper.tracePath(world, player, 10, 1, null).getBlockPos();
            } catch (NullPointerException e) {
                look = player.getPosition();
            }
            if (look.equals(player.getPosition())) {
                return;
            } else {
                player.setSprinting(false);
                player.setPositionAndUpdate(look.getX(), look.up().getY(), look.getZ());
            }
        }
    }
}