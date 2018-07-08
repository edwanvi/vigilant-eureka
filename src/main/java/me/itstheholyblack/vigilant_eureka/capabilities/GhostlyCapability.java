package me.itstheholyblack.vigilant_eureka.capabilities;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.network.PacketHandler;
import me.itstheholyblack.vigilant_eureka.network.PacketUpdateGhostly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GhostlyCapability {

    @CapabilityInject(GhostlyCapability.IGhostlyHandler.class)
    public static final Capability<GhostlyCapability.IGhostlyHandler> CAPABILITY_GHOST = null;

    public interface IGhostlyHandler {
        boolean isVisible();

        void setVisible(boolean visible);
    }

    public static class DefaultGhostly implements IGhostlyHandler {
        private boolean visible;

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    public static class Storage implements Capability.IStorage<IGhostlyHandler> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IGhostlyHandler> capability, IGhostlyHandler instance, EnumFacing side) {
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("vis", instance.isVisible());
            return tag;
        }

        @Override
        public void readNBT(Capability<IGhostlyHandler> capability, IGhostlyHandler instance, EnumFacing side, NBTBase nbt) {
            instance.setVisible(((NBTTagCompound) nbt).getBoolean("vis"));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        IGhostlyHandler instance = CAPABILITY_GHOST.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY_GHOST;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? CAPABILITY_GHOST.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) CAPABILITY_GHOST.getStorage().writeNBT(CAPABILITY_GHOST, instance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            CAPABILITY_GHOST.getStorage().readNBT(CAPABILITY_GHOST, instance, null, nbt);
        }
    }

    @Nullable
    public static IGhostlyHandler getHandler(Entity entity) {
        if (entity.hasCapability(CAPABILITY_GHOST, EnumFacing.DOWN)) {
            return entity.getCapability(CAPABILITY_GHOST, EnumFacing.DOWN);
        }
        return null;
    }

    public static void causeSync(IGhostlyHandler handler, EntityPlayer entity) {
        PacketUpdateGhostly message = new PacketUpdateGhostly(handler.isVisible(), entity.getUUID(entity.getGameProfile()));
        PacketHandler.INSTANCE.sendToAllTracking(message, entity);
        if (entity instanceof EntityPlayerMP) {
            PacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP) entity);
        }
        Reference.LOGGER.log(Level.INFO, !handler.isVisible() + ", " + entity.world.isRemote);
        entity.setInvisible(!handler.isVisible());
    }
}
