package me.itstheholyblack.vigilant_eureka.core;

import me.itstheholyblack.vigilant_eureka.util.ArrayUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkRebuilder {

    private static final Block[] blacklist = new Block[]{
            Blocks.END_GATEWAY,
            Blocks.END_PORTAL,
            Blocks.END_PORTAL_FRAME,
            Blocks.BEDROCK,
            Blocks.BARRIER,
            Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK
    };

    public static void rebuildChunk(World world, BlockPos pos, BlockPos stand) {
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
                        BlockPos working = new BlockPos(x + oldChunk.x * 16, y, z + oldChunk.z * 16);
                        if (!ArrayUtil.contains(blacklist, oldBlock)) {
                            world.setBlockState(working, state, 3);
                            if (world.canSeeSky(working) && !oldBlock.equals(state.getBlock())) {
                                ((WorldServer) world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, true, working.getX(), working.getY(), working.getZ(), 10, 0.5, 1, 0.5, 0.05D);
                            }
                        }
                    }
                }
            }
            oldChunk.setTerrainPopulated(false);
            oldChunk.populate(provider, generator);
            oldChunk.markDirty();
            oldChunk.resetRelightChecks();
            ((WorldServer) world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, true, stand.getX(), stand.getY(), stand.getZ(), 1000, 0.75, 1, 0.75, 0.05D);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
