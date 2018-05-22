package me.itstheholyblack.vigilant_eureka.entity.render;

import me.itstheholyblack.vigilant_eureka.entity.EntityPlayerBody;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderEntityPlayerBody extends RenderBiped<EntityPlayerBody> {
    public RenderEntityPlayerBody(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPlayer(0.0F, false), 0);
    }

    @Override
    public void doRender(@Nonnull EntityPlayerBody dopple, double x, double y, double z, float yaw, float partialTicks) {
        this.mainModel = new ModelPlayer(0.0f, dopple.smallArms());
        super.doRender(dopple, x, y, z, yaw, partialTicks);
    }

    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityPlayerBody entity) {
        Minecraft mc = Minecraft.getMinecraft();

        if (!(mc.getRenderViewEntity() instanceof AbstractClientPlayer))
            return DefaultPlayerSkin.getDefaultSkinLegacy();

        return ((AbstractClientPlayer) mc.getRenderViewEntity()).getLocationSkin();
    }
}
