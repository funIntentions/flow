package com.projects.models;

import com.projects.helper.DeviceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/18/2015.
 */
public abstract class Device
{
    protected String name;
    protected Integer id;
    protected DeviceType type; // maybe not needed anymore?
    protected List<Property> properties;
    private ElectricityUsageSchedule electricityUsageSchedule;

    public Device(Device device)
    {
       this(device.getName(), device.getId(), device.getType(), device.getProperties(), device.getElectricityUsageSchedule());
    }

    public Device(String device, Integer id, DeviceType deviceType, List<Property> deviceProperties, ElectricityUsageSchedule deviceUsageSchedule)
    {
        name = device;
        this.id = id;
        type = deviceType;
        properties = new ArrayList<Property>();
        electricityUsageSchedule = new ElectricityUsageSchedule(deviceUsageSchedule);

        for (Property property : deviceProperties)
        {
            properties.add(new Property(property));
            changePropertyVariableValue(property);
        }
    }

    public abstract void changePropertyValue(int index, Object value);

    protected abstract void changePropertyVariableValue(Property property);

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

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public ElectricityUsageSchedule getElectricityUsageSchedule() {
        return electricityUsageSchedule;
    }

    public void setElectricityUsageSchedule(ElectricityUsageSchedule electricityUsageSchedule) {
        this.electricityUsageSchedule = electricityUsageSchedule;
    }
}
