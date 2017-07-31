package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockLeyLine extends BlockTileEntity<LeyLineTile> {
    public BlockLeyLine() {
        super(Material.ROCK, "leyline");
        setUnlocalizedName(Reference.MOD_ID + ".leyline");
        setRegistryName("leyline");
        setCreativeTab(ModItems.CREATIVETAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (stack.getItem().equals(ModItems.dimKey)) {
                LeyLineTile t = this.getTileEntity(worldIn, pos);
                if (t == null) {
                    t = this.getTileEntity(worldIn, pos.up());
                }
                NBTTagCompound compound = stack.getTagCompound();
                if (compound != null && t != null && compound.getInteger("y") > 0) {
                    playerIn.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + "The key and the intersection arc together."), true);
                } else if (compound.getInteger("y") <= 0) {
                    playerIn.sendStatusMessage(new TextComponentString(TextFormatting.RED + "There's no interaction."), true);
                }
            }
        }
        return true;
    }

    @Override
    public Class<LeyLineTile> getTileEntityClass() {
        return LeyLineTile.class;
    }

    @Nullable
    @Override
    public LeyLineTile createTileEntity(World world, IBlockState state) {
        return new LeyLineTile();
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
