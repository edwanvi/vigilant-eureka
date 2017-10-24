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

    private static ResourceLocation LEY_STRUCT_ABS_1 = new ResourceLocation(Reference.MOD_ID + ":leystruct1");
    private static ResourceLocation LAYING_STATUE = new ResourceLocation(Reference.MOD_ID + ":layingley");
    private static ResourceLocation LEY_WELL = new ResourceLocation(Reference.MOD_ID + ":leywell");
    private static ResourceLocation LEY_STATUE_1 = new ResourceLocation(Reference.MOD_ID + ":leystatue1");
    private static ResourceLocation LEY_LIBRARY = new ResourceLocation(Reference.MOD_ID + ":leylibrary");

    public WorldGenLey() {
        resourceLocations.add(LEY_STRUCT_ABS_1);
        resourceLocations.add(LAYING_STATUE);
        resourceLocations.add(LEY_WELL);
        resourceLocations.add(LEY_STATUE_1);
        resourceLocations.add(LEY_LIBRARY);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!world.isRemote) {
            if (random.nextInt(200) < 1) {
                // get values for template
                WorldServer server = (WorldServer) world;
                TemplateManager templateManager = server.getStructureTemplateManager();
                // random template resource location
                ResourceLocation r = resourceLocations.get(random.nextInt(resourceLocations.size()));
                // get template
                Template t = templateManager.get(world.getMinecraftServer(), r);
                // actual block level co-ords of chunk
                int blockX = chunkX * 16;
                int blockZ = chunkZ * 16;
                // get block level generation position
                int XModifier = random.nextInt(16);
                int ZModifier = random.nextInt(16);
                // pick random values in (
                while (!(XModifier == 0 || XModifier == 15 || ZModifier == 0 || ZModifier == 15)) {
                    XModifier = random.nextInt(16);
                    ZModifier = random.nextInt(16);
                }
                int randX = blockX + XModifier;
                int randZ = blockZ + ZModifier;
                // get ground level + 1 at (randX, randZ)
                int y;
                if (!(r.equals(LEY_WELL) || r.equals(LEY_LIBRARY))) {
                    y = 1 + getGroundFromAbove(world, randX, randZ);
                } else {
                    y = getGroundFromAbove(world, randX, randZ);
                }
                if (y < 0) {
                    System.out.println("Y < 0");
                    return;
                }
                // System.out.format("Generating %s at (%d, %d, %d)\n", r.toString(), randX, y, randZ);
                PlacementSettings settings = new PlacementSettings();
                BlockPos generationPos = new BlockPos(randX, y, randZ);
                settings.setRandom(world.rand);
                // Get random rotation value
                settings.setRotation(Rotation.values()[settings.getRandom(generationPos).nextInt(4)]);
                t.addBlocksToWorld(world, generationPos, settings);
            }
        }
    }


    int getGroundFromAbove(World world, int x, int z) {
        int y = 255;
        boolean foundGround = false;
        while (!foundGround && y-- >= 20) {
            Block blockAt = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            // "ground" is grass, sand, and dirt.
            foundGround = (blockAt.equals(Blocks.DIRT) || blockAt.equals(Blocks.GRASS) || blockAt.equals(Blocks.SAND));
        }
        return y;
    }
}
