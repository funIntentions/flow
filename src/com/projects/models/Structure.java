package com.projects.models;

import com.projects.helper.StructureType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/17/2015.
 */
public class Structure
{
    private String name;
    private Integer id;
    private StructureType type;
    private Integer numberOfUnits;
    private List<PropertyModel> properties;
    private List<Device> appliances;
    private List<Device> energySources;
    private List<Device> energyStorageDevices;

    public Structure(String structure, StructureType structureType)
    {
        this(structure, -1, structureType, 1, new ArrayList<PropertyModel>(), new ArrayList<Device>(), new ArrayList<Device>() , new ArrayList<Device>());
    }

    public Structure(String structure, StructureType structureType, List<PropertyModel> propertyList)
    {
        this(structure, -1, structureType, 1, propertyList, new ArrayList<Device>(), new ArrayList<Device>(), new ArrayList<Device>());
    }

    public Structure(Structure structure)
    {
        this(structure.getName(),
                structure.getId(),
                structure.getType(),
                structure.getNumberOfUnits(),
                structure.getProperties(),
                structure.getAppliances(),
                structure.getEnergySources(),
                structure.getEnergyStorageDevices());
    }

    public Structure(String structure,
                     Integer id,
                     StructureType structureType,
                     Integer numberOfUnits,
                     List<PropertyModel> propertyList,
                     List<Device> applianceList,
                     List<Device> energySourceList,
                     List<Device> energyStorageList)
    {
        name = structure;
        this.id = id;
        type = structureType;
        this.numberOfUnits = numberOfUnits;

        properties = copyProperties(propertyList);

        appliances = copyDevices(applianceList);
        energySources = copyDevices(energySourceList);
        energyStorageDevices = copyDevices(energyStorageList);
    }

    private List<PropertyModel> copyProperties(List<PropertyModel> list)
    {
        List<PropertyModel> copy = new ArrayList<PropertyModel>();

        for (PropertyModel property : list)
        {
            copy.add(new PropertyModel(property));
        }

        return copy;
    }

    private List<Device> copyDevices(List<Device> list)
    {
        List<Device> copy = new ArrayList<Device>();

        for (Device device : list)
        {
            copy.add(new Device(device));
        }

        return copy;
    }

    public List<Device> getEnergyStorageDevices() {
        return energyStorageDevices;
    }

    public void setEnergyStorageDevices(List<Device> energyStorageDevices) {
        this.energyStorageDevices = energyStorageDevices;
    }

    public List<Device> getEnergySources() {

        return energySources;
    }

    public void setEnergySources(List<Device> energySources) {
        this.energySources = energySources;
    }

    public List<Device> getAppliances() {

        return appliances;
    }

    public void setAppliances(List<Device> appliances) {
        this.appliances = appliances;
    }

    public List<PropertyModel> getProperties() {

        return properties;
    }

    public void setProperties(List<PropertyModel> properties) {
        this.properties = properties;
    }

    public StructureType getType() {

        return type;
    }

    public Integer getNumberOfUnits()
    {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits)
    {
        this.numberOfUnits = numberOfUnits;
    }

    public void setType(StructureType type) {
        this.type = type;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }


}
