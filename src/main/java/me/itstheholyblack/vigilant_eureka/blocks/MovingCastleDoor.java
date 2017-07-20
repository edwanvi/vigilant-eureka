package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.MovingCastleDoorTile;
import me.itstheholyblack.vigilant_eureka.core.CustomTeleporter;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.util.FullPosition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MovingCastleDoor extends BlockBreakable implements ITileEntityProvider {

    public static final PropertyBool IS_TOP = PropertyBool.create("is_top");

    public MovingCastleDoor() {
        super(Material.PORTAL, false);
        setUnlocalizedName(Reference.MOD_ID + ".movingdoor");
        setRegistryName("movingdoor");
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        MovingCastleDoorTile t = new MovingCastleDoorTile();
        t.setDestination(BlockPos.ORIGIN, 0);
        return t;
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
        if (worldIn.getBlockState(pos.up()).getBlock().equals(ModBlocks.movingdoor)
                || worldIn.getBlockState(pos.down()).getBlock().equals(ModBlocks.movingdoor)) {
            return true;
        }
        return worldIn.getBlockState(pos.up()).getBlock().equals(ModBlocks.movingdoor)
                || worldIn.getBlockState(pos.down()).getBlock().equals(ModBlocks.movingdoor);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {
            MovingCastleDoorTile t = (MovingCastleDoorTile) worldIn.getTileEntity(pos);
            if (t.getDestination().getY() > 0 && entityIn instanceof EntityPlayer) {
                // NBTTagCompound compound = t.getTileData();
                FullPosition destination = t.getDestination();
                if (entityIn.dimension != destination.getDimension()) {
                    CustomTeleporter.teleportToDimension((EntityPlayer) entityIn, destination.getDimension(),
                            destination.getX(), destination.getY(), destination.getZ());
                } else {
                    entityIn.setPositionAndUpdate(destination.getX(), destination.getY(), destination.getZ());
                }
            } else {
                System.out.println(t.getDestination().getY());
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (stack.getItem().equals(ModItems.firstItem)) {
                MovingCastleDoorTile t = (MovingCastleDoorTile) worldIn.getTileEntity(pos);
                NBTTagCompound compound = stack.getTagCompound();
                if (compound != null) {
                    t.setDestination(
                            new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z")),
                            stack.getTagCompound().getInteger("dim"));
                }
            }
            return true;
        }

    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_TOP);
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
}
