package me.itstheholyblack.vigilant_eureka.core;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandler {

    private double float_strength = 0.25;

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
        if (e.getEntityData().getBoolean("inPoly")) {
            NBTTagCompound entityData = e.getEntityData();
            BlockPos pos = NBTUtil.getPosFromTag(entityData.getCompoundTag("masterPos"));
            // test if entity still in polygon
            TileEntity tile = e.getEntityWorld().getTileEntity(pos);
            if (tile instanceof LeyLineTile) {
                Polygon polygon = ((LeyLineTile) tile).getSpecialPoly();
                if (polygon.contains(e)) {
                    // add effects
                    EnumLeyTypes enumType = ((LeyLineTile) tile).getType();
                    switch (enumType) {
                        case FLOATER:
                            if (!entityData.getBoolean("floated")) {
                                entityData.setBoolean("floated", true);
                            }
                            break;
                        case CLEANSER:
                            e.clearActivePotions();
                            break;
                        case SPEED:
                            e.addVelocity(e.motionX * 0.5, 0, e.motionZ * 0.5);
                            break;
                        default:
                            break;
                    }
                } else {
                    // exit and remove entity data
                    removeLeyNBT(e);
                }
            }
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

    @SubscribeEvent
    public void minecartTakeoff(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof EntityMinecart && event.getEntityMounting() instanceof EntityPlayer) {
            EntityMinecart minecart = (EntityMinecart) event.getEntityBeingMounted();
            EntityPlayer dismounting = (EntityPlayer) event.getEntityMounting();
            ItemStack chestpiece = dismounting.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (chestpiece.getItem().equals(Items.ELYTRA) && ItemElytra.isUsable(chestpiece)) {
                if (!event.getWorldObj().isRemote && (minecart.motionX != 0 && minecart.motionY != 0 && minecart.motionZ != 0)) {
                    dismounting.addVelocity(minecart.motionX, Math.abs(minecart.motionY), minecart.motionZ);
                    EntityPlayerMP player = (EntityPlayerMP) dismounting;
                    player.setElytraFlying();
                }
            }
        }
    }

    private static void removeLeyNBT(EntityLivingBase e) {
        e.getEntityData().removeTag("timeSince");
        e.getEntityData().removeTag("inPoly");
        e.getEntityData().setBoolean("floated", false);
    }
}
