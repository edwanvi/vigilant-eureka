package me.itstheholyblack.vigilant_eureka.client;

import me.itstheholyblack.vigilant_eureka.Reference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keybinds {

    public static KeyBinding warpKey;

    private static KeyBinding init(String s, int key, String group) {
        KeyBinding kb = new KeyBinding(Reference.MOD_ID + ".keybind." + s, key, group);
        ClientRegistry.registerKeyBinding(kb);
        return kb;
    }

    public static void initWarpKey() {
        warpKey = init("warp_key", Keyboard.KEY_R, Reference.MOD_ID + ".gui.keybinds");
    }
}
