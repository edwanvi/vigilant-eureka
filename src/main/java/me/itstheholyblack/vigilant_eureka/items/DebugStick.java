package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.capabilities.GhostlyCapability;
import me.itstheholyblack.vigilant_eureka.network.PacketHandler;
import me.itstheholyblack.vigilant_eureka.network.PacketSpawnBody;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DebugStick extends Item {
    public DebugStick() {
        setRegistryName(Reference.MOD_ID, "debug_stick");
        setTranslationKey(Reference.MOD_ID + ".debug_stick");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Items.BLAZE_ROD.getRegistryName(), "inventory"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        GhostlyCapability.IGhostlyHandler handler = GhostlyCapability.getHandler(playerIn);
        if (playerIn.isSneaking()) {
            System.out.println(handler.isVisible() + ", clientside: " + worldIn.isRemote);
            return EnumActionResult.SUCCESS;
        }
        if (handler.isVisible()) {
            if (worldIn.isRemote) {
                PacketHandler.INSTANCE.sendToServer(new PacketSpawnBody());
            } else {
                handler.setVisible(false);
                GhostlyCapability.causeSync(handler, playerIn);
            }
            return EnumActionResult.SUCCESS;
        } else {
            playerIn.sendStatusMessage(new TextComponentString("already invisible!"), true);
        }
        return EnumActionResult.FAIL;
    }
}
