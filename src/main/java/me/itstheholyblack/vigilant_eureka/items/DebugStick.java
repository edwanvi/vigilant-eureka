package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.entity.EntityPlayerBody;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DebugStick extends Item {
    public DebugStick() {
        setRegistryName(Reference.MOD_ID, "debug_stick");
        setUnlocalizedName(Reference.MOD_ID + ".debug_stick");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Items.BLAZE_ROD.getRegistryName(), "inventory"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EntityPlayerBody e = new EntityPlayerBody(worldIn);
        e.setPosition(pos.getX(), pos.getY(), pos.getZ());
        e.setPlayer(playerIn.getGameProfile().getName());
        System.out.println(playerIn.getGameProfile().getName());
        worldIn.spawnEntity(e);
        return EnumActionResult.SUCCESS;
    }
}
