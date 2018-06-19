package me.itstheholyblack.vigilant_eureka.proxy;

import com.google.common.collect.ImmutableMap;
import me.itstheholyblack.vigilant_eureka.Reference;
import me.itstheholyblack.vigilant_eureka.blocks.*;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import me.itstheholyblack.vigilant_eureka.blocks.tiles.MovingCastleDoorTile;
import me.itstheholyblack.vigilant_eureka.core.EventHandler;
import me.itstheholyblack.vigilant_eureka.entity.ModEntities;
import me.itstheholyblack.vigilant_eureka.items.*;
import me.itstheholyblack.vigilant_eureka.items.armor.InvisCap;
import me.itstheholyblack.vigilant_eureka.items.armor.WarpBoots;
import me.itstheholyblack.vigilant_eureka.items.baubles.ItemTime;
import me.itstheholyblack.vigilant_eureka.network.PacketHandler;
import me.itstheholyblack.vigilant_eureka.world.WorldGenBismuth;
import me.itstheholyblack.vigilant_eureka.world.WorldGenLey;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        Reference.LOGGER.info("Registering Vigilant Eureka event handler.");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        Reference.LOGGER.info("Registering Vigilant Eureka networking.");
        PacketHandler.registerMessages(Reference.MOD_ID);
        ModEntities.init();
        registerTiles();
    }

    public void init(FMLInitializationEvent e) {
        WorldGenBismuth worldGenBismuth = new WorldGenBismuth();
        GameRegistry.registerWorldGenerator(worldGenBismuth, 100);
        GameRegistry.registerWorldGenerator(new WorldGenLey(), 100);
        GameRegistry.addSmelting(ModItems.bismite, new ItemStack(ModItems.leyKey), 0.7F);
        // fix the tiles
        ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(Reference.MOD_ID, 1);
        fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileEntitySpaceFixer());
    }

    public void postInit(FMLPostInitializationEvent e) {
        OreDictionary.registerOre("oreBismuth", ModBlocks.bismuthOre);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BasicBlock());
        event.getRegistry().register(new MovingCastleDoor());
        event.getRegistry().register(new BlockBismuthOre());
        event.getRegistry().register(new BlockLeyLine());
    }

    private static void registerTiles() {
        GameRegistry.registerTileEntity(MovingCastleDoorTile.class, new ResourceLocation(Reference.MOD_ID, "castledoor"));
        GameRegistry.registerTileEntity(LeyLineTile.class, new ResourceLocation(Reference.MOD_ID, "leytile"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry()
                .register(new ItemBlock(ModBlocks.firstBlock).setRegistryName(ModBlocks.firstBlock.getRegistryName()));
        event.getRegistry()
                .register(new ItemBlock(ModBlocks.movingdoor).setRegistryName(ModBlocks.movingdoor.getRegistryName()).setMaxStackSize(16)); // shoot me
        event.getRegistry().register(new ItemBlock(ModBlocks.bismuthOre).setRegistryName(ModBlocks.bismuthOre.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.leyLine).setRegistryName(ModBlocks.leyLine.getRegistryName()));
        event.getRegistry().register(new DimKey());
        event.getRegistry().register(new WarpBoots());
        event.getRegistry().register(new InvisCap());
        event.getRegistry().register(new ItemTime());
        event.getRegistry().register(new ItemBismite());
        event.getRegistry().register(new ItemLeyKey());
        event.getRegistry().register(new ItemLeyRune());
        event.getRegistry().register(new DebugStick());
        event.getRegistry().register(new ItemCard());
        Items.FIREWORKS.setCreativeTab(CreativeTabs.MISC); // please and thank you
    }

    private static class TileEntitySpaceFixer implements IFixableData {
        private final Map<String, String> tileEntityNames;

        {
            ImmutableMap.Builder<String, String> nameMap = ImmutableMap.builder();
            nameMap.put("minecraft:19d2daed26722f2762d067603604a6d1f909f262leytile", "vigilant_eureka:leytile");
            nameMap.put("19d2daed26722f2762d067603604a6d1f909f262leytile", "vigilant_eureka:leytile");
            nameMap.put("minecraft:leytile", "vigilant_eureka:leytile");
            nameMap.put("leytile", "vigilant_eureka:leytile");
            tileEntityNames = nameMap.build();
        }

        @Override
        public int getFixVersion() {
            return 1;
        }

        @Nonnull
        @Override
        public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
            String tileEntityLocation = compound.getString("id");

            compound.setString("id", tileEntityNames.getOrDefault(tileEntityLocation, tileEntityLocation));

            return compound;
        }
    }
}
