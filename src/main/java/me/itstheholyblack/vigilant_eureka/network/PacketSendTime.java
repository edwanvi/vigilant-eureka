package me.itstheholyblack.vigilant_eureka.network;

import baubles.api.BaublesApi;
import io.netty.buffer.ByteBuf;
import me.itstheholyblack.vigilant_eureka.core.ChunkRebuilder;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendTime implements IMessage {

    private BlockPos look;
    private BlockPos stand;

    @Override
    public void fromBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        look = BlockPos.fromLong(buf.readLong());
        stand = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        buf.writeLong(look.toLong());
        buf.writeLong(stand.toLong());
    }

    public PacketSendTime() {
        RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
        look = result.getBlockPos();
        stand = Minecraft.getMinecraft().player.getPosition();
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
            BlockPos pos = message.look;
            if (!playerEntity.getCooldownTracker().hasCooldown(ModItems.itemTime) && BaublesApi.isBaubleEquipped(playerEntity, ModItems.itemTime) != -1 && !world.isRemote && world instanceof WorldServer) {
                playerEntity.getCooldownTracker().setCooldown(ModItems.itemTime, 100);
                ChunkRebuilder.rebuildChunk(world, pos, message.stand);
            } else if (playerEntity.getCooldownTracker().hasCooldown(ModItems.itemTime)) {
                playerEntity.sendStatusMessage(new TextComponentTranslation("message.eye_already_open"), true);
            }
        }
    }
}
