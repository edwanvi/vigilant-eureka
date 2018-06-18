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
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandler {

    private double float_strength = 0.25;

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase e = event.getEntityLiving();
        if (e.isElytraFlying()) {
            BlockPos pos = e.getPosition();
            World worldIn = e.getEntityWorld();
            for (int i = 0; i < 10; i++) {
                Block scanned = worldIn.getBlockState(pos.down(i)).getBlock();
                if (scanned.equals(Blocks.FIRE) || scanned.equals(Blocks.LAVA) || scanned.equals(Blocks.FLOWING_LAVA)) {
                    // boost player, using code stolen from Mojang
                    Vec3d vec3d = e.getLookVec();
                    e.motionX += vec3d.x * 0.1 + (vec3d.x * 1.5 - e.motionX) * 0.2D;
                    e.motionZ += vec3d.z * 0.1 + (vec3d.z * 1.5 - e.motionZ) * 0.2D;
                    // A graph of this function is available at https://www.desmos.com/calculator/ss7gkav3cb
                    // where i is the x axis.
                    e.addVelocity(0, i > 0 ? -0.07 * i + 0.6 : 0.07, 0);
                    break;
                } else if (!scanned.equals(Blocks.AIR)) {
                    break;
                }
            }
        }
        NBTTagCompound entityData = e.getEntityData();
        if (entityData.getBoolean("inPoly")) {
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

        if (e instanceof EntityPlayer) {
            NBTTagCompound playerData = me.itstheholyblack.vigilant_eureka.util.NBTUtil.getPlayerPersist((EntityPlayer) e);
            if (playerData.getBoolean("metaphysical_high_ground")) {
                e.setInvisible(true); // simply doing e.setInvisible(playerData.getBoolean("metaphysical_high_ground")) causes invis pots to fail
            }
        }
    }

    @SubscribeEvent
    public void armorChanged(LivingEquipmentChangeEvent event) {
        ItemStack oldStack = event.getFrom();
        EntityEquipmentSlot slot = event.getSlot();
        if (slot.equals(EntityEquipmentSlot.HEAD)) {
            if (oldStack.getItem().equals(ModItems.invisCap)) {
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
                if (!event.getWorldObj().isRemote && (Math.abs(minecart.motionX) + Math.abs(minecart.motionY) + Math.abs(minecart.motionZ) != 0)) {
                    dismounting.addVelocity(minecart.motionX, Math.abs(minecart.motionY) + 5, minecart.motionZ);
                    EntityPlayerMP player = (EntityPlayerMP) dismounting;
                    player.setElytraFlying();
                }
            }
        }
    }

    @SubscribeEvent
    public void useItem(PlayerInteractEvent event) {
        if (!event.getWorld().isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            if (event.getItemStack().getItem().equals(Items.FIREWORKS) && player.isRiding() && player.getRidingEntity() instanceof EntityMinecart) {
                if (!player.capabilities.isCreativeMode) {
                    event.getItemStack().shrink(1);
                }
                Vec3d v = player.getLookVec();
                player.getRidingEntity().addVelocity(v.x * 500, v.y * 5, v.z * 500);
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    // loot tables
    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent evt) {
        String prefix = "minecraft:chests/";
        String name = evt.getName().toString();

        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            if (file.equals("stronghold_library") || file.equals("simple_dungeon")) {
                System.out.println("Injecting...");
                evt.getTable().addPool(getInjectPool("simple_dungeon"));
            }
        }
    }

    private LootPool getInjectPool(String entryName) {
        return new LootPool(new LootEntry[]{getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "eureka_inject_pool");
    }

    private LootEntryTable getInjectEntry(String name, int weight) {
        return new LootEntryTable(new ResourceLocation(Reference.MOD_ID, "inject/" + name), weight, 0, new LootCondition[0], "eureka_inject_entry");
    }

    private static void removeLeyNBT(EntityLivingBase e) {
        e.getEntityData().removeTag("timeSince");
        e.getEntityData().removeTag("inPoly");
        e.getEntityData().setBoolean("floated", false);
    }
}
