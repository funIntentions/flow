package com.projects.systems.simulation;

import com.projects.models.PowerPlant;
import com.projects.models.WorldTimer;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/17/2015.
 */
public class StatsManager
{
    float[] priceForDemand;
    float[] emissionsForDemand;

    float[] dailyDemandTrends;
    float[] dailyPriceTrends;
    float[] dailyEmissionTrends;

    private int trendIndex;
    private int intervalCount;
    private long timeInterval;
    private double previousTime;
    private boolean dailyTrendDataReady;

    public StatsManager()
    {
        resetDailyTrends(0);
    }

    public void resetDailyTrends(double currentTime)
    {
        previousTime = currentTime;
        trendIndex = 0;
        timeInterval = TimeUnit.MINUTES.toSeconds(30);
        intervalCount = (int)(TimeUnit.DAYS.toSeconds(1)/timeInterval);

        dailyPriceTrends = new float[intervalCount];
        dailyEmissionTrends = new float[intervalCount];
        dailyDemandTrends = new float[intervalCount];

        dailyTrendDataReady = false;
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

    public void logDailyTrends(DemandManager demandManager, WorldTimer worldTimer)
    {
        int difference = (int)(Math.floor(worldTimer.getTotalTimeInSeconds() - previousTime));

        if (difference/timeInterval > 0 && !dailyTrendDataReady)
        {
            difference /= timeInterval;
            int currentDemand = (int)Math.floor(demandManager.getElectricityDemand());

            for (int i = 0; i < difference; ++i) // TODO: change this so that no matter the update rate, all the data will be accurate, currently data can be skipped and bad assumptions are being made.
            {
                dailyDemandTrends[trendIndex] = currentDemand;
                dailyEmissionTrends[trendIndex] = emissionsForDemand[currentDemand];
                dailyPriceTrends[trendIndex] = priceForDemand[currentDemand];

                previousTime += timeInterval;
                ++trendIndex;

                if (trendIndex == intervalCount)
                {
                    dailyTrendDataReady = true;
                    break;
                }
            }
        }
    }

    public float[] getDailyEmissionTrends()
    {
        return dailyEmissionTrends;
    }

    public float[] getDailyPriceTrends()
    {
        return dailyPriceTrends;
    }

    public float[] getDailyDemandTrends()
    {
        return dailyDemandTrends;
    }

    public float[] getPriceForDemand()
    {
        return priceForDemand;
    }

    public float[] getEmissionsForDemand()
    {
        return emissionsForDemand;
    }

    public boolean isDailyTrendDataReady()
    {
        return dailyTrendDataReady;
    }
}
