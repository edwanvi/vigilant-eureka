package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.JarTile;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockJar extends BlockTileEntity<JarTile> {

    public BlockJar() {
        super(Material.GLASS, "vexjar");
        setTranslationKey(Reference.MOD_ID + ".vexjar");
        setRegistryName("vexjar");
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    /**
     * @deprecated call via {@link IBlockState#getCollisionBoundingBox(IBlockAccess, BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);
    }

    /**
     * Return an AABB (in world coords!) that should be highlighted when the player is targeting this Block
     *
     * @deprecated call via {@link IBlockState#getSelectedBoundingBox(World, BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D).offset(pos);
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     *
     * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityVex && !worldIn.isRemote) {
            entityIn.attackEntityFrom(DamageSource.CRAMMING, ((EntityVex) entityIn).getHealth());
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof JarTile && entityIn.isDead) {
                ((JarTile) tile).addFill(1);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            int fill = tile instanceof JarTile ? ((JarTile) tile).getFillLevel() : 0;
            playerIn.sendStatusMessage(new TextComponentTranslation("message.fillPre").appendText(" " + fill), true);
        }
        return true;
    }

    @Override
    public Class<JarTile> getTileEntityClass() {
        return JarTile.class;
    }

    @Nullable
    @Override
    public JarTile createTileEntity(World world, IBlockState state) {
        return new JarTile();
    }
}
