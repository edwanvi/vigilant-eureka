package me.itstheholyblack.vigilant_eureka.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityCard extends Entity {

    private EntityLivingBase thrower;

    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    public EntityCard(World world) {
        this(world, null, 0, 0, 0);
    }

    public EntityCard(World world, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(world);
        setSize(1.0F, 1.0F);
        this.thrower = shooter;
        if (shooter != null) {
            this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch); // sets pos and heading, not motion
            this.setPosition(this.posX, this.posY, this.posZ); // possibly redundant, sets position again(?)
        }
        double d0 = (double) MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.5D;
        this.accelerationY = accelY / d0 * 0.5D;
        this.accelerationZ = accelZ / d0 * 0.5D;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // update position based on velocity
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = 0.95F;
        // update velocity based on accel
        this.motionX += this.accelerationX;
        this.motionY += this.accelerationY;
        this.motionZ += this.accelerationZ;
        this.motionX *= (double)f;
        this.motionY *= (double)f;
        this.motionZ *= (double)f;
        // finalize position update
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    public enum TYPES {
        BLAND, SHARP, COLD, HOT, EXPLOSION, ENDERIC;
    }
}
