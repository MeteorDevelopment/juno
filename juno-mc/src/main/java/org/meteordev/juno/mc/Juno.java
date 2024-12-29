package org.meteordev.juno.mc;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.AbstractTexture;
import org.meteordev.juno.api.BackendInfo;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.mc.backend.MCDevice;
import org.meteordev.juno.mc.backend.MCWrappedImage;
import org.meteordev.juno.utils.validation.ValidationDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Juno {
    private static Logger log;
    private static Device device;

    public static void init() {
        log = LoggerFactory.getLogger("Juno");
        device = new MCDevice();

        if (FabricLoader.getInstance().isDevelopmentEnvironment() || System.getProperty("JUNO_FORCE_VALIDATION") != null) {
            device = ValidationDevice.wrap(device);
        }

        BackendInfo info = device.getBackendInfo();

        log.info("Juno initialized with backend:");
        log.info("    Name: {}", info.name());
        log.info("    Detail: {}", info.detail());
    }

    public static Device getDevice() {
        return device;
    }

    public static Image wrap(AbstractTexture texture) {
        return new MCWrappedImage(texture);
    }

    private Juno() {}
}
