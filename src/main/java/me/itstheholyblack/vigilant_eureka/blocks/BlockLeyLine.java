package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.core.EnumLeyTypes;
import me.itstheholyblack.vigilant_eureka.core.PolyHelper;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.util.NBTUtil;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockLeyLine extends BlockTileEntity<LeyLineTile> {

    private static final ITextComponent IT_WORKED = new TextComponentTranslation("message.ley_link_good").setStyle(new Style().setColor(TextFormatting.GREEN));
    private static final ITextComponent ALREADY_EXISTS = new TextComponentTranslation("message.ley_link_doublelink").setStyle(new Style().setColor(TextFormatting.RED));
    private static final ITextComponent TWO_WAY = new TextComponentTranslation("message.ley_link_twoway").setStyle(new Style().setColor(TextFormatting.RED));
    private static final ITextComponent SELF_LINK = new TextComponentTranslation("message.ley_link_selflink").setStyle(new Style().setColor(TextFormatting.RED));

    private static final ITextComponent LEY_SOLVE_GOOD = new TextComponentTranslation("message.ley_solve_good").setStyle(new Style().setColor(TextFormatting.GREEN));
    private static final ITextComponent LEY_SOLVE_INCOMPLETE = new TextComponentTranslation("message.ley_solve_incomplete").setStyle(new Style().setColor(TextFormatting.RED));

    public BlockLeyLine() {
        super(Material.ROCK, "leyline");
        this.setHardness(Float.POSITIVE_INFINITY);
        setUnlocalizedName(Reference.MOD_ID + ".leyline");
        setRegistryName("leyline");
        setCreativeTab(ModItems.CREATIVE_TAB);
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
                switch (results) {
                    case SUCCEED:
                        comp.removeTag("tolink");
                        playerIn.sendStatusMessage(IT_WORKED, true);
                        return true;
                    case DOUBLELINK:
                        playerIn.sendStatusMessage(ALREADY_EXISTS, true);
                        return false;
                    case TWOWAY:
                        playerIn.sendStatusMessage(TWO_WAY, true);
                        return false;
                    case SELFLINK:
                        playerIn.sendStatusMessage(SELF_LINK, true);
                        return false;
                }
            } else {
                comp.setTag("tolink", net.minecraft.nbt.NBTUtil.createPosTag(pos));
                return true;
            }
        } else if (stack.getItem().equals(ModItems.dimKey)) {
            ArrayList<BlockPos> poly = PolyHelper.stackSolve((LeyLineTile) worldIn.getTileEntity(pos));
            if (poly != null) {
                LeyLineTile thisTile = (LeyLineTile) worldIn.getTileEntity(pos);
                boolean hasLeader = false;
                for (BlockPos p : poly) {
                    LeyLineTile te = (LeyLineTile) worldIn.getTileEntity(p);
                    te.setPolygon(poly);
                    if (te.isLead()) {
                        hasLeader = true;
                    }
                }
                thisTile.setLead(!hasLeader);
                playerIn.sendStatusMessage(LEY_SOLVE_GOOD, true);
                return true;
            } else {
                playerIn.sendStatusMessage(LEY_SOLVE_INCOMPLETE, true);
                return false;
            }
        } else if (stack.getItem().equals(ModItems.leyRune)) {
            LeyLineTile thisTile = getTileEntity(worldIn, pos);
            thisTile.type = EnumLeyTypes.valueOf(NBTUtil.getTagCompoundSafe(stack).getString("type"));
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
