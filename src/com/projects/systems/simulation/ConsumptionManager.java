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
    private List<Structure> structures;
    private double totalUsageInWatts;
    private double totalUsageInkWh;

    public ConsumptionManager()
    {
        structures = new ArrayList<Structure>();
    }

    public void calculateConsumption(double totalHours)
    {
        totalUsageInWatts = 0;

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            for (Appliance appliance : appliances)
            {
                totalUsageInWatts += appliance.getAverageConsumption();
            }
        }

        totalUsageInkWh = (totalUsageInWatts * totalHours) / 1000;
    }

    public void reset()
    {
        totalUsageInWatts = 0;
        totalUsageInkWh = 0;
    }

    public void removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId().intValue() == structureToRemove.getId().intValue())
            {
                structures.remove(structure);
                return;
            }
        }
    }

    public void removeAllStructures()
    {
        structures.clear();
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

    public double getTotalUsageInkWh() {
        return totalUsageInkWh;
    }

    public double getTotalUsageInWatts()
    {
        return totalUsageInWatts;
    }
}
