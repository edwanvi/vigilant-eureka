package me.itstheholyblack.vigilant_eureka.util;

import net.minecraft.util.math.BlockPos;

public class FullPosition extends BlockPos {

    private int dimension;

    public FullPosition(double x, double y, double z, int dimension) {
        super(x, y, z);
        this.setDimension(dimension);
    }

    public FullPosition(BlockPos pos, int dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), dimension);
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
