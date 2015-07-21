package com.projects.systems.simulation;

import com.projects.models.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 6/24/2015.
 */
public class DemandManager
{
    private List<Structure> structures;
    private HashMap<Integer, List<Float>> structureLoadProfiles;
    private HashMap<Integer, List<Float>> structureDemandProfiles;
    private List<Integer> todaysDemandProfile;
    private float timeOverflow;
    private double usageInWattsPerHour = 0;
    private double totalUsageInkWh = 0;
    private double electricityDemand = 0;
    private boolean dailyDemandProfileReady = false;

    public DemandManager()
    {
        structures = new ArrayList<Structure>();
        structureLoadProfiles = new HashMap<Integer, List<Float>>();
        structureDemandProfiles = new HashMap<Integer, List<Float>>();
        todaysDemandProfile = new ArrayList<Integer>();
        timeOverflow = 0;
    }

    public List<Float> getLoadProfile(Structure structure)
    {
        return structureLoadProfiles.get(structure.getId());
    }

    public void calculateLoadProfiles()
    {
        int secondsInDay = (int)TimeUnit.DAYS.toSeconds(1);
        int interval = 60;
        int length = secondsInDay/interval;

        for (Structure structure : structures)
        {
            List<Float> loadProfile = new ArrayList<Float>();
            List<Appliance> appliances = (List)structure.getAppliances();

            for (int time = 0; time < length; ++time)
            {
                loadProfile.add(0f);

                for (Appliance appliance : appliances)
                {
                    if (appliance.getElectricityUsageSchedule().isOnAtTime(time * interval))
                    {
                        float sum = loadProfile.get(time) + (float) appliance.getAverageConsumption();
                        loadProfile.set(time, sum);
                    }
                }
            }

            structureLoadProfiles.put(structure.getId(), loadProfile);
        }
    }

    public void calculateDemandProfiles(StorageManager storageManager)
    {
        HashMap<Integer, List<Float>> storageProfiles = storageManager.getDeviceStorageProfiles();

        for (Structure structure : structures)
        {
            List<Float> loadProfile = structureLoadProfiles.get(structure.getId());
            List<Float> demandProfile = new ArrayList<Float>();

            int length = loadProfile.size();
            for (int time = 0; time < length; ++time)
            {
                float storageDemand = storageManager.getStructuresStorageDemandAtTime(structure, time);
                float loadDemand = loadProfile.get(time);

                demandProfile.add(loadDemand + storageDemand);
            }

            structureDemandProfiles.put(structure.getId(), demandProfile);
        }
    }

    public void resetDay()
    {
        todaysDemandProfile.clear();
        dailyDemandProfileReady = false;
        processOverflowBuffer();
    }

    private void processOverflowBuffer()
    {
        for (int time = 0; time < timeOverflow; ++time)
        {
            electricityDemand = 0;

            for (List<Float> demandProfile : structureDemandProfiles.values())
            {
                electricityDemand += demandProfile.get(time);
            }

            todaysDemandProfile.add((int) electricityDemand);
        }

        timeOverflow = 0;
    }

    public void calculateDemand(double timeElapsedInSeconds, double totalTimeElapsedInSeconds)
    {
        int previousElapsedSecondsThisDay = (int)(Math.floor(totalTimeElapsedInSeconds - timeElapsedInSeconds) % TimeUnit.DAYS.toSeconds(1));
        int previousElapsedMinutesThisDay = (int)TimeUnit.SECONDS.toMinutes(previousElapsedSecondsThisDay);
        int totalElapsedMinutesThisDay = (int)TimeUnit.SECONDS.toMinutes((long)totalTimeElapsedInSeconds % TimeUnit.DAYS.toSeconds(1));


        if (previousElapsedMinutesThisDay > totalElapsedMinutesThisDay)
        {
            timeOverflow = totalElapsedMinutesThisDay;

            totalElapsedMinutesThisDay = (int)TimeUnit.DAYS.toMinutes(1);
        }

        for (int time = previousElapsedMinutesThisDay; time < totalElapsedMinutesThisDay; ++time)
        {
            electricityDemand = 0;

            for (List<Float> demandProfile : structureDemandProfiles.values())
            {
                electricityDemand += demandProfile.get(time);
            }

            todaysDemandProfile.add((int) electricityDemand);
        }

        System.out.println(todaysDemandProfile.size());

        if (todaysDemandProfile.size() == TimeUnit.DAYS.toMinutes(1))
            dailyDemandProfileReady = true;


        /*usageInWattsPerHour = 0;
        electricityDemand = 0;

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            for (Appliance appliance : appliances)
            {
                double applianceUsageInHours = getAppliancesUsageInHours(timeElapsedInSeconds, totalTimeElapsedInSeconds, appliance);
                double applianceConsumptionDuringHours = appliance.getAverageConsumption() * applianceUsageInHours;
                usageInWattsPerHour += applianceConsumptionDuringHours;

                if (applianceConsumptionDuringHours > 0) // device is on
                    electricityDemand += appliance.getAverageConsumption();
            }

            usageInWattsPerHour *= structure.getNumberOfUnits();
            electricityDemand *= structure.getNumberOfUnits();
        }

        totalUsageInkWh += usageInWattsPerHour / 1000;*/
    }

    public double getAppliancesUsageInHours(double elapsedSecondsThisFrame, double totalTimeElapsedInSeconds, Appliance appliance)
    {
        double usageInSeconds = 0;
        ElectricityUsageSchedule usageSchedule = appliance.getElectricityUsageSchedule();

        double elapsedSecondsThisDay = (totalTimeElapsedInSeconds - elapsedSecondsThisFrame) % WorldTimer.SECONDS_IN_DAY;
        double secondsLeftInDay = WorldTimer.SECONDS_IN_DAY - elapsedSecondsThisDay;

        if (elapsedSecondsThisFrame <= secondsLeftInDay)
        {
            usageInSeconds += usageSchedule.getElectricityUsageDuringSpan(new TimeSpan(elapsedSecondsThisDay, elapsedSecondsThisDay + elapsedSecondsThisFrame));
        }
        else
        {
            usageInSeconds += usageSchedule.getElectricityUsageDuringSpan(new TimeSpan(elapsedSecondsThisDay, elapsedSecondsThisDay + secondsLeftInDay));

            double remainingSeconds = elapsedSecondsThisFrame - secondsLeftInDay;

            int numDays = (int)Math.floor(remainingSeconds / WorldTimer.SECONDS_IN_DAY);
            usageInSeconds += ((double)numDays) * usageSchedule.getUsagePerDay();
            remainingSeconds = remainingSeconds % WorldTimer.SECONDS_IN_DAY;

            usageInSeconds += usageSchedule.getElectricityUsageDuringSpan(new TimeSpan(0, remainingSeconds));
        }

        return usageInSeconds / WorldTimer.SECONDS_IN_HOUR;
    }

    public void reset()
    {
        usageInWattsPerHour = 0;
        totalUsageInkWh = 0;
    }

    public boolean isConsumer(Structure structure)
    {
        return (structureLoadProfiles.get(structure.getId()) != null);
    }

    public boolean removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId() == structureToRemove.getId())
            {
                structures.remove(structure);
                return true;
            }
        }

        return false;
    }

    public void removeAllStructures()
    {
        structures.clear();
    }

    public boolean syncStructures(Structure changedStructure)
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
        else
        {
            return false;
        }

        return true;
    }

    public double getTotalUsageInkWh()
    {
        return totalUsageInkWh;
    }

    public double getUsageInWattsPerHour()
    {
        return usageInWattsPerHour;
    }

    public double getElectricityDemand()
    {
        return electricityDemand;
    }

    public List<Integer> getTodaysDemandProfile()
    {
        return todaysDemandProfile;
    }

    public boolean isDailyDemandProfileReady()
    {
        return dailyDemandProfileReady;
    }
}

