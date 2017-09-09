package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.ModBlocks;
import me.itstheholyblack.vigilant_eureka.blocks.MovingCastleDoor;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.MovingCastleDoorTile;
import me.itstheholyblack.vigilant_eureka.core.NBTUtil;
import me.itstheholyblack.vigilant_eureka.util.RayTraceHelper;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class DimKey extends Item {

    public DimKey() {
        setRegistryName(Reference.MOD_ID, "dim_key");
        setUnlocalizedName(Reference.MOD_ID + ".dim_key");
        setMaxStackSize(1); // category of one
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!world.isRemote) {
            NBTTagCompound tag = NBTUtil.getTagCompoundSafe(stack);
            BlockPos look;
            try {
                look = RayTraceHelper.tracePath(world, playerIn, 3, 1, null).getBlockPos();
            } catch (NullPointerException e) {
                look = playerIn.getPosition();
            }
            if (!world.getBlockState(look).getBlock().equals(ModBlocks.movingdoor)) {
                int x = playerIn.getPosition().getX();
                int y = playerIn.getPosition().getY();
                int z = playerIn.getPosition().getZ();
                int dim = playerIn.dimension;
                tag.setInteger("x", x);
                tag.setInteger("y", y);
                tag.setInteger("z", z);
                tag.setInteger("dim", dim);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        NBTTagCompound tag = NBTUtil.getTagCompoundSafe(stack);
        System.out.println(worldIn.getBlockState(pos).getBlock());

        if (worldIn.getBlockState(pos).getBlock().equals(ModBlocks.movingdoor)) {
            System.out.println("clicked on door");
            MovingCastleDoorTile t;
            if (worldIn.getBlockState(pos).getValue(MovingCastleDoor.IS_TOP)) {
                t = (MovingCastleDoorTile) worldIn.getTileEntity(pos);
            } else {
                t = (MovingCastleDoorTile) worldIn.getTileEntity(pos.up());
            }
            if (tag != null && t != null && tag.getInteger("y") > 0) {
                t.setDestination(
                        new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")),
                        stack.getTagCompound().getInteger("dim"));
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + "Click."), true);
                return EnumActionResult.SUCCESS;
            } else if (tag.getInteger("y") <= 0) {
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.RED + "It won't turn."), true);
                return EnumActionResult.FAIL;
            }
        }

        System.out.println("Did not click door, was " + worldIn.getBlockState(pos).getBlock() + " instead.");
        int x = playerIn.getPosition().getX();
        int y = playerIn.getPosition().getY();
        int z = playerIn.getPosition().getZ();
        int dim = playerIn.dimension;
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        tag.setInteger("dim", dim);
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            int x = compound.getInteger("x");
            int y = compound.getInteger("y");
            int z = compound.getInteger("z");
            int dim = compound.getInteger("dim");
            // (0, 0, 0) isn't reachable without breaking bedrock
            if (x == 0 && y == 0 && z == 0) {
                tooltip.add(I18n.format("mouseovertext.dim_key"));
            } else {
                String dim_name = DimensionType.getById(dim).getName();
                String fulltip = I18n.format("mouseovertext.dim_key") + "\nX: " + Integer.toString(x) + "\nY: "
                        + Integer.toString(y) + "\nZ: " + Integer.toString(z) + "\nDimension: " + dim_name;
                tooltip.add(fulltip);
            }
        } else {
            tooltip.add(I18n.format("mouseovertext.dim_key"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation normalModel = new ModelResourceLocation(getRegistryName() + "_normal", "inventory");
        ModelResourceLocation activeModel = new ModelResourceLocation(getRegistryName() + "_active", "inventory");

        ModelBakery.registerItemVariants(this, normalModel, activeModel);

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                int y = NBTUtil.getTagCompoundSafe(stack).getInteger("y");
                if (y > 0) {
                    return activeModel;
                } else {
                    return normalModel;
                }
            }
        });
    }
}
