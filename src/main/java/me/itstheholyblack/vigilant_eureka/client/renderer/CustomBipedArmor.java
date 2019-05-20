package me.itstheholyblack.vigilant_eureka.client.renderer;

import me.itstheholyblack.vigilant_eureka.capabilities.GhostlyCapability;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Class to conditionally render armor, allowing full invisibility. Code concept by Paul Fulham, modified by Edwan Vi into a working state.
 *
 * @author Paul Fulham
 * @author Edwan Vi
 */
@SideOnly(Side.CLIENT)
public class CustomBipedArmor<T extends LayerRenderer<EntityLivingBase>> implements LayerRenderer<EntityLivingBase> {

    private T layer;

    public CustomBipedArmor(T l) {
        this.layer = l;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float delta, float age, float yaw, float pitch, float scale) {
        if (GhostlyCapability.getHandler(entity).isVisible()) {
            layer.doRenderLayer(entity, limbSwing, limbSwingAmount, delta, age, yaw, pitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return layer.shouldCombineTextures();
    }
}
