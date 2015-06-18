package com.projects.models;

import com.projects.helper.StructureType;

import java.util.List;

/**
 * Created by Dan on 6/17/2015.
 */
public class Structure
{
    private String name;
    private StructureType type;
    private List<PropertyModel> properties;
    private List<Device> appliances;
    private List<Device> energySources;
    private List<Device> energyStorageDevices;

    public Structure(String structure, StructureType structureType)
    {
        name = structure;
        type = structureType;
    }

    // TODO: create two more constructors
}
