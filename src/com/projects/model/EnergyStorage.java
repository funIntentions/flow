package com.projects.model;

import com.projects.helper.StorageState;
import com.projects.helper.StorageStrategy;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by Dan on 6/26/2015.
 */
public class EnergyStorage extends Device
{
    private DoubleProperty chargingRate;
    private DoubleProperty storageCapacity;
    private DoubleProperty storedEnergy;
    private StorageStrategy storageStrategy;
    private StorageState storageState = StorageState.CHARGING;

    public EnergyStorage(String name, int id, double chargingRate, double storageCapacity, double storedEnergy, StorageStrategy storageStrategy)
    {
        super(name, id);

        this.chargingRate = new SimpleDoubleProperty(chargingRate);
        this.storageCapacity = new SimpleDoubleProperty(storageCapacity);
        this.storedEnergy = new SimpleDoubleProperty(storedEnergy);
        this.storageStrategy = storageStrategy;
    }

    public double getChargingRate()
    {
        return chargingRate.get();
    }

    public DoubleProperty chargingRateProperty()
    {
        return chargingRate;
    }

    public void setChargingRate(double chargingRate)
    {
        this.chargingRate.set(chargingRate);
    }

    public StorageState getStorageState()
    {
        return storageState;
    }

    public void setStorageState(StorageState storageState)
    {
        this.storageState = storageState;
    }

    public StorageStrategy getStorageStrategy()
    {
        return storageStrategy;
    }

    public void setStorageStrategy(StorageStrategy storageStrategy)
    {
        this.storageStrategy = storageStrategy;
    }

    public double getStoredEnergy()
    {
        return storedEnergy.get();
    }

    public DoubleProperty storedEnergyProperty()
    {
        return storedEnergy;
    }

    public void setStoredEnergy(double storedEnergy)
    {
        this.storedEnergy.set(storedEnergy);
    }

    public double getStorageCapacity()
    {
        return storageCapacity.get();
    }

    public DoubleProperty storageCapacityProperty()
    {
        return storageCapacity;
    }

    public void setStorageCapacity(double storageCapacity)
    {
        this.storageCapacity.set(storageCapacity);
    }
}
