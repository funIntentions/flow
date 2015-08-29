package com.projects.model;

import com.projects.helper.DeviceUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A device that can help offset peak demand time by storing and releasing electricity.
 */
public class EnergyStorage extends Device {
    private DoubleProperty transferCapacity; // watts per minute
    private DoubleProperty storageCapacity; // watts
    private DoubleProperty storedEnergy; // watts
    private StringProperty storageStrategy; // file string

    /**
     * Energy Storage constructor.
     *
     * @param name             energy storage device's name
     * @param id               unique identifier for device
     * @param transferCapacity the rate at which the storage device can store and release energy per minute
     * @param storageCapacity  the watts of energy that can be stored at a moment in time
     * @param storedEnergy     amount of stored energy currently
     * @param storageStrategy  the storage strategy that will govern when and how much energy is stored or released
     */
    public EnergyStorage(String name, int id, double transferCapacity, double storageCapacity, double storedEnergy, String storageStrategy) {
        super(name, id);

        this.transferCapacity = new SimpleDoubleProperty(transferCapacity);
        this.storageCapacity = new SimpleDoubleProperty(storageCapacity);
        this.storedEnergy = new SimpleDoubleProperty(storedEnergy);
        this.storageStrategy = new SimpleStringProperty(storageStrategy);
    }

    /**
     * Energy Storage copy constructor.
     *
     * @param energyStorage Energy Storage to be copied
     */
    public EnergyStorage(EnergyStorage energyStorage) {
        super(energyStorage.getName(), DeviceUtil.getNextDeviceId());

        this.transferCapacity = new SimpleDoubleProperty(energyStorage.getStorageCapacity());
        this.storageCapacity = new SimpleDoubleProperty(energyStorage.getStorageCapacity());
        this.storedEnergy = new SimpleDoubleProperty(energyStorage.getStoredEnergy());
        this.storageStrategy = new SimpleStringProperty(energyStorage.getStorageStrategy());
    }

    public double getTransferCapacity() {
        return transferCapacity.get();
    }

    public void setTransferCapacity(double transferCapacity) {
        this.transferCapacity.set(transferCapacity);
    }

    public DoubleProperty transferCapacityProperty() {
        return transferCapacity;
    }

    public String getStorageStrategy() {
        return storageStrategy.get();
    }

    public void setStorageStrategy(String storageStrategy) {
        this.storageStrategy.set(storageStrategy);
    }

    public StringProperty storageStrategyProperty() {
        return storageStrategy;
    }

    public double getStoredEnergy() {
        return storedEnergy.get();
    }

    public void setStoredEnergy(double storedEnergy) {
        this.storedEnergy.set(storedEnergy);
    }

    public DoubleProperty storedEnergyProperty() {
        return storedEnergy;
    }

    public double getStorageCapacity() {
        return storageCapacity.get();
    }

    public void setStorageCapacity(double storageCapacity) {
        this.storageCapacity.set(storageCapacity);
    }

    public DoubleProperty storageCapacityProperty() {
        return storageCapacity;
    }
}
