package me.itstheholyblack.vigilant_eureka.entity.render;

import me.itstheholyblack.vigilant_eureka.entity.EntityCard;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderEntityCard extends RenderEntity {
    public static final Factory FACTORY = new Factory();
    private RenderItem itemRenderer;

    public RenderEntityCard(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (this.itemRenderer == null) {
            this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.disableLighting();
        GlStateManager.rotate(0, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(entity.rotationPitch, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.rotationYaw, 0, 0, 1);
        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();

        this.itemRenderer.renderItem(new ItemStack(ModItems.itemCard, 1), ItemCameraTransforms.TransformType.FIXED);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static class Factory implements IRenderFactory<EntityCard> {
        @Override
        public Render<? super EntityCard> createRenderFor(RenderManager manager) {
            return new RenderEntityCard(manager);
        }
    }
}
