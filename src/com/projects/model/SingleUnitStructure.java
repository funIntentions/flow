package com.projects.model;

import com.projects.helper.ImageType;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by Dan on 8/12/2015.
 */
public class SingleUnitStructure extends Structure
{
    protected ObservableList<Appliance> appliances;
    protected ObservableList<EnergySource> energySources;
    protected ObservableList<EnergyStorage> energyStorageDevices;

    public SingleUnitStructure(SingleUnitStructure singleUnitStructure)
    {
        super(singleUnitStructure.getName(), singleUnitStructure.getId(), singleUnitStructure.getX(), singleUnitStructure.getY(), singleUnitStructure.getImage());

        this.appliances = singleUnitStructure.getAppliances();
        this.energySources = singleUnitStructure.getEnergySources();
        this.energyStorageDevices = singleUnitStructure.getEnergyStorageDevices();
    }

    public SingleUnitStructure(String name, int id, ImageType imageType, List<Appliance> appliances, List<EnergySource> energySources, List<EnergyStorage> energyStorageDevices)
    {
        super(name, id, 0, 0, imageType);

        this.appliances = FXCollections.observableArrayList(appliances);
        this.energySources = FXCollections.observableArrayList(energySources);
        this.energyStorageDevices = FXCollections.observableArrayList(energyStorageDevices);
    }

    public ObservableList<Appliance> getAppliances()
    {
        return appliances;
    }

    public void setAppliances(ObservableList<Appliance> appliances)
    {
        this.appliances = appliances;
    }

    public ObservableList<EnergyStorage> getEnergyStorageDevices()
    {
        return energyStorageDevices;
    }

    public void setEnergyStorageDevices(ObservableList<EnergyStorage> energyStorageDevices)
    {
        this.energyStorageDevices = energyStorageDevices;
    }

    public ObservableList<EnergySource> getEnergySources()
    {
        return energySources;
    }

    public void setEnergySources(ObservableList<EnergySource> energySources)
    {
        this.energySources = energySources;
    }
}
