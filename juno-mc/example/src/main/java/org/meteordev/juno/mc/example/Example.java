package org.meteordev.juno.mc.example;

import net.fabricmc.api.ClientModInitializer;
import org.meteordev.juno.mc.events.JunoInitCallback;
import org.meteordev.juno.mc.events.Render2DCallback;
import org.meteordev.juno.mc.events.Render3DCallback;

public class Example implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JunoInitCallback.EVENT.register(Example2D::init);
        Render2DCallback.EVENT.register(Example2D::render);

        JunoInitCallback.EVENT.register(Example3D::init);
        Render3DCallback.EVENT.register(Example3D::render);
    }
}
