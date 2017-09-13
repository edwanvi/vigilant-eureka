package me.itstheholyblack.vigilant_eureka.world;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenLey implements IWorldGenerator {

    private ArrayList<ResourceLocation> resourceLocations = new ArrayList<>();

    public WorldGenLey() {
        resourceLocations.add(new ResourceLocation(Reference.MOD_ID + ":leystruct1"));
        resourceLocations.add(new ResourceLocation(Reference.MOD_ID + ":layingley"));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!world.isRemote) {
            // world randomizer instance
            Random rand = world.rand;
            if (rand.nextInt(100) < 25) {
                // get values for template
                WorldServer server = (WorldServer) world;
                TemplateManager templateManager = server.getStructureTemplateManager();
                ResourceLocation r = resourceLocations.get(world.rand.nextInt(resourceLocations.size()));
                // fetch template
                Template t = templateManager.get(world.getMinecraftServer(), r);
                // actual block level co-ords of chunk
                int blockX = chunkX * 16;
                int blockZ = chunkZ * 16;
                // get block level generation position
                int XModifier = rand.nextInt(16);
                int ZModifier = rand.nextInt(16);
                while (!(XModifier == 0 || XModifier == 15 || ZModifier == 0 || ZModifier == 15)) {
                    XModifier = rand.nextInt(16);
                    ZModifier = rand.nextInt(16);
                }
                int randX = blockX + XModifier;
                int randZ = blockZ + ZModifier;
                // get ground level + 1 at (randX, randZ)
                int y = 1 + getGroundFromAbove(world, randX, randZ);
                PlacementSettings settings = new PlacementSettings();
                settings.setRandom(world.rand);
                // Get random rotation value
                settings.setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);
                t.addBlocksToWorld(world, new BlockPos(randX, y, randZ), settings);
            }
        }
    }


    int getGroundFromAbove(World world, int x, int z) {
        int y = 255;
        boolean foundGround = false;
        while (!foundGround && y-- >= 0) {
            Block blockAt = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            // "ground" is grass, sand, and dirt.
            foundGround = blockAt.equals(Blocks.DIRT) || blockAt.equals(Blocks.GLASS) || blockAt.equals(Blocks.SAND);
        }

        return y;
    }
}
