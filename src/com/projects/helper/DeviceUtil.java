package com.projects.helper;

/**
 * Created by Dan on 7/29/2015.
 */
public class DeviceUtil {
    private static int nextDeviceId = 0;

    public DeviceUtil() {

    }

    public static int getNextDeviceId() {
        return nextDeviceId++;
    }
}
