package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.JarTile;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockJar extends BlockTileEntity<JarTile> {

    public BlockJar() {
        super(Material.GLASS, "vexjar");
        setUnlocalizedName(Reference.MOD_ID + ".vexjar");
        setRegistryName("vexjar");
        setCreativeTab(ModItems.CREATIVE_TAB);
    }

    @Override
    public Class<JarTile> getTileEntityClass() {
        return JarTile.class;
    }

    @Nullable
    @Override
    public JarTile createTileEntity(World world, IBlockState state) {
        return new JarTile();
    }
}
