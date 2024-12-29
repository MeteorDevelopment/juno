package org.meteordev.juno.mc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.joml.Matrix4f;
import org.meteordev.juno.api.Device;

public interface Render3DCallback {
    Event<Render3DCallback> EVENT = EventFactory.createArrayBacked(
            Render3DCallback.class,
            (listeners) -> (device, projection, view) -> {
                for (Render3DCallback listener : listeners) {
                    listener.render(device, projection, view);
                }
            }
    );

    void render(Device device, Matrix4f projection, Matrix4f view);
}
