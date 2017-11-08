package me.itstheholyblack.vigilant_eureka.core;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.client.Keybinds;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.network.PacketHandler;
import me.itstheholyblack.vigilant_eureka.network.PacketSendKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class InputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (Keybinds.warpKey.isPressed() && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem().equals(ModItems.warpBoots)) {
            PacketHandler.INSTANCE.sendToServer(new PacketSendKey());
        }
    }
}
