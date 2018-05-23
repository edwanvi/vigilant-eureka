package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.network.PacketHandler;
import me.itstheholyblack.vigilant_eureka.network.PacketSpawnBody;
import me.itstheholyblack.vigilant_eureka.util.NBTUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
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
        NBTTagCompound persist = NBTUtil.getPlayerPersist(playerIn);
        if (!persist.getBoolean("metaphysical_high_ground")) {
            persist.setBoolean("metaphysical_high_ground", true);
            playerIn.setInvisible(true);
            if (worldIn.isRemote) {
                PacketHandler.INSTANCE.sendToServer(new PacketSpawnBody());
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}
