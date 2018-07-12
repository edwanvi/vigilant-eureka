package me.itstheholyblack.vigilant_eureka.blocks.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class JarTile extends TileEntity implements ITickable {

    private int fillLevel;

    public JarTile() {
        super();
        this.fillLevel = -1;
    }

    @Override
    public void update() {
        BlockPos pos = this.getPos();
        List<EntityVex> vexes = this.world.getEntitiesWithinAABB(EntityVex.class, new AxisAlignedBB(
                        getPos().add(-10, -10, -10),
                        getPos().add(10, 10, 10)
                )
        );
        // how vexing!
        for (EntityVex vex : vexes) {
            // most of this was stolen from XP orbs
            double motionX = 0;
            double motionY = 0;
            double motionZ = 0;
            double d1 = ((pos.getX() + 0.5) - vex.posX) / 16.0D;
            double d2 = (pos.getY() - vex.posY) / 16.0D;
            double d3 = ((pos.getZ() + 0.5) - vex.posZ) / 16.0D;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0D - d4;

            if (d5 > 0.0D) {
                d5 = d5 * d5;
                motionX += d1 / d4 * d5 * 0.1D;
                motionY += d2 / d4 * d5 * 0.1D;
                motionZ += d3 / d4 * d5 * 0.1D;
            }

            double pull = (1 / getDistanceSq(vex.posX, vex.posY, vex.posZ) > 1 ? getDistanceSq(vex.posX, vex.posY, vex.posZ) : 1) * 10;
            vex.move(MoverType.SELF, motionX * pull, motionY * pull, motionZ * pull);
        }
    }

    public void addFill(int fill) {
        if (this.fillLevel + fill <= 64) {
            this.setFillLevel(this.fillLevel + fill);
        }
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    public int getFillLevel() {
        return fillLevel;
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
        // Prepare a packet for syncing our TE to the client.
        // If you have a complex tile entity that doesn't need to have all information on the client you can write a more optimal NBT here.
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        // Here we get the packet from the server and read it into our client side tile entity
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.fillLevel = compound.getInteger("fillLevel");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("fillLevel", this.fillLevel);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
