package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.core.EnumLeyTypes;
import me.itstheholyblack.vigilant_eureka.core.NBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemLeyRune extends Item {

    public ItemLeyRune() {
        setRegistryName(Reference.MOD_ID, "ley_rune");
        setUnlocalizedName(Reference.MOD_ID + ".ley_rune");
        setMaxStackSize(1); // category of one
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        NBTTagCompound compound = NBTUtil.getTagCompoundSafe(stack);
        compound.setString("type", "SPEED");
        String type = compound.getString("type");
        System.out.println(EnumLeyTypes.valueOf(type));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
