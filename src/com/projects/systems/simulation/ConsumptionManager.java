package com.projects.systems.simulation;

import com.projects.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/24/2015.
 */
public class ConsumptionManager
{
    private List<Structure> structures;
    private double usageInWattsPerHour;
    private double totalUsageInkWh;

    public ConsumptionManager()
    {
        structures = new ArrayList<Structure>();
    }

    public void calculateConsumption(double timeElapsedInSeconds)
    {
        usageInWattsPerHour = 0;
        double timeElapsedInHours = timeElapsedInSeconds / Time.SECONDS_IN_HOUR;

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            for (Appliance appliance : appliances)
            {
                // TODO: check if appliance is on
                usageInWattsPerHour += appliance.getAverageConsumption() * timeElapsedInHours;
            }
        }

        totalUsageInkWh += usageInWattsPerHour / 1000;
    }

    public void reset()
    {
        usageInWattsPerHour = 0;
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

    public double getUsageInWattsPerHour()
    {
        return usageInWattsPerHour;
    }
}
