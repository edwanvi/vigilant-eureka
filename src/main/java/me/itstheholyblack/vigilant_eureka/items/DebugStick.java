package me.itstheholyblack.vigilant_eureka.items;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
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
        if (!worldIn.isRemote && worldIn instanceof WorldServer) {
            try {
                Chunk oldChunk = worldIn.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
                IChunkProvider provider = worldIn.getChunkProvider();
                IChunkGenerator generator = ((ChunkProviderServer) provider).chunkGenerator;
                Chunk newChunk = generator.generateChunk(oldChunk.x, oldChunk.z);

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < worldIn.getHeight(); y++) {
                            IBlockState state = newChunk.getBlockState(x, y, z);
                            worldIn.setBlockState(new BlockPos(x + oldChunk.x * 16, y, z + oldChunk.z * 16), state, 3);
                        }
                    }
                }

                oldChunk.setTerrainPopulated(false);
                generator.populate(oldChunk.x, oldChunk.z);
                oldChunk.markDirty();
                oldChunk.resetRelightChecks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
