package me.itstheholyblack.vigilant_eureka.entity;

import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityCard extends EntityThrowable {

    private EntityLivingBase thrower;

    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    private TYPES type;

    public EntityCard(World world) {
        this(world, null, 0, 0, 0);
    }

    public EntityCard(World world, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(world);
        setSize(0.5F, 0.1F);
        this.thrower = shooter;
        if (shooter != null) {
            this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch); // sets pos and heading, not motion
            this.setPosition(this.posX, this.posY, this.posZ); // possibly redundant, sets position again(?)
        }
        double d0 = (double) MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.5D;
        this.accelerationY = accelY / d0 * 0.5D;
        this.accelerationZ = accelZ / d0 * 0.5D;
        this.type = TYPES.BLAND;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted >= 20 * 5) { // 20 per second times five seconds, have the compiler do the math
            RayTraceResult r = new RayTraceResult(
                    RayTraceResult.Type.MISS,
                    new Vec3d(this.posX, this.posY, this.posZ),
                    EnumFacing.UP,
                    new BlockPos(this.posX, this.posY, this.posZ)
            );
            doImpact(r, false);
        }
        // update position based on velocity
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = 0.95F;
        // update velocity based on accel
        this.motionX += this.accelerationX;
        this.motionY += this.accelerationY;
        this.motionZ += this.accelerationZ;
        this.motionX *= (double) f;
        this.motionY *= (double) f;
        this.motionZ *= (double) f;
        // finalize position update
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
            if (!result.entityHit.equals(thrower)) {
                // do stuff
                if (this.posY >= result.entityHit.getPositionEyes(1).y - 0.4
                        && this.posY <= result.entityHit.getPositionEyes(1).y + 0.4) {
                    doImpact(result, true);
                } else {
                    doImpact(result, false);
                }
            }
        } else {
            doImpact(result, false);
        }
    }

    private void doImpact(RayTraceResult r, boolean amped) {
        switch (this.type) {
            case BLAND:
                if (!this.world.isRemote) {
                    EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(ModItems.itemCard, 1));
                    this.world.spawnEntity(entityItem);
                }
                break;
            case EXPLOSION:
                this.world.newExplosion(this, this.posX, this.posY, this.posZ, amped ? 3.0F : 1.5F, amped, true);
                break;
            case ENDERIC:
                if (!(this.thrower == null)) {
                    this.thrower.setPositionAndUpdate(r.hitVec.x, r.hitVec.y + 1, r.hitVec.z);
                    EnderTeleportEvent enderTeleportEvent = new EnderTeleportEvent(
                            thrower,
                            r.hitVec.x,
                            r.hitVec.y + 1,
                            r.hitVec.z,
                            0.0F);
                    MinecraftForge.EVENT_BUS.post(enderTeleportEvent);
                }
                break;
            case HOT:
                if (r.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
                    r.entityHit.setFire(amped ? 8 : 4);
                } else {
                    BlockPos pos = r.getBlockPos();
                    this.world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                }
                break;
        }
        this.setDead();
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
    }

    public TYPES getType() {
        return type;
    }

    public void setType(TYPES type) {
        this.type = type;
    }

    public enum TYPES {
        BLAND, SHARP, COLD, HOT, EXPLOSION, ENDERIC
    }
}
