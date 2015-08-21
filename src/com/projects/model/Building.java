package com.projects.model;

import com.projects.helper.Constants;
import com.projects.helper.DemandState;
import com.projects.helper.StructureUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by Dan on 8/12/2015.
 */
public class Building extends Structure
{
    private ObservableList<Appliance> appliances;
    private ObservableList<EnergySource> energySources;
    private ObservableList<EnergyStorage> energyStorageDevices;
    private ObservableList<ObservableList<Float>> loadProfilesForWeek;
    private ObservableList<UsageTimeSpan> manualLoadProfileData;
    private boolean usingCustomLoadProfile = false;
    private DemandState demandState = DemandState.LOW;

    public Building(Building building)
    {
        super(building.getName(), StructureUtil.getNextStructureId(), building.getAnimatedSprite());

        this.appliances = building.getAppliances();
        this.energySources = building.getEnergySources();
        this.energyStorageDevices = building.getEnergyStorageDevices();
        this.loadProfilesForWeek = building.getLoadProfilesForWeek();
        this.manualLoadProfileData = building.getManualLoadProfileData();
    }

    public Building(String name, int id, double x, double y, AnimatedSprite animatedSprite, List<Appliance> appliances, List<EnergySource> energySources, List<EnergyStorage> energyStorageDevices, List<UsageTimeSpan> customUsageTimeSpans)
    {
        super(name, id, x, y, animatedSprite);

        this.appliances = FXCollections.observableArrayList(appliances);
        this.energySources = FXCollections.observableArrayList(energySources);
        this.energyStorageDevices = FXCollections.observableArrayList(energyStorageDevices);
        this.loadProfilesForWeek = FXCollections.observableArrayList();
        this.manualLoadProfileData = FXCollections.observableArrayList(customUsageTimeSpans);

        calculateLoadProfile();
    }

    public double getManualUsageAtDateAndTime(int day, double time)
    {
        double totalUsage = 0;

        for (UsageTimeSpan manualUsage : manualLoadProfileData)
        {
            if (manualUsage.isActiveForDay(day) &&
                    (manualUsage.getFrom().toSecondOfDay() <= time &&
                            manualUsage.getTo().toSecondOfDay() >= time))
            {
                totalUsage += manualUsage.getUsage();
            }
        }

        return totalUsage;
    }

    public void calculateLoadProfile()
    {
        loadProfilesForWeek.clear();

        int interval = 60;
        int length = (int)Constants.SECONDS_IN_DAY/interval;

        for (int day = 0; day < Constants.DAYS_IN_WEEK; ++day)
        {
            ObservableList<Float> loadProfile = FXCollections.observableArrayList();

            for (int time = 0; time < length; ++time)
            {
                loadProfile.add(0f);

                if (!usingCustomLoadProfile)
                {
                    for (Appliance appliance : appliances)
                    {
                        if (appliance.isOn(day, time * interval))
                        {
                            float sum = loadProfile.get(time) + (float) appliance.getUsageConsumption();
                            loadProfile.set(time, sum);
                        }
                        else
                        {
                            float sum = loadProfile.get(time) + (float) appliance.getStandbyConsumption();
                            loadProfile.set(time, sum);
                        }
                    }
                }
                else
                {
                    loadProfile.set(time,(float) getManualUsageAtDateAndTime(day, time * interval));
                }
            }

            loadProfilesForWeek.add(loadProfile);
        }
    }

    public ObservableList<UsageTimeSpan> getManualLoadProfileData()
    {
        return manualLoadProfileData;
    }

    public void setManualLoadProfileData(ObservableList<UsageTimeSpan> manualLoadProfileData)
    {
        this.manualLoadProfileData = manualLoadProfileData;
    }

    public boolean isUsingCustomLoadProfile()
    {
        return usingCustomLoadProfile;
    }

    public void setUsingCustomLoadProfile(boolean usingCustomLoadProfile)
    {
        this.usingCustomLoadProfile = usingCustomLoadProfile;
    }

    public ObservableList<Appliance> getAppliances()
    {
        return appliances;
    }

    public void setAppliances(ObservableList<Appliance> appliances)
    {
        this.appliances = appliances;
    }

    public ObservableList<EnergyStorage> getEnergyStorageDevices()
    {
        return energyStorageDevices;
    }

    public void setEnergyStorageDevices(ObservableList<EnergyStorage> energyStorageDevices)
    {
        this.energyStorageDevices = energyStorageDevices;
    }

    public ObservableList<EnergySource> getEnergySources()
    {
        return energySources;
    }

    public void setEnergySources(ObservableList<EnergySource> energySources)
    {
        this.energySources = energySources;
    }

    public ObservableList<ObservableList<Float>> getLoadProfilesForWeek()
    {
        return loadProfilesForWeek;
    }

    public DemandState getDemandState()
    {
        return demandState;
    }

    public void setDemandState(DemandState demandState)
    {
        this.demandState = demandState;
    }
}
