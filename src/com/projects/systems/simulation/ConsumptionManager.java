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

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            for (Appliance appliance : appliances)
            {
                double applianceUsageInHours = getAppliancesUsageInHours(timeElapsedInSeconds, appliance) / Time.SECONDS_IN_HOUR;
                usageInWattsPerHour += appliance.getAverageConsumption() * applianceUsageInHours;
            }
        }

        totalUsageInkWh += usageInWattsPerHour / 1000;
    }

    public double getAppliancesUsageInHours(double elapsedSeconds, Appliance appliance)
    {
        double usageInHours = 0;
        double remainder = elapsedSeconds;
        ElectricityUsageSchedule usageSchedule = appliance.getElectricityUsageSchedule();

        /*if (remainder >= Time.SECONDS_IN_YEAR)
        {
            remainder = remainder % Time.SECONDS_IN_YEAR;
            int numYears = (int)Math.floor(remainder / Time.SECONDS_IN_YEAR);
        }
        if (remainder >= Time.SECONDS_IN_MONTH)
        {
            remainder = remainder % Time.SECONDS_IN_MONTH;
            int numMonths = (int)Math.floor(remainder / Time.SECONDS_IN_MONTH);
        }
        if (remainder >= Time.SECONDS_IN_WEEK)
        {
            remainder = remainder % Time.SECONDS_IN_WEEK;
            int numWeeks = (int)Math.floor(remainder / Time.SECONDS_IN_WEEK);
        }*/
        if (remainder >= Time.SECONDS_IN_DAY)
        {
            int numDays = (int)Math.floor(remainder / Time.SECONDS_IN_DAY);
            usageInHours += ((double)numDays) * usageSchedule.getUsagePerDay();

            remainder = remainder % Time.SECONDS_IN_DAY;
        }

        if (remainder != 0)
        {
            usageInHours += usageSchedule.getElectricityUsageDuringSpan(new TimeSpan(0, remainder)); // TODO: should I optimize it if it'll always be 0?
        }

        return usageInHours;
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
