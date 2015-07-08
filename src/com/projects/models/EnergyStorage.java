package com.projects.models;

import com.projects.helper.DeviceType;

import java.util.List;

/**
 * Created by Dan on 6/26/2015.
 */
public class EnergyStorage extends Device
{
    private boolean isCharging;
    private boolean isReleasing;
    private double chargingRate;
    private double storedEnergy;
    private double connectionPower;

    public EnergyStorage(List<Property> energyStorageProperties, ElectricityUsageSchedule deviceUsageSchedule)
    {
        super("Energy Storage", -1, DeviceType.ENERGY_STORAGE, energyStorageProperties, deviceUsageSchedule);
    }

    public EnergyStorage(EnergyStorage energyStorage)
    {
        super(energyStorage);
    }

    public EnergyStorage(String name, int id, List<Property> propertyList, ElectricityUsageSchedule deviceUsageSchedule)
    {
        super(name, id, DeviceType.ENERGY_STORAGE, propertyList, deviceUsageSchedule);

        for (Property property : propertyList)
        {
            changePropertyVariableValue(property);
        }
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

        if (property.getName().equals("IsCharging"))
        {
            isCharging = Boolean.valueOf(value.toString());
        }
        else if (property.getName().equals("IsReleasingEnergy"))
        {
            isReleasing = Boolean.valueOf(value.toString());
        }
        else if (property.getName().equals("ChargingRate"))
        {
            chargingRate = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("StoredEnergy"))
        {
            storedEnergy = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("ConnectionPower"))
        {
            connectionPower = Double.valueOf(value.toString());
        }
        else
        {
            System.out.println(property.getName() + " is an unknown property.");
        }
    }

    public boolean isCharging()
    {
        return isCharging;
    }

    public void setCharging(boolean isCharging)
    {
        this.isCharging = isCharging;
    }

    public double getConnectionPower()
    {
        return connectionPower;
    }

    public void setConnectionPower(double connectionPower)
    {
        this.connectionPower = connectionPower;
    }

    public double getStoredEnergy()
    {
        return storedEnergy;
    }

    public void setStoredEnergy(double storedEnergy)
    {
        this.storedEnergy = storedEnergy;
    }

    public double getChargingRate()
    {
        return chargingRate;
    }

    public void setChargingRate(double chargingRate)
    {
        this.chargingRate = chargingRate;
    }

    public boolean isReleasing()
    {
        return isReleasing;
    }

    public void setReleasing(boolean isReleasing)
    {
        this.isReleasing = isReleasing;
    }
}
