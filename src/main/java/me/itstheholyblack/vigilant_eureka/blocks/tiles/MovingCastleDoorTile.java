package me.itstheholyblack.vigilant_eureka.blocks.tiles;

import me.itstheholyblack.vigilant_eureka.util.FullPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class MovingCastleDoorTile extends TileEntity {

    private FullPosition dest_pos;

    public void setDestination(FullPosition position) {
        new Exception().printStackTrace(System.out);
        this.dest_pos = position;
        System.out.println("setting dest to " + position.getX() + " " + position.getY() + " " + position.getZ());
        try {
            TileEntity otherTile = this.world.getTileEntity(this.getPos().up());
            if (otherTile instanceof MovingCastleDoorTile) {
                ((MovingCastleDoorTile) otherTile).setDestination_NoOther(this.dest_pos);
            } else if (this.world.getTileEntity(this.getPos().down()) instanceof MovingCastleDoorTile) {
                otherTile = this.world.getTileEntity(this.getPos().down());
                ((MovingCastleDoorTile) otherTile).setDestination_NoOther(this.dest_pos);
            } else {
            }
        } catch (NullPointerException e) {
            System.out.println("Other tile was null!");
            // no-op, already set destination
        }
        System.out.println("Final destination is " + position.getX() + " " + position.getY() + " " + position.getZ());
        markDirty();
    }

    public void setDestination(BlockPos pos, int dimension) {
        this.setDestination(new FullPosition(pos, dimension));
        markDirty();
    }

    private void setDestination_NoOther(FullPosition pos) {
        this.dest_pos = pos;
        markDirty();
    }

    public FullPosition getDestination() {
        return this.dest_pos;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        double pos_x = compound.getInteger("x");
        double pos_y = compound.getInteger("y");
        double pos_z = compound.getInteger("z");
        int dim = compound.getInteger("dim");
        FullPosition destination = new FullPosition(pos_x, pos_y, pos_z, dim);
        this.setDestination(destination);
        System.out.println("read " + dest_pos.getX() + " " + dest_pos.getY() + " " + dest_pos.getZ() + " from NBT");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        System.out.println("writing " + dest_pos.getX() + " " + dest_pos.getY() + " " + dest_pos.getZ() + " to NBT");
        compound.setInteger("x", this.dest_pos.getX());
        compound.setInteger("y", this.dest_pos.getY());
        compound.setInteger("z", this.dest_pos.getZ());
        compound.setInteger("dim", this.dest_pos.getDimension());
        return compound;
    }
}
