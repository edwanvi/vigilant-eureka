package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":firstblock")
    public static BasicBlock firstBlock;

    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":movingdoor")
    public static MovingCastleDoor movingdoor;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        firstBlock.initModel();
        movingdoor.initModel();
    }
}
