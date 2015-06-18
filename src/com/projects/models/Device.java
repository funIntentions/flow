package com.projects.models;

import com.projects.helper.DeviceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/18/2015.
 */
public class Device
{
    private String name;
    private DeviceType type;
    private List<PropertyModel> properties;

    public Device(String device, DeviceType deviceType, ArrayList<PropertyModel> deviceProperties)
    {
        name = device;
        type = deviceType;
        properties = deviceProperties;
    }
}
