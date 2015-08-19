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
    protected ObservableList<Appliance> appliances;
    protected ObservableList<EnergySource> energySources;
    protected ObservableList<EnergyStorage> energyStorageDevices;
    private ObservableList<ObservableList<Float>> loadProfilesForWeek;
    private DemandState demandState = DemandState.LOW;

    public Building(Building building)
    {
        super(building.getName(), StructureUtil.getNextStructureId(), building.getAnimatedSprite());

        this.appliances = building.getAppliances();
        this.energySources = building.getEnergySources();
        this.energyStorageDevices = building.getEnergyStorageDevices();
        this.loadProfilesForWeek = building.getLoadProfilesForWeek();
    }

    public Building(String name, int id, double x, double y, AnimatedSprite animatedSprite, List<Appliance> appliances, List<EnergySource> energySources, List<EnergyStorage> energyStorageDevices)
    {
        super(name, id, x, y, animatedSprite);

        this.appliances = FXCollections.observableArrayList(appliances);
        this.energySources = FXCollections.observableArrayList(energySources);
        this.energyStorageDevices = FXCollections.observableArrayList(energyStorageDevices);
        this.loadProfilesForWeek = FXCollections.observableArrayList();

        calculateLoadProfile();
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

            loadProfilesForWeek.add(loadProfile);
        }
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
