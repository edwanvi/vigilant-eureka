package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.util.NBTUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemLeyKey extends Item {
    public ItemLeyKey() {
        setRegistryName(Reference.MOD_ID, "ley_key");
        setUnlocalizedName(Reference.MOD_ID + ".ley_key");
        setMaxStackSize(1); // category of one
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            NBTUtil.getTagCompoundSafe(stack).removeTag("tolink");
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            BlockPos p = net.minecraft.nbt.NBTUtil.getPosFromTag(compound.getCompoundTag("tolink"));
            int x;
            int y;
            int z;
            x = p.getX();
            y = p.getY();
            z = p.getZ();
            // (0, 0, 0) isn't reachable without breaking bedrock
            if (y == 0) {
                tooltip.add(I18n.format("mouseovertext.ley_key"));
            } else {
                String fulltip = I18n.format("mouseovertext.ley_key") + "\nX: " + Integer.toString(x) + "\nY: "
                        + Integer.toString(y) + "\nZ: " + Integer.toString(z);
                tooltip.add(fulltip);
            }
        } else {
            tooltip.add(I18n.format("mouseovertext.ley_key"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return NBTUtil.getTagCompoundSafe(stack).hasKey("tolink");
    }
}
