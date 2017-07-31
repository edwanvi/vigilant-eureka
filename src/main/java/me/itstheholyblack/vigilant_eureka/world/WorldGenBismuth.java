package me.itstheholyblack.vigilant_eureka.world;

import me.itstheholyblack.vigilant_eureka.blocks.ModBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenBismuth implements IWorldGenerator {
    WorldGenMinable generator;

    public WorldGenBismuth() {
        generator = new WorldGenMinable(ModBlocks.bismuthOre.getDefaultState(), 10);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimension()) {
            case 0: //Overworld
                this.runGenerator(this.generator, world, rand, chunkX, chunkZ, 15, 0, 80);
                break;
            case -1: //Nether
                break;
            case 1: //End
                break;
        }
    }

    private void runGenerator(WorldGenMinable generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
        int heightDiff = maxHeight - minHeight + 1;
        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunk_X * 16 + rand.nextInt(16);
            int y = minHeight + rand.nextInt(heightDiff);
            int z = chunk_Z * 16 + rand.nextInt(16);
            generator.generate(world, rand, new BlockPos(x, y, z));
        }
    }
}