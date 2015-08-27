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
 * Created by Dan on 5/27/2015.
 */
public class World {

    public class SimulationStatus {
        public int dayOfTheWeek = 0;
        public List<Building> buildings = new ArrayList<>();
        public List<PowerPlant> powerPlants = new ArrayList<>();
        public HashMap<Integer, List<Float>> previousStorageProfiles;
        public List<Float> priceForDemand;
        public List<Float> emissionsForDemand;
        public List<List<Float>> dailyDemandTrends;
        public List<List<Float>> dailyPriceTrends;
        public List<List<Float>> dailyEmissionTrends;
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

    public void setStartDate(LocalDate startDate) {
        worldTimer.setStartDate(startDate);
    }

    public void setEndDate(LocalDate endDate) {
        worldTimer.setEndDate(endDate);
    }

    public void runSimulation() {
        if (!running && !worldTimer.isTimeLimitReached()) {
            demandManager.calculateDemandStates(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1);
            demandManager.calculateDemandProfiles(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1, storageManager);
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
            running = true;

            main.simulationStateProperty().setValue(SimulationState.RUNNING);
        }
    }

    public void pauseSimulation() {
        if (running) {
            simulationHandle.cancel(true);
            running = false;

            main.simulationStateProperty().setValue(SimulationState.PAUSED);
        }
    }

    public void resetSimulation() {
        pauseSimulation();
        worldTimer.reset();
        demandManager.reset();
        storageManager.reset();
        statsManager.reset();

        main.simulationStateProperty().setValue(SimulationState.RESET);
    }

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

    public void changeUpdateRate(WorldTimer.UpdateRate updateRate) {
        worldTimer.setUpdateRate(updateRate);
    }

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
            statsManager.resetDailyTrends();
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
                main.getStructureDetailsPaneController().displayResults(demandManager.getStructures(),
                        demandManager.getStructureExpenses(),
                        demandManager.getStructureEnvironmentalImpact());
                statsManager.setStatsForDay(0);
                demandManager.resetDemandStates();
                supplyManager.resetProductionStates();

                main.simulationStateProperty().setValue(SimulationState.FINISHED);
            });
        }
    }

    public HashMap<Integer, Structure> getStructures() {
        return structures;
    }

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
                if (c.wasPermutated()) {
                } else if (c.wasUpdated()) {
                } else {
                    for (Structure removed : c.getRemoved()) {
                        removeStructure(removed.getId());
                    }
                    for (Structure added : c.getAddedSubList()) {
                        updateStructure(added);
                    }
                }
            }
        });
    }
}
