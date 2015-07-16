package com.projects.systems.simulation;

import com.projects.models.PowerPlant;
import com.projects.models.WorldTimer;

import java.util.List;

/**
 * Created by Dan on 6/29/2015.
 */
public class SimulationStatus
{
    public WorldTimer worldTimer;
    public List<PowerPlant> powerPlants;
    public double price;
    public double totalUsageInkWh;
    public double electricityDemand;
    public double emissions;
}
