package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.MovingCastleDoorTile;
import me.itstheholyblack.vigilant_eureka.core.CustomTeleporter;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.util.FullPosition;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class MovingCastleDoor extends BlockTileEntity<MovingCastleDoorTile> {

    public static final PropertyBool IS_TOP = PropertyBool.create("is_top");

    public MovingCastleDoor() {
        super(Material.WOOD, "movingdoor");
        setUnlocalizedName(Reference.MOD_ID + ".movingdoor");
        setRegistryName("movingdoor");
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    }

    @Nullable
    @Override
    public MovingCastleDoorTile createTileEntity(World world, IBlockState state) {
        if (state.getValue(IS_TOP)) {
            MovingCastleDoorTile t = new MovingCastleDoorTile();
            t.setDestination(BlockPos.ORIGIN, 0);
            return t;
        } else {
            return null;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        if (world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)
                && !world.getBlockState(pos.down()).getBlock().equals(ModBlocks.movingdoor)) {
            world.setBlockState(pos, state.withProperty(IS_TOP, false));
            world.setBlockState(pos.up(), state.withProperty(IS_TOP, true));
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.destroyBlock(pos, false);
        }
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().equals(ModBlocks.movingdoor)
                || worldIn.getBlockState(pos.down()).getBlock().equals(ModBlocks.movingdoor);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote && state.getValue(IS_TOP)) {
            MovingCastleDoorTile t;
            if (worldIn.isAreaLoaded(pos, 16)) {
                t = this.getTileEntity(worldIn, pos);
            } else {
                return;
            }
            FullPosition destination = t.getDestination();
            if (!t.getDestination().equals(BlockPos.ORIGIN)) {
                if (entityIn.dimension != destination.getDimension()) {
                    if (entityIn instanceof EntityPlayer) {
                        CustomTeleporter.teleportToDimension((EntityPlayer) entityIn, destination.getDimension(),
                                destination.getX(), destination.getY(), destination.getZ());
                    } else {
                        entityIn.setPosition(destination.getX(), destination.getY(), destination.getZ());
                        entityIn.changeDimension(destination.getDimension());
                        entityIn.setPositionAndUpdate(destination.getX(), destination.getY(), destination.getZ());
                    }
                } else {
                    entityIn.setPositionAndUpdate(destination.getX(), destination.getY(), destination.getZ());
                }
            }
        }
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess access, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public Class<MovingCastleDoorTile> getTileEntityClass() {
        return MovingCastleDoorTile.class;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_TOP);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(IS_TOP, meta == 1);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(IS_TOP) ? 1 : 0;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("mouseovertext.dim_door"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
