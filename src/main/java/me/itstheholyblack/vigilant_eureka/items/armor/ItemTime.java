package me.itstheholyblack.vigilant_eureka.items.armor;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTime extends Item implements IBauble, IRenderBauble {
    public ItemTime() {
        setRegistryName(Reference.MOD_ID, "time");
        setUnlocalizedName(Reference.MOD_ID + ".time");
        this.setMaxStackSize(1);
        this.setCreativeTab(ModItems.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, RenderType renderType, float v) {
        if (renderType.equals(RenderType.BODY)) {
            Helper.rotateIfSneaking(entityPlayer);
            Helper.translateToChest();
            Helper.defaultTransforms();
            scale(1.25F);
            GlStateManager.translate(0F, -0.2F, 0F);
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(this), ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }
    }

    private static void scale(float f) {
        GlStateManager.scale(f, f, f);
    }
}
