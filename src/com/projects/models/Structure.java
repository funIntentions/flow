package com.projects.models;

import com.projects.helper.ImageType;
import com.projects.helper.StructureType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/17/2015.
 */
public class Structure
{
    protected String name;
    protected Integer id;
    protected StructureType type;
    protected ImageType image;
    protected Integer numberOfUnits;
    protected int x;
    protected int y;

    protected List<Property> properties;
    protected List<Device> appliances;
    protected List<Device> energySources;
    protected List<Device> energyStorageDevices;

    public Structure(String structure, StructureType structureType, ImageType imageType)
    {
        this(structure, -1, structureType, imageType, 1, new ArrayList<Property>(), new ArrayList<Device>(), new ArrayList<Device>() , new ArrayList<Device>());
    }

    public Structure(String structure, ImageType imageType, StructureType structureType, List<Property> propertyList)
    {
        this(structure, -1, structureType, imageType, 1, propertyList, new ArrayList<Device>(), new ArrayList<Device>(), new ArrayList<Device>());
    }

    public Structure(Structure structure)
    {
        this(structure.getName(),
                structure.getId(),
                structure.getType(),
                structure.getImage(),
                structure.getNumberOfUnits(),
                structure.getProperties(),
                structure.getAppliances(),
                structure.getEnergySources(),
                structure.getEnergyStorageDevices());
    }

    public Structure(String structure,
                     Integer id,
                     StructureType structureType,
                     ImageType imageType,
                     Integer numberOfUnits,
                     List<Property> propertyList,
                     List<Device> applianceList,
                     List<Device> energySourceList,
                     List<Device> energyStorageList)
    {
        name = structure;
        this.id = id;
        type = structureType;
        this.numberOfUnits = numberOfUnits;
        this.image = imageType;

        properties = copyProperties(propertyList);

        appliances = copyDevices(applianceList);
        energySources = copyDevices(energySourceList);
        energyStorageDevices = copyDevices(energyStorageList);

        for (Property property : properties)
        {
            changePropertyValue(properties.indexOf(property), property.getValue());
        }
    }

    private List<Property> copyProperties(List<Property> list)
    {
        List<Property> copy = new ArrayList<Property>();

        for (Property property : list)
        {
            copy.add(new Property(property));
        }

        return copy;
    }

    private List<Device> copyDevices(List<Device> list)
    {
        List<Device> copy = new ArrayList<Device>();

        for (Device device : list)
        {
            Device deviceCopy = null;

            if (device instanceof Appliance)
            {
                deviceCopy = new Appliance((Appliance)device);
            }
            else if (device instanceof EnergySource)
            {
                deviceCopy = new EnergySource((EnergySource)device);
            }
            else if (device instanceof EnergyStorage)
            {
                deviceCopy = new EnergyStorage((EnergyStorage)device);
            }
            copy.add(deviceCopy);
        }

        return copy;
    }

    public void changePropertyValue(int index, Object value)
    {
        Property property = properties.get(index);
        property.setValue(value);

        if (property.getName().equals("xCoordinate"))
        {
            x = Integer.valueOf(property.getValue().toString());
        }
        else if (property.getName().equals("yCoordinate"))
        {
            y = Integer.valueOf(property.getValue().toString());
        }
    }

    public List<Device> getEnergyStorageDevices()
    {
        return energyStorageDevices;
    }

    public void setEnergyStorageDevices(List<Device> energyStorageDevices)
    {
        this.energyStorageDevices = energyStorageDevices;
    }

    public List<Device> getEnergySources()
    {

        return energySources;
    }

    public void setEnergySources(List<Device> energySources)
    {
        this.energySources = energySources;
    }

    public List<Device> getAppliances()
    {
        return appliances;
    }

    public void setAppliances(List<Device> appliances)
    {
        this.appliances = appliances;
    }

    public List<Property> getProperties()
    {
        return properties;
    }

    public void setProperties(List<Property> properties)
    {
        this.properties = properties;
    }

    public StructureType getType()
    {
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

    public void setType(StructureType type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
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

    public ImageType getImage() {
        return image;
    }

    public void setImage(ImageType image) {
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
