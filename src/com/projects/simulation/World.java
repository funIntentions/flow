package com.projects.simulation;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.SimulationState;
import com.projects.model.Building;
import com.projects.model.PowerPlant;
import com.projects.model.Structure;
import com.projects.model.WorldTimer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controls the simulation and the managers.
 */
public class World {

    /**
     * Used to neatly package the simulation's status at a moment in time.
     */
    public class SimulationStatus {
        public int dayOfTheWeek = 0; // mon = 0, tue, wed, thur, fri, sat, sun
        public List<Building> buildings = new ArrayList<>();
        public List<PowerPlant> powerPlants = new ArrayList<>();
        public HashMap<Integer, List<Float>> previousStorageProfiles; // Storage profiles of the last day. Storage Device Id's are the key
        public List<Float> priceForDemand; // Prices versus demand. The $/kWh it cost to produce 8000 watts == priceForDemand.get(8000)
        public List<Float> emissionsForDemand; // Emission rate versus demand. The g/kWh it cost to produce 8000 watts == priceForDemand.get(8000)
        public List<List<Float>> dailyDemandTrends; // Demand versus time data. The watts of demand on the 3rd day of the simulation at 30 minutes == dailyDemandTrends.get(3).get(30)
        public List<List<Float>> dailyPriceTrends; //  Price versus time data. The $/kWh cost for energy on the 3rd day of the simulation at 30 minutes == dailyPriceTrends.get(3).get(30)
        public List<List<Float>> dailyEmissionTrends; //  Emission rate versus time data. The g/kWh cost produced to meet demand on the 3rd day of the simulation at 30 minutes == dailyPriceTrends.get(3).get(30)
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Runnable simulationTick = World.this::tick;
    private HashMap<Integer, Structure> structures;
    private ScheduledFuture<?> simulationHandle;

    private boolean running;
    private DemandManager demandManager;
    private SupplyManager supplyManager;
    private StatsManager statsManager;
    private StorageManager storageManager;
    private WorldTimer worldTimer;
    private SimulationStatus simulationStatus;
    private Main main;

    /**
     * World constructor.
     */
    public World() {
        structures = new HashMap<>();

        running = false;
        demandManager = new DemandManager();
        supplyManager = new SupplyManager();
        statsManager = new StatsManager();
        storageManager = new StorageManager();
        worldTimer = new WorldTimer();
        simulationStatus = new SimulationStatus();
    }

    /**
     * Syncs a structure with any managers that might need to know about it.
     * @param structure the structure to sync
     */
    public void updateStructure(Structure structure) {
        structures.put(structure.getId(), structure);

        if (structure instanceof Building) {
            demandManager.syncStructures((Building) structure);
        }

        if (structure instanceof Building) {
            storageManager.syncStructures((Building) structure);
        }

        if (supplyManager.syncStructures(structure)) {
            statsManager.updatePriceAndEmissionsStats(supplyManager);
            main.getProductionStatisticsController().setEmissionsForDemandData(statsManager.getEmissionsForDemandData());
            main.getProductionStatisticsController().setPriceForDemandData(statsManager.getPriceForDemandData());
        }
    }

    /**
     * Removes a structure from the world.
     * @param id structure's id
     */
    public void removeStructure(Integer id) {
        demandManager.removeStructure(structures.get(id));
        if (supplyManager.removeStructure(structures.get(id))) {
            statsManager.updatePriceAndEmissionsStats(supplyManager);
            main.getProductionStatisticsController().setEmissionsForDemandData(statsManager.getEmissionsForDemandData());
            main.getProductionStatisticsController().setPriceForDemandData(statsManager.getPriceForDemandData());
        }
        storageManager.removeStructure(structures.get(id));
        structures.remove(id);
    }

    /**
     * Starts the simulation.
     */
    public void runSimulation() {
        if (!running && !worldTimer.isTimeLimitReached()) {
            demandManager.calculateDemandStates(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1);
            demandManager.calculateDemandProfiles(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1, storageManager);
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
            running = true;

            main.simulationStateProperty().setValue(SimulationState.RUNNING);
        }
    }

    /**
     * Pauses the simulation.
     */
    public void pauseSimulation() {
        if (running) {
            simulationHandle.cancel(true);
            running = false;

            main.simulationStateProperty().setValue(SimulationState.PAUSED);
        }
    }

    /**
     * Resets the simulation.
     */
    public void resetSimulation() {
        pauseSimulation();
        worldTimer.reset();
        demandManager.reset();
        storageManager.reset();
        statsManager.reset();

        main.simulationStateProperty().setValue(SimulationState.RESET);
    }

    /**
     * Captures the simulations state in this moment of time.
     */
    private void updateSimulationStatus() {
        simulationStatus.dayOfTheWeek = worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1;
        simulationStatus.buildings = demandManager.getStructures();
        simulationStatus.powerPlants = supplyManager.getPowerPlants();
        simulationStatus.previousStorageProfiles = storageManager.getDeviceStorageProfiles();
        simulationStatus.priceForDemand = statsManager.getPriceForDemandData();
        simulationStatus.emissionsForDemand = statsManager.getPriceForDemandData();
        simulationStatus.dailyDemandTrends = statsManager.getDailyDemandTrends();
        simulationStatus.dailyPriceTrends = statsManager.getDailyPriceTrends();
        simulationStatus.dailyEmissionTrends = statsManager.getDailyEmissionTrends();
    }

    /**
     * Sets the rate which the simulation updates at.
     * @param updateRate the new rate
     */
    public void changeUpdateRate(WorldTimer.UpdateRate updateRate) {
        worldTimer.setUpdateRate(updateRate);
    }

    /**
     * Advances the simulation by some unit of time determined by it's update rate.
     */
    private void tick() {
        worldTimer.tick(Constants.FIXED_SIMULATION_RATE_SECONDS);

        Platform.runLater(() -> {
            main.currentTimeProperty().setValue(LocalTime.ofSecondOfDay((int) (worldTimer.getTotalTimeInSeconds() % TimeUnit.DAYS.toSeconds(1))));
            main.currentDateProperty().setValue(worldTimer.getCurrentDate());
        });

        demandManager.calculateDemand(worldTimer.getModifiedTimeElapsedInSeconds(), worldTimer.getTotalTimeInSeconds());
        supplyManager.calculateSupply(demandManager.getElectricityDemand());
        supplyManager.updateProductionStates();

        if (demandManager.isDailyDemandProfileReady()) {
            statsManager.logDailyTrends(demandManager.getDemandProfileForToday());
            demandManager.calculateDaysExpenses(statsManager.getDailyPriceTrendsForToday());
            demandManager.calculateDaysEnvironmentalImpact(statsManager.getDailyEmissionTrendsForToday());

            updateSimulationStatus();
            storageManager.updateStorageStrategies(simulationStatus);

            if (!storageManager.errorEncountered()) {
                demandManager.calculateDemandStates(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1);
                demandManager.calculateDemandProfiles(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1, storageManager);
                demandManager.resetDay();
            } else {
                pauseSimulation();
                resetSimulation();
            }
        }

        if (worldTimer.isTimeLimitReached()) {
            pauseSimulation();
            Platform.runLater(() -> {
                main.getBuildingDetailsController().displayResults(demandManager.getStructures(),
                        demandManager.getStructureExpenses(),
                        demandManager.getStructureEnvironmentalImpact());
                statsManager.setStatsForDay(0);
                demandManager.resetDemandStates();
                supplyManager.resetProductionStates();

                main.simulationStateProperty().setValue(SimulationState.FINISHED);
            });
        }
    }

    /**
     * Provides a reference to main for the world and all the managers.
     * @param main a reference to main
     */
    public void setMain(Main main) {
        this.main = main;
        demandManager.setMain(main);
        storageManager.setMain(main);
        supplyManager.setMain(main);
        statsManager.setMain(main);

        main.updateRateProperty().addListener((observable, oldValue, newValue) ->
        {
            changeUpdateRate(newValue);
        });

        main.startDateProperty().addListener((observable, oldValue, newValue) ->
        {
            setStartDate(newValue);
        });

        main.endDateProperty().addListener((observable, oldValue, newValue) ->
        {
            setEndDate(newValue);
        });

        main.getWorldStructureData().addListener((ListChangeListener<Structure>) c -> {
            while (c.next()) {
                for (Structure removed : c.getRemoved()) {
                    removeStructure(removed.getId());
                }
                for (Structure added : c.getAddedSubList()) {
                    updateStructure(added);
                }
            }
        });
    }

    public void setStartDate(LocalDate startDate) {
        worldTimer.setStartDate(startDate);
    }

    public void setEndDate(LocalDate endDate) {
        worldTimer.setEndDate(endDate);
    }
}
