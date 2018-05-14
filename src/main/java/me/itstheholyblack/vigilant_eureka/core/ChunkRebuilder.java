package me.itstheholyblack.vigilant_eureka.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkRebuilder {
    public static void rebuildChunk(World world, BlockPos pos) {
        // ngl i stole this from ICBM
        try {
            Chunk oldChunk = world.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
            IChunkProvider provider = world.getChunkProvider();
            IChunkGenerator generator = ((ChunkProviderServer) provider).chunkGenerator;
            Chunk newChunk = generator.generateChunk(oldChunk.x, oldChunk.z);
            //oldChunk.setTerrainPopulated(false);
            //oldChunk.populate(provider, generator);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = (pos.getY() - 4); y < (pos.getY() + 4); y++) {
                        IBlockState state = newChunk.getBlockState(x, y, z);
                        Block oldBlock = oldChunk.getBlockState(x, y, z).getBlock();
                        if (!oldBlock.equals(Blocks.END_PORTAL) && !oldBlock.equals(Blocks.END_PORTAL_FRAME) && !oldBlock.equals(Blocks.END_GATEWAY)) {
                            world.setBlockState(new BlockPos(x + oldChunk.x * 16, y, z + oldChunk.z * 16), state, 3);
                        }
                    }
                }
            }
            oldChunk.setTerrainPopulated(false);
            oldChunk.populate(provider, generator);
            System.out.println("Finished that up nice!");
            oldChunk.markDirty();
            oldChunk.resetRelightChecks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
