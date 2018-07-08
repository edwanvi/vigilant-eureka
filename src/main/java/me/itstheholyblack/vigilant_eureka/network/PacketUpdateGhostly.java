package me.itstheholyblack.vigilant_eureka.network;

import io.netty.buffer.ByteBuf;
import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.capabilities.GhostlyCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketUpdateGhostly implements IMessage {

    boolean visible;
    UUID playerId;

    public PacketUpdateGhostly() {
    }

    public PacketUpdateGhostly(boolean v, UUID id) {
        visible = v;
        playerId = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        visible = buf.readBoolean();
        playerId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(visible);
        buf.writeLong(playerId.getMostSignificantBits());
        buf.writeLong(playerId.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<PacketUpdateGhostly, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateGhostly message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        void handle(PacketUpdateGhostly message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.playerId);
            Reference.LOGGER.debug(player.getName());
            GhostlyCapability.IGhostlyHandler handler = GhostlyCapability.getHandler(player);
            handler.setVisible(message.visible);
        }
    }
}
