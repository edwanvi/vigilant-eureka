package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.entity.EntityCard;
import me.itstheholyblack.vigilant_eureka.util.NBTUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;

public class ItemCard extends Item {
    private static HashMap<Item, EntityCard.TYPES> TYPE_MAP = new HashMap<>();

    public ItemCard() {
        setRegistryName(Reference.MOD_ID, "throwing_card");
        setUnlocalizedName(Reference.MOD_ID + ".throwing_card");
        setMaxStackSize(128);
        setCreativeTab(ModItems.CREATIVE_TAB);

        for (Item i : new Item[]{Items.SNOWBALL, Item.getItemFromBlock(Blocks.SNOW), Item.getItemFromBlock(Blocks.SNOW_LAYER), Item.getItemFromBlock(Blocks.ICE), Item.getItemFromBlock(Blocks.PACKED_ICE)}) {
            TYPE_MAP.put(i, EntityCard.TYPES.COLD);
        }

        for (Item i : new Item[]{Items.TNT_MINECART, Items.FIREWORKS, Items.FIREWORK_CHARGE, Item.getItemFromBlock(Blocks.TNT)}) {
            TYPE_MAP.put(i, EntityCard.TYPES.EXPLOSION);
        }

        for (Item i : new Item[]{Items.BLAZE_ROD, Items.BLAZE_POWDER, Items.FIRE_CHARGE, Items.FLINT_AND_STEEL}) {
            TYPE_MAP.put(i, EntityCard.TYPES.HOT);
        }

        for (Item i : new Item[]{
                Items.ENDER_EYE,
                Items.ENDER_PEARL,
                Items.END_CRYSTAL,
                Item.getItemFromBlock(Blocks.END_STONE),
                Item.getItemFromBlock(Blocks.END_BRICKS),
                Item.getItemFromBlock(Blocks.END_ROD),
                Item.getItemFromBlock(Blocks.ENDER_CHEST),
                Item.getItemFromBlock(Blocks.PURPUR_BLOCK), Item.getItemFromBlock(Blocks.PURPUR_SLAB), Item.getItemFromBlock(Blocks.PURPUR_PILLAR), Item.getItemFromBlock(Blocks.PURPUR_STAIRS),
                Items.CHORUS_FRUIT,
                Items.CHORUS_FRUIT_POPPED,
                Item.getItemFromBlock(Blocks.CHORUS_FLOWER), Item.getItemFromBlock(Blocks.CHORUS_PLANT)
        }) {
            TYPE_MAP.put(i, EntityCard.TYPES.ENDERIC);
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!playerIn.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        if (!worldIn.isRemote) {
            EntityCard c = new EntityCard(worldIn, playerIn, playerIn.getLook(1).x, playerIn.getLook(1).y, playerIn.getLook(1).z);
            c.setType(EntityCard.TYPES.BLAND);
            c.posY += playerIn.getEyeHeight();
            c.setPositionAndRotation(c.posX, c.posY, c.posZ, playerIn.cameraYaw, playerIn.cameraPitch);
            worldIn.spawnEntity(c);

            ItemStack other;
            if (handIn.equals(EnumHand.MAIN_HAND)) {
                other = playerIn.getHeldItem(EnumHand.OFF_HAND);
            } else {
                other = playerIn.getHeldItem(EnumHand.MAIN_HAND);
            }

            Item otherItem = other.getItem();
            c.setType(getTypeFromItem(otherItem));
            if (otherItem.equals(ModItems.dimKey)) {
                NBTTagCompound keyComp = NBTUtil.getTagCompoundSafe(other);
                NBTTagCompound cardComp = c.getEntityData();
                cardComp.setInteger("p_x", keyComp.getInteger("x"));
                cardComp.setInteger("p_y", keyComp.getInteger("y"));
                cardComp.setInteger("p_z", keyComp.getInteger("z"));
                cardComp.setInteger("dim", keyComp.getInteger("dim"));
            } else if (otherItem instanceof ItemSword) {
                c.setType(EntityCard.TYPES.SHARP);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    private static EntityCard.TYPES getTypeFromItem(Item item) {
        return TYPE_MAP.get(item) == null ? EntityCard.TYPES.BLAND : TYPE_MAP.get(item);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("mouseovertext.throwing_card"));
    }
}
