package me.itstheholyblack.vigilant_eureka.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityPlayerBody extends EntityLiving {

    private static final DataParameter<String> PLAYER_NAME = EntityDataManager.createKey(EntityPlayerBody.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> SMALL_ARMS = EntityDataManager.createKey(EntityPlayerBody.class, DataSerializers.BOOLEAN);

    public EntityPlayerBody(World worldIn) {
        super(worldIn);
        setSize(0.6F, 1.8F);
        setHealth(20);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(PLAYER_NAME, "");
        dataManager.register(SMALL_ARMS, false);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("playerName", dataManager.get(PLAYER_NAME));
        compound.setBoolean("smallArms", dataManager.get(SMALL_ARMS));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setPlayer(compound.getString("playerName"));
        setSmallArms(compound.getBoolean("smallArms"));
    }

    public String getPlayer() {
        return dataManager.get(PLAYER_NAME);
    }

    public void setPlayer(String player) {
        dataManager.set(PLAYER_NAME, player);
    }

    public boolean smallArms() {
        return dataManager.get(SMALL_ARMS);
    }

    public void setSmallArms(boolean smallArms) {
        dataManager.set(SMALL_ARMS, smallArms);
    }
}
