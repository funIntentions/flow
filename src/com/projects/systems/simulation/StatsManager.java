package com.projects.systems.simulation;

import com.projects.models.PowerPlant;

import java.util.List;

/**
 * Created by Dan on 7/17/2015.
 */
public class StatsManager
{
    float[] priceForDemand;
    float[] emissionsForDemand;

    public StatsManager()
    {

    }

    public void updatePriceAndEmissionsStats(SupplyManager supplyManager)
    {
        int maxDemand = 0;

        List<PowerPlant> powerPlants = supplyManager.getPowerPlants();
        for (PowerPlant powerPlant : powerPlants)
        {
            maxDemand += powerPlant.getCapacity();
        }

        priceForDemand = new float[maxDemand];
        emissionsForDemand = new float[maxDemand];

        for (int demand = 0; demand < maxDemand; ++demand)
        {
            supplyManager.calculateSupply(demand);

            double price = supplyManager.getPrice();
            double emissions = supplyManager.getEmissions();

            priceForDemand[demand] = (float)price;
            emissionsForDemand[demand] = (float)emissions;
        }
    }

    public void calculateSimulationStats(DemandManager demandManager, SupplyManager supplyManager)
    {

    }

    public float[] getPriceForDemand()
    {
        return priceForDemand;
    }

    public float[] getEmissionsForDemand()
    {
        return emissionsForDemand;
    }
}
