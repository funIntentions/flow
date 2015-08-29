package com.projects.simulation;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.DemandState;
import com.projects.model.Building;
import com.projects.model.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages the energy demands for all the buildings in the world.
 */
public class DemandManager {
    private List<Building> structures;
    private HashMap<Integer, Float> structureExpenses;
    private HashMap<Integer, Float> structureEnvironmentalImpact;
    private HashMap<Integer, List<Float>> structureDemandProfiles;
    private List<Integer> demandProfileForToday;
    private float timeOverflow;
    private double totalUsageInkWh = 0;
    private double electricityDemand = 0;
    private boolean dailyDemandProfileReady = false;
    private float lowDemand, averageDemand, mediumDemand, highDemand;
    private Main main;

    /**
     * DemandManager constructor
     */
    public DemandManager() {
        structureExpenses = new HashMap<>();
        structureEnvironmentalImpact = new HashMap<>();
        structures = new ArrayList<>();
        structureDemandProfiles = new HashMap<>();
        demandProfileForToday = new ArrayList<>();
        timeOverflow = 0;
    }

    /**
     * Provides a reference of main and adds a listener to mains selected structure so that it can send the selected structure's load profiles to the structure details pane to display.
     * @param main reference to main
     */
    public void setMain(Main main) {
        this.main = main;

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue instanceof Building)
                main.getBuildingDetailsPaneController().setStructureData(newValue, ((Building) newValue).getLoadProfilesForWeek());
            else
                main.getBuildingDetailsPaneController().setStructureData(newValue, new ArrayList<>());
        });
    }

    /**
     * Calculates the high, medium, mild and low levels of demand for the day that are relative to the maximum demand during the day.
     * @param day day of the week
     */
    public void calculateDemandStates(int day) {
        structureDemandProfiles.clear();
        float largestDemandSum = 0, demandSum;

        for (int time = 0; time < Constants.MINUTES_IN_DAY; ++time) {
            demandSum = 0;
            for (Building structure : structures) {
                List<Float> loadProfile = structure.getLoadProfilesForWeek().get(day);

                demandSum += loadProfile.get(time);
            }

            demandSum /= structures.size();

            if (demandSum > largestDemandSum) {
                largestDemandSum = demandSum;
            }
        }

        float increment = largestDemandSum / 4.0f; // 4.0 == number of states
        lowDemand = increment;
        averageDemand = increment * 2;
        mediumDemand = increment * 3;
        highDemand = increment * 4;
    }

    /**
     * Calculates the demand profiles of each building from the aggregate of their load profile and the storage demand profiles managed in the storage manager.
     * @param day day of the week
     * @param storageManager contains the storage demand profiles
     */
    public void calculateDemandProfiles(int day, StorageManager storageManager) {
        structureDemandProfiles.clear();

        for (Building structure : structures) {
            List<Float> loadProfile = structure.getLoadProfilesForWeek().get(day);
            List<Float> demandProfile = new ArrayList<>();

            int length = loadProfile.size();
            for (int time = 0; time < length; ++time) {
                float storageDemand = storageManager.getStructuresStorageDemandAtTime(structure, time);
                float loadDemand = loadProfile.get(time);
                float totalDemand = loadDemand + storageDemand;

                if (totalDemand < 0) // Storage device released energy is greater than the current load
                {
                    totalDemand = 0;
                }

                demandProfile.add(totalDemand);
            }

            structureDemandProfiles.put(structure.getId(), demandProfile);
        }
    }

    /**
     * Calculates how much the buildings owe for the electricity that they consumed during the day.
     * @param pricesForDay price of electricity at every minute of the day
     */
    public void calculateDaysExpenses(List<Float> pricesForDay) {
        for (Structure structure : structures) {
            List<Float> structuresDemandProfile = structureDemandProfiles.get(structure.getId());

            Float totalExpenses = structureExpenses.get(structure.getId());

            if (totalExpenses == null)
                totalExpenses = 0f;

            for (int time = 0; time < structuresDemandProfile.size(); ++time) {
                Float demandAtThisTime = structuresDemandProfile.get(time);
                Float priceAtThisTime = pricesForDay.get(time);

                Float newExpense = (demandAtThisTime / (1000 * TimeUnit.HOURS.toMinutes(1))) * priceAtThisTime;
                totalExpenses += newExpense;
            }

            structureExpenses.put(structure.getId(), totalExpenses);
        }
    }

    /**
     * Calculates how much of an environmental impact the buildings had during the day due to their electricity consumption.
     * Their environmental impact is a running sum of how much grams of green house gasses they are responsible for.
     * @param emissionsToday emissions produced per kWh during the day
     */
    public void calculateDaysEnvironmentalImpact(List<Float> emissionsToday) {

        for (Structure structure : structures) {
            List<Float> structuresDemandProfile = structureDemandProfiles.get(structure.getId());

            Float totalEmissions = structureEnvironmentalImpact.get(structure.getId());

            if (totalEmissions == null)
                totalEmissions = 0f;

            for (int time = 0; time < structuresDemandProfile.size(); ++time) {
                Float demandAtThisTime = structuresDemandProfile.get(time);
                Float emissionsAtTheTime = emissionsToday.get(time);

                Float newExpense = (demandAtThisTime / (1000 * TimeUnit.HOURS.toMinutes(1))) * emissionsAtTheTime;
                totalEmissions += newExpense;
            }

            structureEnvironmentalImpact.put(structure.getId(), totalEmissions);
        }
    }

    /**
     * Resets the day so that the demand manager is ready for the next.
     */
    public void resetDay() {
        demandProfileForToday.clear();
        dailyDemandProfileReady = false;
        processOverflowBuffer();
    }

    /**
     * If the timer ticked past midnight before the rest of the simulation could prepare for the next day, that extra time is kept in the time overflow buffer.
     * Here that time is processed so that the simulation can catch up.
     */
    private void processOverflowBuffer() {
        for (int time = 0; time < timeOverflow; ++time) {
            electricityDemand = 0;

            for (List<Float> demandProfile : structureDemandProfiles.values()) {
                electricityDemand += demandProfile.get(time);
            }

            demandProfileForToday.add((int) electricityDemand);
        }

        timeOverflow = 0;
    }

    /**
     * Calculated the demand at this moment in time base on the demand profiles of the buildings.
     * @param timeElapsedInSeconds the difference in seconds since the last update
     * @param totalTimeElapsedInSeconds the total number of seconds that have elapsed in the simulation
     */
    public void calculateDemand(double timeElapsedInSeconds, double totalTimeElapsedInSeconds) {
        int previousElapsedSecondsThisDay = (int) (Math.floor(totalTimeElapsedInSeconds - timeElapsedInSeconds) % TimeUnit.DAYS.toSeconds(1));
        int previousElapsedMinutesThisDay = (int) TimeUnit.SECONDS.toMinutes(previousElapsedSecondsThisDay);
        int totalElapsedMinutesThisDay = (int) TimeUnit.SECONDS.toMinutes((long) totalTimeElapsedInSeconds % TimeUnit.DAYS.toSeconds(1));


        if (previousElapsedMinutesThisDay > totalElapsedMinutesThisDay) {
            timeOverflow = totalElapsedMinutesThisDay;

            totalElapsedMinutesThisDay = (int) TimeUnit.DAYS.toMinutes(1);
        }

        for (int time = previousElapsedMinutesThisDay; time < totalElapsedMinutesThisDay; ++time) {
            electricityDemand = 0;

            for (List<Float> demandProfile : structureDemandProfiles.values()) {
                electricityDemand += demandProfile.get(time);
            }

            demandProfileForToday.add((int) electricityDemand);

            updateDemandStates(time);
        }


        if (demandProfileForToday.size() == TimeUnit.DAYS.toMinutes(1))
            dailyDemandProfileReady = true;
    }

    /**
     * Updates the states of buildings based on their energy demands.
     * @param minutesElapsedToday number of minutes elapsed today
     */
    private void updateDemandStates(int minutesElapsedToday) {
        for (Building structure : structures) {
            List<Float> demandProfile = structureDemandProfiles.get(structure.getId());
            float structuresDemandAtTime = demandProfile.get(minutesElapsedToday);

            if (structuresDemandAtTime <= lowDemand) {
                structure.setDemandState(DemandState.LOW);
            } else if (structuresDemandAtTime <= averageDemand) {
                structure.setDemandState(DemandState.AVERAGE);
            } else if (structuresDemandAtTime <= mediumDemand) {
                structure.setDemandState(DemandState.MEDIUM);
            } else if (structuresDemandAtTime <= highDemand) {
                structure.setDemandState(DemandState.HIGH);
            }
        }
    }

    /**
     * Resets all the buildings to their default state, low consumption.
     */
    public void resetDemandStates() {
        for (Building building : structures) {
            building.setDemandState(DemandState.LOW);
        }
    }

    /**
     * Resets the demand manager so that it's all ready for the simulation to run from the beginning again.
     */
    public void reset() {
        totalUsageInkWh = 0;
        structureExpenses.clear();
        structureEnvironmentalImpact.clear();
        timeOverflow = 0;
        resetDay();
    }

    /**
     * Removes a structure from the buildings being managed.
     * @param structureToRemove the structure to remove
     * @return true if the structure was found, false otherwise
     */
    public boolean removeStructure(Structure structureToRemove) {
        for (Structure structure : structures) {
            if (structure.getId() == structureToRemove.getId()) {
                structures.remove(structure);
                return true;
            }
        }

        return false;
    }

    /**
     * Either adds, removes, or updates a building that this manager should know about
     * @param changedStructure the building in question
     * @return true if the structure was or is being managed by this manager, false if it never was and isn't now
     */
    public boolean syncStructures(Building changedStructure) {
        int structureIndex = -1;

        for (int i = 0; i < structures.size(); ++i) {
            if (changedStructure.getId() == structures.get(i).getId()) {
                structureIndex = i;
            }
        }

        int applianceCount = changedStructure.getAppliances().size();

        if (structureIndex < 0 && applianceCount > 0) {
            structures.add(changedStructure);
        } else if (structureIndex >= 0) {
            if (applianceCount > 0) {
                structures.set(structureIndex, changedStructure);
            } else {
                structures.remove(structureIndex);
            }
        } else {
            return false;
        }

        return true;
    }

    public List<Float> getDemandProfile(Structure structure) {
        return structureDemandProfiles.get(structure.getId());
    }

    public double getTotalUsageInkWh() {
        return totalUsageInkWh;
    }

    public double getElectricityDemand() {
        return electricityDemand;
    }

    public List<Integer> getDemandProfileForToday() {
        return demandProfileForToday;
    }

    public List<Building> getStructures() {
        return structures;
    }

    public HashMap<Integer, Float> getStructureExpenses() {
        return structureExpenses;
    }

    public HashMap<Integer, Float> getStructureEnvironmentalImpact() {
        return structureEnvironmentalImpact;
    }

    public boolean isDailyDemandProfileReady() {
        return dailyDemandProfileReady;
    }
}

