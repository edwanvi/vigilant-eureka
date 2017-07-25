package me.itstheholyblack.vigilant_eureka.core;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase e = event.getEntityLiving();
        if (e.isElytraFlying()) { // OH YES
            BlockPos pos = e.getPosition();
            World worldIn = e.getEntityWorld();
            int i = 0;
            while (i <= 9) {
                BlockPos scanpos = pos.down(i);
                Block scanned = worldIn.getBlockState(scanpos).getBlock();
                if (scanned.equals(Blocks.FIRE) || scanned.equals(Blocks.LAVA) || scanned.equals(Blocks.FLOWING_LAVA)) {
                    FMLLog.log.debug("Boosting for heat.");
                    Vec3d vec3d = e.getLookVec();
                    double d0 = 1.5D;
                    double d1 = 0.1D;
                    e.motionX += vec3d.x * d1 + (vec3d.x * d0 - e.motionX) * 0.2D;
                    e.motionZ += vec3d.z * d1 + (vec3d.z * d0 - e.motionZ) * 0.2D;
                    double up_boost;
                    if (i > 0) {
                        up_boost = -0.07 * i + 0.6;
                    } else {
                        up_boost = 0.07;
                    }
                    if (up_boost > 0) {
                        e.addVelocity(0, up_boost, 0);
                    }
                    break;
                } else if (!scanned.equals(Blocks.AIR)) {
                    break;
                }
                i++;
            }
        }
    }
}