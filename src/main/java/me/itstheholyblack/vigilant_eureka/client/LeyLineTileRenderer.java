package me.itstheholyblack.vigilant_eureka.client;

import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class LeyLineTileRenderer extends TileEntitySpecialRenderer<LeyLineTile> {

    @Override
    public void render(LeyLineTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // BEGIN ENDERDRAGON CODE
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderHelper.disableStandardItemLighting();
        float the_ticker = (float) ((100) * Math.cos(te.ticks + partialTicks / 10) + 100);
        float f = (the_ticker) / 200.0F;
        float f1 = 0.0F;

        int red = 0;
        int blue = 0;
        int green = 0;
        if (te.isLead()) {
            green = 255;
        } else if (te.getLinkOut().equals(BlockPos.ORIGIN.down())) {
            red = 0;
            blue = 255;
            green = 0;
        } else {
            red = 255;
            blue = 255;
        }

        if (f > 0.8F) {
            f1 = (f - 0.8F) / 0.2F;
        }

        Random random = new Random(432L);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5);
        GlStateManager.scale(0.1, 0.1, 0.1);

        for (int i = 0; (float) i < (f + f * f) / 2.0F * 60.0F; ++i) {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
            float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
            float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (255.0F * (1.0F - f1))).endVertex();
            bufferbuilder.pos(-0.866D * (double) f3, (double) f2, (double) (-0.5F * f3)).color(red, green, blue, 0).endVertex();
            bufferbuilder.pos(0.866D * (double) f3, (double) f2, (double) (-0.5F * f3)).color(red, green, blue, 0).endVertex();
            bufferbuilder.pos(0.0D, (double) f2, (double) (1.0F * f3)).color(red, green, blue, 0).endVertex();
            bufferbuilder.pos(-0.866D * (double) f3, (double) f2, (double) (-0.5F * f3)).color(red, green, blue, 0).endVertex();
            tessellator.draw();
        }

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        // END ENDERDRAGON CODE


        BlockPos pos = te.getLinkOut();
        if (!pos.equals(BlockPos.ORIGIN) && !pos.equals(BlockPos.ORIGIN.down())) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            bufferbuilder.pos(0.5, 0.375, 0.5).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(0.5, 0.625, 0.5).color(255, 255, 255, 255).endVertex();

            bufferbuilder.pos(
                    (pos.getX() - te.getPos().getX()) + 0.5,
                    (pos.getY() - te.getPos().getY()) + 0.375,
                    (pos.getZ() - te.getPos().getZ()) + 0.5)
                    .color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(
                    (pos.getX() - te.getPos().getX()) + 0.5,
                    (pos.getY() - te.getPos().getY()) + 0.625,
                    (pos.getZ() - te.getPos().getZ()) + 0.5)
                    .color(255, 255, 255, 255).endVertex();
            tessellator.draw();

            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
        }
    }

    @Override
    public boolean isGlobalRenderer(LeyLineTile te) {
        return true;
    }
}
