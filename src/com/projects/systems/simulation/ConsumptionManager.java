package com.projects.systems.simulation;

import com.projects.models.Device;
import com.projects.models.Property;
import com.projects.models.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/24/2015.
 */
public class ConsumptionManager
{
    List<Structure> structures;

    public ConsumptionManager()
    {
        structures = new ArrayList<Structure>();
    }

    public double calculateConsumption(double time)
    {
        double totalConsumption = 0;

        for (Structure structure : structures)
        {
            List<Device> appliances = structure.getAppliances();

            for (Device appliance : appliances)
            {
                List<Property> properties = appliance.getProperties();

                for (Property property : properties)
                {
                    if (property.getName().equals("AverageConsumption"))
                    {
                        totalConsumption += Double.valueOf(property.getValue().toString()) * time;
                    }
                }
            }
        }

        return totalConsumption;
    }

    public void removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId().intValue() == structureToRemove.getId())
            {
                structures.remove(structure);
                return;
            }
        }
    }

    public void syncStructures(Structure changedStructure)
    {
        int structureIndex = -1;

        for (int i = 0; i < structures.size(); ++i)
        {
            if (changedStructure.getId().intValue() == structures.get(i).getId().intValue())
            {
                structureIndex = i;
            }
        }

        int applianceCount = changedStructure.getAppliances().size();

        if (structureIndex < 0 && applianceCount > 0)
        {
            structures.add(changedStructure);
        }
        else if (structureIndex >=0)
        {
            if (applianceCount > 0)
            {
                structures.set(structureIndex, changedStructure);
            }
            else
            {
                structures.remove(structureIndex);
            }
        }
    }
}
