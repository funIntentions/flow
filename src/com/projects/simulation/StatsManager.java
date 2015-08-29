package com.projects.simulation;

import com.projects.Main;
import com.projects.model.PowerPlant;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the details of production and the daily trends in the simulation.
 */
public class StatsManager {
    List<Float> priceForDemand;
    List<Float> emissionsForDemand;
    List<List<Float>> dailyDemandTrends;
    List<List<Float>> dailyPriceTrends;
    List<List<Float>> dailyEmissionTrends;
    private Main main;

    /**
     * StatsManager constructor.
     */
    public StatsManager() {
        priceForDemand = new ArrayList<>();
        emissionsForDemand = new ArrayList<>();
        dailyPriceTrends = new ArrayList<>();
        dailyEmissionTrends = new ArrayList<>();
        dailyDemandTrends = new ArrayList<>();
    }

    /**
     * Provides a reference to main and add a listener so that stats are set for the requested day.
     *
     * @param main
     */
    public void setMain(Main main) {
        this.main = main;

        main.dailyStatsDayProperty().addListener((observable, oldValue, newValue) ->
        {
            setStatsForDay(newValue.intValue());
        });
    }

    /**
     * Set the daily trends for the requested day.
     *
     * @param day day during the span of the simulation
     */
    public void setStatsForDay(int day) {
        main.getDailyStatisticsController().setDemandChartData(dailyDemandTrends.get(day));
        main.getDailyStatisticsController().setPriceChartData(dailyPriceTrends.get(day));
        main.getDailyStatisticsController().setEmissionsChartData(dailyEmissionTrends.get(day));
    }

    /**
     * Resets the daily trends.
     */
    public void reset() {
        dailyPriceTrends.clear();
        dailyEmissionTrends.clear();
        dailyDemandTrends.clear();
    }

    /**
     * Calculates the data for price and emission rates as demand changes.
     *
     * @param supplyManager supply manager which manages the worlds power plants
     */
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

    /**
     * Returns the emission rate when there is a particular electricity demand.
     *
     * @param demand the electricity demand
     * @return the emission rate (g/kWh)
     */
    public float getEmissionsForDemand(int demand) {
        if (emissionsForDemand.size() == 0)
            return 0;
        else if (demand > emissionsForDemand.size())
            return emissionsForDemand.get(emissionsForDemand.size() - 1);
        else
            return emissionsForDemand.get(demand);
    }

    /**
     * Returns the price of power when there is a particular electricity demand.
     *
     * @param demand the electricity demand
     * @return the cost $/kWh
     */
    public float getPriceForDemand(int demand) {
        if (priceForDemand.size() == 0)
            return 0;
        else if (demand > priceForDemand.size())
            return priceForDemand.get(priceForDemand.size() - 1);
        else
            return priceForDemand.get(demand);
    }

    /**
     * Given a demand profile for the day the daily trends are calculated using the price and emission rate data.
     *
     * @param demandProfile the demand profile for the day
     */
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
    }

    public List<Float> getDailyEmissionTrendsForToday() {
        return dailyEmissionTrends.get(dailyEmissionTrends.size() - 1);
    }

    public List<Float> getDailyPriceTrendsForToday() {
        return dailyPriceTrends.get(dailyPriceTrends.size() - 1);
    }

    public List<Float> getDailyDemandTrendsForToday() {
        return dailyDemandTrends.get(dailyDemandTrends.size() - 1);
    }

    public List<Float> getPriceForDemandData() {
        return priceForDemand;
    }

    public List<Float> getEmissionsForDemandData() {
        return emissionsForDemand;
    }

    public List<List<Float>> getDailyEmissionTrends() {
        return dailyEmissionTrends;
    }

    public List<List<Float>> getDailyDemandTrends() {
        return dailyDemandTrends;
    }

    public List<List<Float>> getDailyPriceTrends() {
        return dailyPriceTrends;
    }
}

