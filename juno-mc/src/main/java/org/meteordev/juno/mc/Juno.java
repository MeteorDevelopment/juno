package org.meteordev.juno.mc;

import org.meteordev.juno.api.Device;
import org.meteordev.juno.opengl.GLDevice;

public class Juno {
    private static Device device;

    public static void init() {
        device = GLDevice.create();
    }

    public static Device getDevice() {
        return device;
    }
}
