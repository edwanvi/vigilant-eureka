package me.itstheholyblack.vigilant_eureka.core;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.client.Keybinds;
import me.itstheholyblack.vigilant_eureka.network.PacketHandler;
import me.itstheholyblack.vigilant_eureka.network.PacketSendKey;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class InputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keybinds.warpKey.isPressed()) {
            // Someone pressed our tutorialKey. We send a message
            PacketHandler.INSTANCE.sendToServer(new PacketSendKey());
        }
    }
}
