package com.projects.models;

import com.projects.helper.StructureType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/22/2015.
 */
public class CompositeStructure //extends Structure
{
    /*private Integer numberOfUnits;
    private Structure unit;

    public CompositeStructure(String structureName, String unitName)
    {
        super(structureName, -1, StructureType.COMPOSITE_UNIT, 1, new ArrayList<Property>(), new ArrayList<Device>(), new ArrayList<Device>(), new ArrayList<Device>());
        unit = new Structure(unitName, StructureType.SINGLE_UNIT);
    }

    public CompositeStructure(String structure, List<Property> propertyList)
    {
        super(structure, -1, StructureType.COMPOSITE_UNIT, 1, propertyList, new ArrayList<Device>(), new ArrayList<Device>(), new ArrayList<Device>());
    }

    public CompositeStructure(Structure structure)
    {
        super(structure.getName(),structure.getId(), structure.getType(), 1, structure.getProperties(), structure.getAppliances(), structure.getEnergySources(), structure.getEnergyStorageDevices());
        numberOfUnits = 0;
        unit = new Structure("Default", StructureType.SINGLE_UNIT);
    }

    public CompositeStructure(CompositeStructure structure)
    {
        super(structure.getName(), structure.getId(), structure.getType(), 1, structure.getProperties(), structure.getAppliances(), structure.getEnergySources(), structure.getEnergyStorageDevices());
        numberOfUnits = structure.getNumberOfUnits();
        unit = new Structure(structure.getUnit());
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public Structure getUnit() {
        return unit;
    }

    public void setUnit(Structure unit) {
        this.unit = unit;
    }*/
}
