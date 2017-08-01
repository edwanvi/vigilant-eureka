package me.itstheholyblack.vigilant_eureka.blocks.tiles;

import me.itstheholyblack.vigilant_eureka.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class LeyLineTile extends TileEntity implements ITickable {

    public float ticks = 0;

    private ArrayList<BlockPos> links_out;
    private ArrayList<BlockPos> links_in;

    public LeyLineTile() {
        super();
        this.links_out = new ArrayList<>();
        this.links_in = new ArrayList<>();
    }

    public EnumLinkResults addLinkOut(BlockPos bp) {
        if (!this.links_out.contains(bp)) {
            LeyLineTile otherLine = (LeyLineTile) this.world.getTileEntity(bp);
            if (!otherLine.links_out.contains(this.getPos())) {
                System.out.println("Link succeeded!");
                this.links_out.add(bp);
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

    public BlockPos getLinkOutAtIndex(int i) {
        return this.links_out.get(i);
    }

    public boolean addLinkIn(BlockPos bp) {
        if (!this.links_in.contains(bp)) {
            this.links_in.add(bp);
            markDirty();
            return true;
        } else {
            return false;
        }
    }

    public int numberOut() {
        return this.links_out.size();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.getBoolean("isLinkedOut")) {
            System.out.println("Loading oubound links.");
            NBTTagList n = (NBTTagList) compound.getTag("links_out");
            int i = 0;
            for (NBTBase tag : n) {
                this.links_out.add(NBTUtil.getPosFromTag((NBTTagCompound) tag));
                i++;
            }
            System.out.println("Loaded " + Integer.toString(i) + " links.");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (links_out != null && links_out.size() > 0) {
            compound.setBoolean("isLinkedOut", true);
            NBTTagList n = new NBTTagList();
            for (BlockPos p : links_out) {
                n.appendTag(NBTUtil.createPosTag(p));
            }
            compound.setTag("links_out", n);
        }
        if (links_in != null && links_in.size() > 0) {
            compound.setBoolean("isLinkedIn", true);
            NBTTagList n = new NBTTagList();
            for (BlockPos p : links_in) {
                n.appendTag(NBTUtil.createPosTag(p));
            }
            compound.setTag("links_in", n);
        }
        if (links_out == null && links_in == null) {
            compound.setBoolean("isLinked", false);
        }
        return compound;
    }

    @Override
    public void update() {
        ticks = ticks + 0.1F;
        if (!this.world.isRemote && this.links_out.size() > 0 && this.world.isAreaLoaded(this.getPos(), 16)) {
            try {
                for (BlockPos p : links_out) {
                    if (!this.getWorld().getBlockState(p).getBlock().equals(ModBlocks.leyLine)) {
                        links_out.remove(p);
                        markDirty();
                    }
                }
            } catch (ConcurrentModificationException e) {
                System.out.println("oh bother");
            }
        }
    }

    @Override
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

    public enum EnumLinkResults {
        SUCCEED, SELFLINK, DOUBLELINK, TWOWAY;
    }
}
