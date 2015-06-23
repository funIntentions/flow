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
    private Integer id;
    private DeviceType type;
    private List<PropertyModel> properties;

    public Device(Device device)
    {
       this(device.getName(), device.getId(), device.getType(), device.getProperties());
    }

    public Device(String device, Integer id, DeviceType deviceType, List<PropertyModel> deviceProperties)
    {
        name = device;
        this.id = id;
        type = deviceType;
        properties = new ArrayList<PropertyModel>();

        for (PropertyModel property : deviceProperties)
        {
            properties.add(new PropertyModel(property));
        }
    }

    public void changePropertyValue(int index, Object value)
    {
        properties.get(index).setValue(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
