package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.core.NBTUtil;
import me.itstheholyblack.vigilant_eureka.core.PolyHelper;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
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
import java.util.ArrayList;

public class BlockLeyLine extends BlockTileEntity<LeyLineTile> {
    public BlockLeyLine() {
        super(Material.ROCK, "leyline");
        setUnlocalizedName(Reference.MOD_ID + ".leyline");
        setRegistryName("leyline");
        setCreativeTab(ModItems.CREATIVETAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItem().equals(ModItems.leyKey)) {
            NBTTagCompound comp = NBTUtil.getTagCompoundSafe(stack);
            if (comp.hasKey("tolink")) {
                LeyLineTile tile = this.getTileEntity(worldIn, pos);
                BlockPos p = net.minecraft.nbt.NBTUtil.getPosFromTag(comp.getCompoundTag("tolink"));
                LeyLineTile.EnumLinkResults results = tile.addLinkOut(p);
                if (results.equals(LeyLineTile.EnumLinkResults.SUCCEED)) {
                    comp.removeTag("tolink");
                    playerIn.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + I18n.format("message.ley_link_good")), true);
                } else if (results.equals(LeyLineTile.EnumLinkResults.DOUBLELINK)) {
                    playerIn.sendStatusMessage(new TextComponentString(TextFormatting.RED + I18n.format("message.ley_link_doublelink")), true);
                } else if (results.equals(LeyLineTile.EnumLinkResults.TWOWAY)) {
                    playerIn.sendStatusMessage(new TextComponentString(TextFormatting.RED + I18n.format("message.ley_link_twoway")), true);
                } else {
                    playerIn.sendStatusMessage(new TextComponentString(TextFormatting.RED + I18n.format("message.ley_link_selflink")), true);
                }
            } else {
                comp.setTag("tolink", net.minecraft.nbt.NBTUtil.createPosTag(pos));
            }
            return true;
        } else if (stack.getItem().equals(ModItems.dimKey)) {
            ArrayList<BlockPos> poly = PolyHelper.stackSolve((LeyLineTile) worldIn.getTileEntity(pos));
            if (poly != null) {
                LeyLineTile thisTile = (LeyLineTile) worldIn.getTileEntity(pos);
                boolean hasLeader = false;
                for (BlockPos p : poly) {
                    LeyLineTile te = (LeyLineTile) worldIn.getTileEntity(p);
                    te.setPolygon(poly);
                    if (!te.isLead()) {
                        continue;
                    } else {
                        hasLeader = true;
                    }
                }
                thisTile.setLead(!hasLeader);
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + I18n.format("message.ley_solve_good")), true);
            } else {
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.RED + I18n.format("message.ley_solve_incomplete")), true);
            }
        }
        return false;
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
