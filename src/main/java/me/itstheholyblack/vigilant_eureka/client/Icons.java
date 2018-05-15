package me.itstheholyblack.vigilant_eureka.client;

import me.itstheholyblack.vigilant_eureka.client.renderer.RenderHelp;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Icons {

    public static final Icons INSTANCE = new Icons();

    public TextureAtlasSprite time_open;
    public TextureAtlasSprite time_closed;

    private Icons(){}

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre evt) {
        time_open = RenderHelp.forName(evt.getMap(), "time_open_rend", "items");
        time_closed = RenderHelp.forName(evt.getMap(), "time_rend", "items");
    }
}
