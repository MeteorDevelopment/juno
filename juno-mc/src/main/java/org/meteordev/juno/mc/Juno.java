package org.meteordev.juno.mc;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.AbstractTexture;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.mc.backend.MCDevice;
import org.meteordev.juno.mc.backend.MCWrappedImage;
import org.meteordev.juno.utils.validation.ValidationDevice;

public class Juno {
    private static Device device;

    public static void init() {
        device = new MCDevice();

        if (FabricLoader.getInstance().isDevelopmentEnvironment() || System.getProperty("JUNO_FORCE_VALIDATION") != null) {
            device = ValidationDevice.wrap(device);
        }
    }

    public static Device getDevice() {
        return device;
    }

    public static Image wrap(AbstractTexture texture) {
        return new MCWrappedImage(texture);
    }

    private Juno() {}
}
