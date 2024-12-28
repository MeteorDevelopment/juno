package org.meteordev.juno.mc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.meteordev.juno.api.Device;

public interface JunoInitCallback {
    Event<JunoInitCallback> EVENT = EventFactory.createArrayBacked(
            JunoInitCallback.class,
            (listeners) -> (device) -> {
                for (JunoInitCallback listener : listeners) {
                    listener.init(device);
                }
            }
    );

    void init(Device device);
}
