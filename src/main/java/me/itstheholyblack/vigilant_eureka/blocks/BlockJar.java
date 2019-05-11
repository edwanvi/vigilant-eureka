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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockJar extends BlockTileEntity<JarTile> {

    public BlockJar() {
        super(Material.GLASS, "vexjar");
        setTranslationKey(Reference.MOD_ID + ".vexjar");
        setRegistryName("vexjar");
        setCreativeTab(ModItems.CREATIVE_TAB);
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
