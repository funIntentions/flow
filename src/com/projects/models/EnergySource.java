package com.projects.models;

import com.projects.helper.DeviceType;

import java.util.List;

/**
 * Created by Dan on 6/26/2015.
 */
public class EnergySource extends Device
{
    private double ratedDCVoltage;
    private double ratedACVoltage;
    private double current;

    public EnergySource(List<Property> energySourceProperties, ElectricityUsageSchedule deviceUsageSchedule)
    {
        super("Energy Source", -1, DeviceType.ENERGY_SOURCE, energySourceProperties, deviceUsageSchedule);
    }

    public EnergySource(EnergySource energySource)
    {
        super(energySource);
    }

    public EnergySource(String name, int id, List<Property> propertyList, ElectricityUsageSchedule deviceUsageSchedule)
    {
        super(name, id, DeviceType.ENERGY_SOURCE, propertyList, deviceUsageSchedule);
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

        if (property.getName().equals("Rated DC Voltage"))
        {
            ratedDCVoltage = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("Rated AC Voltage"))
        {
            ratedACVoltage = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("Current"))
        {
            current = Double.valueOf(value.toString());
        }
        else
        {
            System.out.println(property.getName() + " is an unknown property.");
        }
    }

    public double getCurrent()
    {
        return current;
    }

    public void setCurrent(double current)
    {
        this.current = current;
    }

    public double getRatedACVoltage()
    {
        return ratedACVoltage;
    }

    public void setRatedACVoltage(double ratedACVoltage)
    {
        this.ratedACVoltage = ratedACVoltage;
    }

    public double getRatedDCVoltage()
    {
        return ratedDCVoltage;
    }

    public void setRatedDCVoltage(double ratedDCVoltage)
    {
        this.ratedDCVoltage = ratedDCVoltage;
    }
}
