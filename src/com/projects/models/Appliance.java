package com.projects.models;

import com.projects.helper.DeviceType;

import java.util.List;

/**
 * Created by Dan on 6/26/2015.
 */
public class Appliance extends Device
{
    private double standbyConsumption;
    private double averageConsumption;
    private double power;

    public Appliance(List<Property> applianceProperties, ElectricityUsageSchedule deviceUsageSchedule)
    {
        super("Appliance", -1, DeviceType.APPLIANCE, applianceProperties, deviceUsageSchedule);
    }

    public Appliance(Appliance appliance)
    {
        super(appliance);
    }

    public Appliance(String name, int id, List<Property> propertyList, ElectricityUsageSchedule deviceUsageSchedule)
    {
        super(name, id, DeviceType.APPLIANCE, propertyList, deviceUsageSchedule);
    }

    public void changePropertyValue(int index, Object value)
    {
        Property property = properties.get(index);
        property.setValue(value);

        changePropertyVariableValue(property);
    }

    protected void changePropertyVariableValue(Property property)
    {
        Object value = property.getValue();

        if (property.getName().equals("StandbyConsumption"))
        {
            standbyConsumption = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("AverageConsumption"))
        {
            averageConsumption = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("Power"))
        {
            power = Double.valueOf(value.toString());
        }
        else
        {
            System.out.println(property.getName() + " is an unknown property.");
        }
    }

    public double getStandbyConsumption()
    {
        return standbyConsumption;
    }

    public void setStandbyConsumption(double standbyConsumption)
    {
        this.standbyConsumption = standbyConsumption;
    }

    public double getPower()
    {
        return power;
    }

    public void setPower(double power)
    {
        this.power = power;
    }

    public double getAverageConsumption()
    {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption)
    {
        this.averageConsumption = averageConsumption;
    }

}
