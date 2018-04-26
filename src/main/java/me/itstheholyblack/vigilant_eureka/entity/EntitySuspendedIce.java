package me.itstheholyblack.vigilant_eureka.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class EntitySuspendedIce extends EntityFallingBlock {

    public EntitySuspendedIce(World worldIn) {
        super(worldIn, 0, 0, 0, Blocks.FROSTED_ICE.getDefaultState());
    }

    public EntitySuspendedIce(World worldIn, double x, double y, double z, IBlockState fallingBlockState) {
        super(worldIn, x, y, z, fallingBlockState);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 20) {
            this.setNoGravity(false);
            this.addVelocity(0, -0.5, 0);
            this.setHurtEntities(true);
        }
    }
}
