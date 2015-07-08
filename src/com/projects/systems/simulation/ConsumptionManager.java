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
    private double timeElapsedPreviously;

    public ConsumptionManager()
    {
        structures = new ArrayList<Structure>();
        timeElapsedPreviously = 0; // start of day
    }

    public void calculateConsumption(double timeElapsedInSeconds, double timeElapsedThisDayInSeconds)
    {
        usageInWattsPerHour = 0;

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            for (Appliance appliance : appliances)
            {
                double applianceUsageInHours = getAppliancesUsageInHours(timeElapsedInSeconds, timeElapsedThisDayInSeconds, appliance);
                usageInWattsPerHour += appliance.getAverageConsumption() * applianceUsageInHours;
            }
        }

        totalUsageInkWh += usageInWattsPerHour / 1000;
    }

    public double getAppliancesUsageInHours(double elapsedSecondsThisFrame, double elapsedSecondsThisDay, Appliance appliance)
    {
        double usageInSeconds = 0;
        double remainder = elapsedSecondsThisFrame;
        ElectricityUsageSchedule usageSchedule = appliance.getElectricityUsageSchedule();

        if (remainder >= Time.SECONDS_IN_DAY)
        {
            int numDays = (int)Math.floor(remainder / Time.SECONDS_IN_DAY);
            usageInSeconds += ((double)numDays) * usageSchedule.getUsagePerDay();
            remainder = remainder % Time.SECONDS_IN_DAY;

            timeElapsedPreviously = 0;
        }
        else
        {
            timeElapsedPreviously = elapsedSecondsThisDay - remainder;
        }

        if (remainder != 0)
        {
            usageInSeconds += usageSchedule.getElectricityUsageDuringSpan(new TimeSpan(timeElapsedPreviously, elapsedSecondsThisDay));
        }

        return usageInSeconds / Time.SECONDS_IN_HOUR;
    }

    public void reset()
    {
        usageInWattsPerHour = 0;
        totalUsageInkWh = 0;
        timeElapsedPreviously = 0;
    }

    public void removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId() == structureToRemove.getId())
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
            if (changedStructure.getId() == structures.get(i).getId())
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
