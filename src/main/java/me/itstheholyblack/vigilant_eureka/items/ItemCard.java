package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.entity.EntityCard;
import me.itstheholyblack.vigilant_eureka.util.ArrayUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCard extends Item {

    private static Item[] COLD_THINGS;

    public ItemCard() {
        setRegistryName(Reference.MOD_ID, "throwing_card");
        setUnlocalizedName(Reference.MOD_ID + ".throwing_card");
        setCreativeTab(ModItems.CREATIVE_TAB);
        COLD_THINGS = new Item[]{
                Items.SNOWBALL,
                Item.getItemFromBlock(Blocks.ICE),
                Item.getItemFromBlock(Blocks.FROSTED_ICE),
                Item.getItemFromBlock(Blocks.PACKED_ICE)
        };
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!playerIn.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        if (!worldIn.isRemote) {
            System.out.println("spawning card");
            EntityCard c = new EntityCard(worldIn, playerIn, playerIn.getLook(1).x, playerIn.getLook(1).y, playerIn.getLook(1).z);
            c.setType(EntityCard.TYPES.BLAND);
            c.posY += playerIn.getEyeHeight();
            worldIn.spawnEntity(c);

            ItemStack other;
            if (handIn.equals(EnumHand.MAIN_HAND)) {
                other = playerIn.getHeldItem(EnumHand.OFF_HAND);
            } else {
                other = playerIn.getHeldItem(EnumHand.MAIN_HAND);
            }

            Item otherItem = other.getItem();
            if (otherItem.equals(Items.ENDER_EYE) || otherItem.equals(Items.ENDER_PEARL)) {
                c.setType(EntityCard.TYPES.ENDERIC);
            } else if (otherItem.equals(Item.getItemFromBlock(Blocks.TNT)) || otherItem.equals(Items.FIREWORKS)) {
                c.setType(EntityCard.TYPES.EXPLOSION);
            } else if (otherItem instanceof ItemSword) {
                c.setType(EntityCard.TYPES.SHARP);
            } else if (otherItem.equals(Items.FIRE_CHARGE) || otherItem.equals(Items.FLINT_AND_STEEL)) {
                c.setType(EntityCard.TYPES.HOT);
            } else if (ArrayUtil.contains(COLD_THINGS, otherItem)) {
                c.setType(EntityCard.TYPES.COLD);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
