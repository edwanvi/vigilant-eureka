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

    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":leyline")
    public static BlockLeyLine leyLine;

    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":bismuth_ore")
    public static BlockBismuthOre bismuthOre;

    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":vexjar")
    public static BlockJar blockJar;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        firstBlock.initModel();
        movingdoor.initModel();
        bismuthOre.initModel();
        leyLine.initModel();
    }
}
