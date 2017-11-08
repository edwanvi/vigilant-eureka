package me.itstheholyblack.vigilant_eureka.blocks.tiles;

import me.itstheholyblack.vigilant_eureka.util.FullPosition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MovingCastleDoorTile extends TileEntity {

    private FullPosition dest_pos;

    public void setDestination(FullPosition position) {
        // new Exception().printStackTrace(System.out);
        this.dest_pos = position;
        markDirty();
    }

    public void setDestination(BlockPos pos, int dimension) {
        this.setDestination(new FullPosition(pos, dimension));
        markDirty();
    }

    public FullPosition getDestination() {
        return this.dest_pos;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        double pos_x = compound.getInteger("destination_x");
        double pos_y = compound.getInteger("destination_y");
        double pos_z = compound.getInteger("destination_z");
        int dim = compound.getInteger("destination_dim");
        FullPosition destination = new FullPosition(pos_x, pos_y, pos_z, dim);
        this.setDestination(destination);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("destination_x", this.dest_pos.getX());
        compound.setInteger("destination_y", this.dest_pos.getY());
        compound.setInteger("destination_z", this.dest_pos.getZ());
        compound.setInteger("destination_dim", this.dest_pos.getDimension());
        return compound;
    }
}
