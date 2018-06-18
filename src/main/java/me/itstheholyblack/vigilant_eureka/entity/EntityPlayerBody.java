package me.itstheholyblack.vigilant_eureka.entity;

import com.google.common.base.Optional;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import me.itstheholyblack.vigilant_eureka.util.NBTUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class EntityPlayerBody extends EntityLiving {

    private static final DataParameter<String> PLAYER_USERNAME = EntityDataManager.createKey(EntityPlayerBody.class, DataSerializers.STRING);
    private static final DataParameter<Optional<UUID>> PLAYER_UUID = EntityDataManager.createKey(EntityPlayerBody.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Boolean> SMALL_ARMS = EntityDataManager.createKey(EntityPlayerBody.class, DataSerializers.BOOLEAN);
    private static final ITextComponent INTERACT_MESSAGE = new TextComponentTranslation("message.body").setStyle(new Style().setColor(TextFormatting.GOLD));

    public EntityPlayerBody(World worldIn) {
        super(worldIn);
        setSize(1.8F, 0.5F);
        setHealth(20);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(PLAYER_USERNAME, "");
        dataManager.register(SMALL_ARMS, false);
        dataManager.register(PLAYER_UUID, Optional.absent());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("playerName", dataManager.get(PLAYER_USERNAME));
        if (dataManager.get(PLAYER_UUID).isPresent()) {
            compound.setUniqueId("playerId", dataManager.get(PLAYER_UUID).get());
        }
        compound.setBoolean("smallArms", dataManager.get(SMALL_ARMS));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setPlayerName(compound.getString("playerName"));
        setSmallArms(compound.getBoolean("smallArms"));
        setPlayerId(compound.getUniqueId("playerId"));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.limbSwingAmount = 0.0F;
        EntityPlayer p = this.world.getPlayerEntityByUUID(this.getPlayerId());
        if (p != null && !world.isRemote) {
            // i'm messing with the scoreboard and there's nothing you can do to stop me
            if (!this.isOnSameTeam(p)) {
                if (this.getTeam() != null) {
                    world.getScoreboard().removePlayerFromTeam(this.getCachedUniqueIdString(), (ScorePlayerTeam) this.getTeam());
                }
                world.getScoreboard().addPlayerToTeam(this.getCachedUniqueIdString(), p.getTeam().getName());
            }
            // "sync" potions
            Collection<PotionEffect> playerEffects = p.getActivePotionEffects();
            Collection<PotionEffect> bodyEffects = this.getActivePotionEffects();
            for (PotionEffect effect : bodyEffects) {
                if (!playerEffects.contains(effect)) {
                    this.removeActivePotionEffect(effect.getPotion());
                    this.markPotionsDirty();
                }
            }
            for (PotionEffect effect : playerEffects) {
                if (!this.isPotionActive(effect.getPotion())) {
                    this.addPotionEffect(effect);
                }
            }
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.getName().equals(this.getPlayerName())) {
            if (!player.getHeldItem(hand).isEmpty() && player.getUniqueID().equals(this.getPlayerId())) {
                player.sendStatusMessage(INTERACT_MESSAGE, true);
            } else if (player.getUniqueID().equals(this.getPlayerId())) {
                this.attackEntityFrom(DamageSource.OUT_OF_WORLD, this.getHealth());
            }
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        EntityPlayer p = this.world.getPlayerEntityByName(this.getPlayerName());
        return (p != null && !source.equals(DamageSource.OUT_OF_WORLD)) ? super.attackEntityFrom(source, amount) && p.attackEntityFrom(source, amount) : super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        EntityPlayer p = this.world.getPlayerEntityByUUID(this.getPlayerId());
        if (p != null) {
            NBTUtil.getPlayerPersist(p).setBoolean("metaphysical_high_ground", false);
            if (!p.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().equals(ModItems.invisCap)) {
                p.setInvisible(false);
            }
            if (!cause.equals(DamageSource.OUT_OF_WORLD)) {
                p.attackEntityFrom(cause, p.getHealth());
            } else {
                p.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            }
        }
        super.onDeath(cause);
    }

    @Nullable
    public UUID getPlayerId() {
        return dataManager.get(PLAYER_UUID).isPresent() ? dataManager.get(PLAYER_UUID).get() : null;
    }

    public void setPlayerId(UUID id) {
        dataManager.set(PLAYER_UUID, Optional.of(id));
    }

    public String getPlayerName() {
        return dataManager.get(PLAYER_USERNAME);
    }

    public void setPlayerName(String name) {
        if (name.endsWith("s")) {
            this.setCustomNameTag(name + "' body");
        } else {
            this.setCustomNameTag(name + "'s body");
        }
        dataManager.set(PLAYER_USERNAME, name);
    }

    public boolean smallArms() {
        return dataManager.get(SMALL_ARMS);
    }

    public void setSmallArms(boolean smallArms) {
        dataManager.set(SMALL_ARMS, smallArms);
    }
}
