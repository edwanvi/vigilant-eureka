package me.itstheholyblack.vigilant_eureka.capabilities;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static me.itstheholyblack.vigilant_eureka.capabilities.GhostlyCapability.getHandler;

// sounds like a snake
public class CapAdder {
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "ghostly_cap"), new GhostlyCapability.Provider());
        }
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) {
        final GhostlyCapability.IGhostlyHandler original = getHandler(event.getOriginal());
        final GhostlyCapability.IGhostlyHandler clone = getHandler(event.getEntity());
        clone.setVisible(original.isVisible());
    }
}
