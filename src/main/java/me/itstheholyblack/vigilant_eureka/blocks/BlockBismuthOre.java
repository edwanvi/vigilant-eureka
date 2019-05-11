package me.itstheholyblack.vigilant_eureka.blocks;

import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockBismuthOre extends Block {
    public BlockBismuthOre() {
        super(Material.ROCK);
        setTranslationKey(Reference.MOD_ID + ".bismuth_ore");
        setRegistryName("bismuth_ore");
        setCreativeTab(ModItems.CREATIVE_TAB);
        setHardness(3.0f);
        setHarvestLevel("pickaxe", 2);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.bismite;
    }

    public int quantityDropped(Random random) {
        return 2 + random.nextInt(5);
    }

    public int quantityDroppedWithBonus(int fortune, Random random) {
        return Math.min(9, this.quantityDropped(random) + random.nextInt(1 + fortune));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
