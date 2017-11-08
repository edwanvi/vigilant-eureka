package me.itstheholyblack.vigilant_eureka.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEndericPoof implements IMessage {

    private BlockPos pos;
    private static double[] positionList_x = new double[24];
    private static double[] positionList_y = new double[24];

    public PacketEndericPoof(BlockPos p) {
        pos = p;
    }

    public PacketEndericPoof() { // why do you need this
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<PacketEndericPoof, IMessage> {

        @Override
        public IMessage onMessage(PacketEndericPoof message, MessageContext ctx) {
            if (PacketEndericPoof.positionList_x[0] != 1) {
                // fill lists
                for (int i = 0; i < 24; i++) {
                    PacketEndericPoof.positionList_x[i] = Math.cos((15 * i) * (Math.PI / 180));
                }

                for (int i = 0; i < 24; i++) {
                    PacketEndericPoof.positionList_y[i] = Math.sin((15 * i) * (Math.PI / 180));
                }
            }
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        public void handle(PacketEndericPoof message, MessageContext ctx) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            double r = 5.0D;
            if (player.world.isRemote) {
                for (int i = 0; i < 24; i++) {
                    // System.out.println("(X, Y): " + x + ", " + y);
                    for (int j = 0; j < 100; j++) {
                        double yMod = +player.getRNG().nextDouble() * (double) player.height - 0.25D;
                        double yParticle = message.pos.getY() - 1.5 + yMod;
                        r = 10 * (-Math.pow(yMod - 1, 2) + 2);
                        double x = PacketEndericPoof.positionList_x[i] * r;
                        double y = PacketEndericPoof.positionList_y[i] * r;
                        player.world.spawnParticle(
                                EnumParticleTypes.PORTAL,
                                message.pos.getX() + x,
                                yParticle,
                                message.pos.getZ() + y,
                                -x, player.getRNG().nextDouble(), -y
                        );
                    }

                }
            }
        }
    }
}
