package me.itstheholyblack.vigilant_eureka.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static int packetId = 0;

    public static SimpleNetworkWrapper INSTANCE;

    public PacketHandler() {
    }

    private static int nextID() {
        return packetId++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(PacketSendWarp.Handler.class, PacketSendWarp.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketSendTime.Handler.class, PacketSendTime.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketSpawnBody.Handler.class, PacketSpawnBody.class, nextID(), Side.SERVER);

        INSTANCE.registerMessage(PacketEndericPoof.Handler.class, PacketEndericPoof.class, nextID(), Side.CLIENT);
    }
}
