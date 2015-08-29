package com.projects.helper;

/**
 * Utility for assisting with devices.
 */
public class DeviceUtil {
    private static int nextDeviceId = 0;

    public static int getNextDeviceId() {
        return nextDeviceId++;
    }
}
