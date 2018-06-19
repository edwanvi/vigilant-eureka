package me.itstheholyblack.vigilant_eureka.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySuspendedIce extends EntityFallingBlock implements IEntityAdditionalSpawnData {

    private IBlockState fallTile;

    public EntitySuspendedIce(World worldIn) {
        super(worldIn, 0, 0, 0, Blocks.FROSTED_ICE.getDefaultState());
    }

    public EntitySuspendedIce(World worldIn, double x, double y, double z, IBlockState fallingBlockState) {
        super(worldIn, x, y, z, fallingBlockState);
        this.fallTile = fallingBlockState;
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


    @Override
    public void writeSpawnData(ByteBuf buffer) {
        int state = 0;
        try {
            if (this.fallTile.equals(Blocks.ICE.getDefaultState())) {
                buffer.writeInt(state);
                return;
            } else if (this.fallTile.equals(Blocks.FROSTED_ICE.getDefaultState())) {
                state = 1;
            } else if (this.fallTile.equals(Blocks.PACKED_ICE)) {
                state = 2;
            } else if (this.fallTile.equals(Blocks.SNOW.getDefaultState())) {
                state = 3;
            }
        } catch (NullPointerException e) {
            state = 3;
        }
        buffer.writeInt(state);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        int state = additionalData.readInt();
        switch (state) {
            case 0:
                this.fallTile = Blocks.ICE.getDefaultState();
                break;
            case 1:
                this.fallTile = Blocks.FROSTED_ICE.getDefaultState();
                break;
            case 2:
                this.fallTile = Blocks.PACKED_ICE.getDefaultState();
                break;
            case 3:
                this.fallTile = Blocks.SNOW.getDefaultState();
                break;
            default:
                this.fallTile = Blocks.FROSTED_ICE.getDefaultState();
                break;
        }
    }
}
