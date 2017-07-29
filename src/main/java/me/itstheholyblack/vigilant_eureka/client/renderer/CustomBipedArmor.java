package me.itstheholyblack.vigilant_eureka.client.renderer;

import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Class to conditionally render armor, allowing full invisibility. Code concept by Paul Fulham, modified by Edwan Vi into a working state.
 *
 * @author Paul Fulham
 * @author Edwan Vi
 */
public class CustomBipedArmor implements LayerRenderer<EntityLivingBase> {

    private LayerBipedArmor layer;

    public CustomBipedArmor(LayerBipedArmor l) {
        this.layer = l;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float delta, float age, float yaw, float pitch, float scale) {
        if (!entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().equals(ModItems.invisCap)) {
            layer.doRenderLayer(entity, limbSwing, limbSwingAmount, delta, age, yaw, pitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return layer.shouldCombineTextures();
    }
}
