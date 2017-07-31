package me.itstheholyblack.vigilant_eureka.blocks.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class LeyLineTile extends TileEntity implements ITickable {

    private UUID name;
    public float ticks = 0;

    // private ArrayList<BlockPos> links;

    public LeyLineTile() {
        super();
        this.name = UUID.randomUUID();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.name = compound.getUniqueId("name");
        // this.links = compound.getByteArray("links").toString();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setUniqueId("name", this.name);
        return compound;
    }

    @Override
    public void update() {
        ticks = ticks + 0.1F;
    }
}
