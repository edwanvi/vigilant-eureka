package me.itstheholyblack.vigilant_eureka.blocks.tiles;

import me.itstheholyblack.vigilant_eureka.blocks.ModBlocks;
import me.itstheholyblack.vigilant_eureka.core.EnumLeyTypes;
import me.itstheholyblack.vigilant_eureka.core.Polygon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LeyLineTile extends TileEntity implements ITickable {

    public float ticks = 0;

    private BlockPos link_out;
    private ArrayList<BlockPos> polygon;
    private Polygon specialPoly;
    private int delayCounter = 10;
    private List<Entity> lastList;
    private boolean isLead;
    public EnumLeyTypes type;

    public LeyLineTile() {
        super();
        this.link_out = new BlockPos(BlockPos.ORIGIN); // this will be very quickly changed
        this.polygon = new ArrayList<>();
        this.type = EnumLeyTypes.FLOATER;
    }

    public EnumLinkResults addLinkOut(BlockPos bp) {
        if (bp.equals(this.getPos())) {
            return EnumLinkResults.SELFLINK;
        }
        if (!this.link_out.equals(bp)) {
            LeyLineTile otherLine = (LeyLineTile) this.world.getTileEntity(bp);
            if (!otherLine.link_out.equals(this.getPos())) {
                System.out.println("Link succeeded!");
                this.link_out = bp;
                markDirty();
                return EnumLinkResults.SUCCEED;
            } else {
                System.out.println("Link failed - two way connection");
                return EnumLinkResults.TWOWAY;
            }
        } else {
            System.out.println("Link failed - double linkage");
            return EnumLinkResults.DOUBLELINK;
        }
    }

    public BlockPos getLinkOut() {
        return this.link_out;
    }

    public void setPolygon(ArrayList<BlockPos> p) {
        this.polygon = p;
        Polygon.Builder fancyPolyBuilder = Polygon.builder();
        for (BlockPos pos : p) {
            fancyPolyBuilder.add(pos.getX(), pos.getZ());
        }
        specialPoly = fancyPolyBuilder.build();
        markDirty();
    }

    public boolean isLead() {
        return isLead;
    }

    public void setLead(boolean lead) {
        this.isLead = lead;
    }

    @Deprecated
    public int numberOut() {
        return 1;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.getString("type").equals("")) {
            type = EnumLeyTypes.valueOf(compound.getString("type"));
        } else {
            type = EnumLeyTypes.FLOATER;
        }
        if (compound.getBoolean("isLinkedOut")) {
            this.link_out = NBTUtil.getPosFromTag(compound.getCompoundTag("link_out"));
        }
        if (compound.hasKey("poly")) {
            NBTTagList n = (NBTTagList) compound.getTag("poly");
            ArrayList<BlockPos> newPoly = new ArrayList<>();
            for (NBTBase tag : n) {
                newPoly.add(NBTUtil.getPosFromTag((NBTTagCompound) tag));
            }
            setPolygon(newPoly);
        }
        isLead = compound.getBoolean("lead");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!link_out.equals(BlockPos.ORIGIN)) {
            compound.setBoolean("isLinkedOut", true);
            NBTTagCompound n = NBTUtil.createPosTag(link_out);
            compound.setTag("link_out", n);
        } else {
            compound.setBoolean("isLinkedOut", false);
        }
        if (polygon.size() > 0) {
            NBTTagList n = new NBTTagList();
            for (BlockPos p : polygon) {
                n.appendTag(NBTUtil.createPosTag(p));
            }
            compound.setTag("poly", n);
        }
        compound.setBoolean("lead", isLead);
        compound.setString("type", type.name());
        return compound;
    }

    public boolean isLinked() {
        return !this.link_out.equals(BlockPos.ORIGIN) && !this.link_out.equals(BlockPos.ORIGIN.down());
    }

    @Override
    public void update() {
        ticks = ticks + 0.1F;

        if (this.link_out.equals(BlockPos.ORIGIN.down())) {
            this.link_out = BlockPos.ORIGIN;
            markDirty();
        }

        if (!this.world.isRemote && this.isLinked() && this.world.isAreaLoaded(this.getPos(), 16)) {
            if (!this.getWorld().getBlockState(link_out).getBlock().equals(ModBlocks.leyLine)) {
                link_out = BlockPos.ORIGIN.down();
                markDirty();
                return;
            } else {
                LeyLineTile te = (LeyLineTile) this.world.getTileEntity(link_out);
                if (te.getLinkOut().equals(BlockPos.ORIGIN.down())) {
                    System.out.println("Linked to BROKEN link node " + this.link_out);
                    this.link_out = BlockPos.ORIGIN.down();
                    markDirty();
                    return;
                }
            }
        }

        if (isLead) {
            delayCounter--;
            if (delayCounter <= 0 || lastList == null) {
                delayCounter = 10;
                lastList = specialPoly.getEntities(this.world);
                if (lastList.size() > 0) {
                    for (Entity e : lastList) {
                        NBTTagCompound compound = e.getEntityData();
                        if (!(compound.getBoolean("inPoly")) && e instanceof EntityPlayer) {
                            ((EntityPlayer) e).sendStatusMessage(new TextComponentTranslation("message.enter_ley")
                                    .setStyle(new Style()
                                            .setItalic(true)
                                            .setColor(TextFormatting.AQUA)
                                    ), true);
                        }
                        compound.setBoolean("inPoly", specialPoly.contains(e));
                        compound.setTag("masterPos", NBTUtil.createPosTag(this.pos));
                        NBTTagList typesList = compound.getTagList("leyTypes", 8);
                        typesList.appendTag(new NBTTagString(this.type.toString()));
                    }
                }
            }
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        // getUpdateTag() is called whenever the chunkdata is sent to the
        // client. In contrast getUpdatePacket() is called when the tile entity
        // itself wants to sync to the client. In many cases you want to send
        // over the same information in getUpdateTag() as in getUpdatePacket().
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        // Prepare a packet for syncing our TE to the client. Since we only have to sync the stack
        // and that's all we have we just write our entire NBT here. If you have a complex
        // tile entity that doesn't need to have all information on the client you can write
        // a more optimal NBT here.
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        // Here we get the packet from the server and read it into our client side tile entity
        this.readFromNBT(packet.getNbtCompound());
    }

    public Polygon getSpecialPoly() {
        return specialPoly;
    }

    public EnumLeyTypes getType() {
        return type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public enum EnumLinkResults {
        SUCCEED, SELFLINK, DOUBLELINK, TWOWAY;
    }

}
