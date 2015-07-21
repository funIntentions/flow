package com.projects.systems.simulation;

import com.projects.models.PowerPlant;
import com.projects.models.WorldTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/17/2015.
 */
public class StatsManager
{
    List<Float> priceForDemand;
    List<Float> emissionsForDemand;

    List<Float> dailyDemandTrends;
    List<Float> dailyPriceTrends;
    List<Float> dailyEmissionTrends;

    private int trendIndex;
    private int intervalCount;
    private long timeInterval;
    private double previousTime;
    private boolean dailyTrendDataReady;
    private List<Integer> dailyDemandBuffer;

    public StatsManager()
    {
        dailyDemandBuffer = new ArrayList<Integer>();
        priceForDemand = new ArrayList<Float>();
        emissionsForDemand = new ArrayList<Float>();
        dailyPriceTrends = new ArrayList<Float>();
        dailyEmissionTrends = new ArrayList<Float>();
        dailyDemandTrends = new ArrayList<Float>();
        resetDailyTrends(0);
    }

    public void resetDailyTrends(double currentTime)
    {
        previousTime = currentTime;
        trendIndex = 0;
        timeInterval = TimeUnit.MINUTES.toSeconds(30);
        intervalCount = (int)(TimeUnit.DAYS.toSeconds(1)/timeInterval);

        dailyPriceTrends.clear();
        dailyEmissionTrends.clear();
        dailyDemandTrends.clear();

        dailyTrendDataReady = false;

        copyDailyDemandBuffer();
    }

    private void copyDailyDemandBuffer()
    {
        for (Integer demand : dailyDemandBuffer)
        {
            dailyDemandTrends.add(0f);
            dailyEmissionTrends.add(0f);
            dailyPriceTrends.add(0f);

            dailyDemandTrends.set(trendIndex, (float)demand);
            dailyEmissionTrends.set(trendIndex, (emissionsForDemand.get(demand)));
            dailyPriceTrends.set(trendIndex, (priceForDemand.get(demand)));

            previousTime += timeInterval;
            ++trendIndex;
        }

        dailyDemandBuffer.clear();
    }

    public void updatePriceAndEmissionsStats(SupplyManager supplyManager)
    {
        int maxDemand = 0;

        List<PowerPlant> powerPlants = supplyManager.getPowerPlants();
        for (PowerPlant powerPlant : powerPlants)
        {
            maxDemand += powerPlant.getCapacity();
        }

        priceForDemand.clear();
        emissionsForDemand.clear();

        for (int demand = 0; demand <= maxDemand; ++demand)
        {
            supplyManager.calculateSupply(demand);
            priceForDemand.add(0f);
            emissionsForDemand.add(0f);

            double price = supplyManager.getPrice();
            double emissions = supplyManager.getEmissions();

            priceForDemand.set(demand, (float)price);
            emissionsForDemand.set(demand, (float)emissions);
        }
    }

    public float getEmissionsForDemand(int demand)
    {
        if (emissionsForDemand.size() == 0)
            return 0;
        else if (demand > emissionsForDemand.size())
            return emissionsForDemand.get(emissionsForDemand.size()-1);
        else
            return emissionsForDemand.get(demand);
    }

    public float getPriceForDemand(int demand)
    {
        if (priceForDemand.size() == 0)
            return 0;
        else if (demand > priceForDemand.size())
            return priceForDemand.get(priceForDemand.size()-1);
        else
            return priceForDemand.get(demand);
    }

    public void logDailyTrends(DemandManager demandManager, WorldTimer worldTimer)
    {
        double difference = worldTimer.getTotalTimeInSeconds() - previousTime;

        if (difference/timeInterval > 0 && !dailyTrendDataReady)
        {
            difference /= timeInterval;

            for (int i = 0; i < difference; ++i) // TODO: change this so that no matter the update rate, all the data will be accurate, currently data can be skipped and bad assumptions are being made.
            {
                //demandManager.calculateDemand(worldTimer.getTotalTimeInSeconds() - previousTime, difference);
                int currentDemand = (int) Math.floor(demandManager.getElectricityDemand());

                if (trendIndex != intervalCount)
                {
                    dailyDemandTrends.add((float)currentDemand);
                    dailyEmissionTrends.add(getEmissionsForDemand(currentDemand));
                    dailyPriceTrends.add(getPriceForDemand(currentDemand));

                    //dailyDemandTrends.set(trendIndex, (float)currentDemand);
                    //dailyEmissionTrends.set(trendIndex, emissionsForDemand.get(currentDemand));
                    //dailyPriceTrends.set(trendIndex, priceForDemand.get(currentDemand));
                }
                else
                {
                    dailyDemandBuffer.add(currentDemand);
                }

                previousTime += timeInterval;
                ++trendIndex;
            }

            if (trendIndex == intervalCount)
            {
                dailyTrendDataReady = true;
            }
        }
    }

    public List<Float> getDailyEmissionTrends()
    {
        return dailyEmissionTrends;
    }

    public List<Float> getDailyPriceTrends()
    {
        return dailyPriceTrends;
    }

    public List<Float> getDailyDemandTrends()
    {
        return dailyDemandTrends;
    }

    public List<Float> getPriceForDemand()
    {
        return priceForDemand;
    }

    public List<Float> getEmissionsForDemand()
    {
        return emissionsForDemand;
    }

    public boolean isDailyTrendDataReady()
    {
        return dailyTrendDataReady;
    }
}
