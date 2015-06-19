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
    private List<PropertyModel> properties;
    private List<Device> appliances;
    private List<Device> energySources;
    private List<Device> energyStorageDevices;

    public Structure(String structure, StructureType structureType)
    {
        this(structure, structureType, new ArrayList<PropertyModel>(), new ArrayList<Device>(), new ArrayList<Device>() , new ArrayList<Device>());
    }

    public Structure(String structure, StructureType structureType, List<PropertyModel> propertyList)
    {
        this(structure, structureType, propertyList, new ArrayList<Device>(), new ArrayList<Device>(), new ArrayList<Device>());
    }

    public Structure(String structure,
                     StructureType structureType,
                     List<PropertyModel> propertyList,
                     List<Device> applianceList,
                     List<Device> energySourceList,
                     List<Device> energyStorageList)
    {
        name = structure;
        id = -1;
        type = structureType;
        properties = propertyList;
        appliances = applianceList;
        energySources = energySourceList;
        energyStorageDevices = energyStorageList;
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
