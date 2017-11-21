package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.entity.EntityCard;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCard extends Item {

    private static final double SPEED = 2;

    public ItemCard() {
        setRegistryName(Reference.MOD_ID, "throwing_card");
        setUnlocalizedName(Reference.MOD_ID + ".throwing_card");
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!playerIn.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        if (!worldIn.isRemote) {
            EntityCard c = new EntityCard(worldIn, playerIn, playerIn.getLook(1).x, playerIn.getLook(1).y, playerIn.getLook(1).z);
            c.posY += playerIn.getEyeHeight();
            worldIn.spawnEntity(c);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
