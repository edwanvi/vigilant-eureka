package me.itstheholyblack.vigilant_eureka.network;

import io.netty.buffer.ByteBuf;
import me.itstheholyblack.vigilant_eureka.entity.EntityPlayerBody;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSpawnBody implements IMessage {

    boolean smallArms;

    @SideOnly(Side.CLIENT)
    public PacketSpawnBody() {
        smallArms = !(Minecraft.getMinecraft().player).getSkinType().equals("default");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        smallArms = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(smallArms);
    }

    public static class Handler implements IMessageHandler<PacketSpawnBody, IMessage> {
        @Override
        public IMessage onMessage(PacketSpawnBody message, MessageContext ctx) {
            // Always use a construct like this to actually handle your message. This ensures that
            // your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
            // is called on the networking thread so it is not safe to do a lot of things
            // here.
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSpawnBody message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();

            EntityPlayerBody e = new EntityPlayerBody(world);
            e.setPosition(playerEntity.posX, playerEntity.posY + 1, playerEntity.posZ);
            e.rotationYaw = (float) (180.0F * world.rand.nextDouble());
            e.renderYawOffset = e.rotationYaw;

            e.setSmallArms(message.smallArms);
            e.setPlayerName(playerEntity.getName());
            e.setPlayerId(EntityPlayer.getUUID(playerEntity.getGameProfile()));

            world.spawnEntity(e);
        }
    }
}
