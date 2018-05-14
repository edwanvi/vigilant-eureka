package me.itstheholyblack.vigilant_eureka.network;

import baubles.api.BaublesApi;
import io.netty.buffer.ByteBuf;
import me.itstheholyblack.vigilant_eureka.core.ChunkRebuilder;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendTime implements IMessage {

    private BlockPos blockPos;

    @Override
    public void fromBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        blockPos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        buf.writeLong(blockPos.toLong());
    }

    public PacketSendTime() {
        RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
        blockPos = result.getBlockPos();
    }

    public static class Handler implements IMessageHandler<PacketSendTime, IMessage> {
        @Override
        public IMessage onMessage(PacketSendTime message, MessageContext ctx) {
            // Always use a construct like this to actually handle your message. This ensures that
            // your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
            // is called on the networking thread so it is not safe to do a lot of things
            // here.
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendTime message, MessageContext ctx) {
            // This code is run on the server side. So you can do server-side calculations here
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            BlockPos pos = message.blockPos;
            if (BaublesApi.isBaubleEquipped(playerEntity, ModItems.itemTime) != -1 && !world.isRemote && world instanceof WorldServer) {
                ChunkRebuilder.rebuildChunk(world, pos);
            }
        }
    }
}
