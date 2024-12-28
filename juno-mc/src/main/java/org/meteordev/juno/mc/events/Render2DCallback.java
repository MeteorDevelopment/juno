package org.meteordev.juno.mc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.meteordev.juno.api.Device;

public interface Render2DCallback {
    Event<Render2DCallback> EVENT = EventFactory.createArrayBacked(
            Render2DCallback.class,
            (listeners) -> (device) -> {
                for (Render2DCallback listener : listeners) {
                    listener.render(device);
                }
            }
    );

    void render(Device device);
}
