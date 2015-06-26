package com.projects.systems.simulation;

import com.projects.models.Appliance;
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

        System.out.println("Structures: " + structures.size());

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            System.out.println("Appliances: " + appliances.size());

            for (Appliance appliance : appliances)
            {
                totalConsumption += appliance.getAverageConsumption() * time;
            }
        }

        System.out.println("Total Consumption: " + totalConsumption);

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
