package com.projects.simulation;

import com.projects.Main;
import com.projects.model.PowerPlant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 7/17/2015.
 */
public class StatsManager {
    List<Float> priceForDemand;
    List<Float> emissionsForDemand;
    List<List<Float>> dailyDemandTrends;
    List<List<Float>> dailyPriceTrends;
    List<List<Float>> dailyEmissionTrends;
    private Main main;
    private int currentDay;
    private boolean dailyTrendDataReady;
    private List<Integer> dailyDemandBuffer;

    public StatsManager() {
        dailyDemandBuffer = new ArrayList<>();
        priceForDemand = new ArrayList<>();
        emissionsForDemand = new ArrayList<>();
        dailyPriceTrends = new ArrayList<>();
        dailyEmissionTrends = new ArrayList<>();
        dailyDemandTrends = new ArrayList<>();
        currentDay = -1;
        resetDailyTrends();
    }

    public void setMain(Main main) {
        this.main = main;

        main.dailyStatsDayProperty().addListener((observable, oldValue, newValue) ->
        {
            setStatsForDay(newValue.intValue());
        });
    }

    public void setStatsForDay(int day) {
        main.getDailyStatisticsController().setDemandChartData(dailyDemandTrends.get(day));
        main.getDailyStatisticsController().setPriceChartData(dailyPriceTrends.get(day));
        main.getDailyStatisticsController().setEmissionsChartData(dailyEmissionTrends.get(day));
    }


    public void reset() {
        dailyPriceTrends.clear();
        dailyEmissionTrends.clear();
        dailyDemandTrends.clear();
        currentDay = -1;
        resetDailyTrends();
    }

    public void resetDailyTrends() {
        dailyTrendDataReady = false;

        copyDailyDemandBuffer();
    }

    private void copyDailyDemandBuffer() {
        for (Integer demand : dailyDemandBuffer) {
            dailyDemandTrends.get(currentDay).add((float) demand);
            dailyEmissionTrends.get(currentDay).add((emissionsForDemand.get(demand)));
            dailyPriceTrends.get(currentDay).add((priceForDemand.get(demand)));
        }

        dailyDemandBuffer.clear();
    }

    public void updatePriceAndEmissionsStats(SupplyManager supplyManager) {
        int maxDemand = 0;

        List<PowerPlant> powerPlants = supplyManager.getPowerPlants();
        for (PowerPlant powerPlant : powerPlants) {
            maxDemand += powerPlant.getCapacity();
        }

        priceForDemand.clear();
        emissionsForDemand.clear();

        for (int demand = 0; demand <= maxDemand; ++demand) {
            supplyManager.calculateSupply(demand);
            priceForDemand.add(0f);
            emissionsForDemand.add(0f);

            double price = supplyManager.getPrice();
            double emissions = supplyManager.getEmissions();

            priceForDemand.set(demand, (float) price);
            emissionsForDemand.set(demand, (float) emissions);
        }
    }

    public float getEmissionsForDemand(int demand) {
        if (emissionsForDemand.size() == 0)
            return 0;
        else if (demand > emissionsForDemand.size())
            return emissionsForDemand.get(emissionsForDemand.size() - 1);
        else
            return emissionsForDemand.get(demand);
    }

    public float getPriceForDemand(int demand) {
        if (priceForDemand.size() == 0)
            return 0;
        else if (demand > priceForDemand.size())
            return priceForDemand.get(priceForDemand.size() - 1);
        else
            return priceForDemand.get(demand);
    }

    public void logDailyTrends(List<Integer> demandProfile) {
        List<Float> dailyDemandTrend = new ArrayList<>();
        List<Float> dailyEmissionTrend = new ArrayList<>();
        List<Float> dailyPriceTrend = new ArrayList<>();

        for (int time = 0; time < demandProfile.size(); ++time) {
            int currentDemand = demandProfile.get(time);
            dailyDemandTrend.add((float) currentDemand);
            dailyEmissionTrend.add(getEmissionsForDemand(currentDemand));
            dailyPriceTrend.add(getPriceForDemand(currentDemand));
        }

        dailyDemandTrends.add(dailyDemandTrend);
        dailyEmissionTrends.add(dailyEmissionTrend);
        dailyPriceTrends.add(dailyPriceTrend);

        ++currentDay;
    }

    public List<Float> getDailyEmissionTrends() {
        return dailyEmissionTrends.get(currentDay);
    }

    public List<Float> getDailyPriceTrends() {
        return dailyPriceTrends.get(currentDay);
    }

    public List<Float> getDailyDemandTrends() {
        return dailyDemandTrends.get(currentDay);
    }

    public List<Float> getDailyEmissionTrends(int day) {
        return dailyEmissionTrends.get(day);
    }

    public List<Float> getDailyPriceTrends(int day) {
        return dailyPriceTrends.get(day);
    }

    public List<Float> getDailyDemandTrends(int day) {
        return dailyDemandTrends.get(day);
    }

    public List<Float> getPriceForDemand() {
        return priceForDemand;
    }

    public List<Float> getEmissionsForDemand() {
        return emissionsForDemand;
    }

    public boolean isDailyTrendDataReady() {
        return dailyTrendDataReady;
    }
}

