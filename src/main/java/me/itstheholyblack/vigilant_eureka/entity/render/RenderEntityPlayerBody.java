package me.itstheholyblack.vigilant_eureka.entity.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.itstheholyblack.vigilant_eureka.entity.EntityPlayerBody;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class RenderEntityPlayerBody extends RenderBiped<EntityPlayerBody> {
    private static final ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation TEXTURE_ALEX = new ResourceLocation("textures/entity/alex.png");

    public RenderEntityPlayerBody(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPlayer(0.0F, false), 0);
    }

    @Override
    public void doRender(@Nonnull EntityPlayerBody dopple, double x, double y, double z, float yaw, float partialTicks) {
        this.mainModel = new ModelPlayer(0.0f, dopple.smallArms());
        super.doRender(dopple, x, y, z, yaw, partialTicks);
    }

    @Override
    protected void applyRotations(EntityPlayerBody entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        GlStateManager.rotate(180.0F - rotationYaw, 0, 1, 0);
        GlStateManager.translate(0, 0.24, 0);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
    }

    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityPlayerBody entity) {
        GameProfile profile = new GameProfile(null, entity.getPlayer());
        Minecraft minecraft = Minecraft.getMinecraft();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            return minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        } else {
            return entity.smallArms() ? TEXTURE_ALEX : TEXTURE_STEVE;
        }
    }
}
