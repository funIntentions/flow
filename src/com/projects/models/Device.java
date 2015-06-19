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

    public Device(String device, DeviceType deviceType, List<PropertyModel> deviceProperties)
    {
        name = device;
        type = deviceType;
        properties = deviceProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public List<PropertyModel> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyModel> properties) {
        this.properties = properties;
    }
}
