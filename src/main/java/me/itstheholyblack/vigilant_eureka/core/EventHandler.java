package me.itstheholyblack.vigilant_eureka.core;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandler {

    private final double float_strength = 0.25;

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
                    // boost player, using code stolen from Mojang
                    Vec3d vec3d = e.getLookVec();
                    double d0 = 1.5D;
                    double d1 = 0.1D;
                    e.motionX += vec3d.x * d1 + (vec3d.x * d0 - e.motionX) * 0.2D;
                    e.motionZ += vec3d.z * d1 + (vec3d.z * d0 - e.motionZ) * 0.2D;
                    double up_boost;
                    if (i > 0) {
                        // A graph of this function is available at https://www.desmos.com/calculator/ss7gkav3cb
                        // where i is the x axis.
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
        if (e.getEntityData().getBoolean("inPoly") && e.getEntityData().getInteger("timeSince") < 10) {
            // increment time (recalculate when > 10)
            e.getEntityData().setInteger("timeSince", e.getEntityData().getInteger("timeSince") + 1);
            NBTTagList leyTypes = e.getEntityData().getTagList("leyTypes", 8);
            ArrayList<EnumLeyTypes> enumTypes = new ArrayList<>();
            int i = 0;
            for (NBTBase n : leyTypes) {
                String str = leyTypes.getStringTagAt(i);
                try {
                    enumTypes.add(EnumLeyTypes.valueOf(str));
                } catch (IllegalArgumentException ex) {
                    // no type set or someone's fucking around
                    enumTypes.add(EnumLeyTypes.ACTUALLY_NOTHING);
                }
                i++;
            }
            for (EnumLeyTypes enumType : enumTypes) {
                switch (enumType) {
                    case FLOATER:
                        if (!e.getEntityData().getBoolean("floated")) {
                            e.getEntityData().setBoolean("floated", true);
                        }
                        break;
                    case CLEANSER:
                        e.clearActivePotions();
                        break;
                    case SPEED:
                        e.addPotionEffect(new PotionEffect(MobEffects.SPEED, 1000, 1, true, true));
                        break;
                    default:
                        break;
                }
            }
        } else if (e.getEntityData().getInteger("timeSince") >= 10) {
            e.getEntityData().removeTag("timeSince");
            e.getEntityData().removeTag("inPoly");
            e.getEntityData().setBoolean("floated", false);
        }
    }

    @SubscribeEvent
    public void armorChanged(LivingEquipmentChangeEvent event) {
        ItemStack oldStack = event.getFrom();
        ItemStack newStack = event.getTo();
        EntityEquipmentSlot slot = event.getSlot();
        if (slot.equals(EntityEquipmentSlot.HEAD)) {
            if (newStack.getItem().equals(ModItems.invisCap)) {
                // switch to the invis cap
                event.getEntityLiving().setInvisible(true);
            } else if (oldStack.getItem().equals(ModItems.invisCap)) {
                // switch off cap
                event.getEntityLiving().setInvisible(false);
            }
        }
    }

    @SubscribeEvent
    public void livingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity().getEntityData().getBoolean("floated")) {
            event.getEntity().addVelocity(0, float_strength, 0);
        }
    }
}
