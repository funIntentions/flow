package com.projects.simulation;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.SimulationState;
import com.projects.model.Building;
import com.projects.model.Structure;
import com.projects.model.WorldTimer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
* Created by Dan on 5/27/2015.
*/
public class World
{
    private HashMap<Integer, Structure> structures;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Runnable simulationTick = new Runnable()
    {
        public void run()
        {
            tick();
        }
    };
    private ScheduledFuture<?> simulationHandle;

    private boolean running;
    private DemandManager demandManager;
    private SupplyManager supplyManager;
    private StatsManager statsManager;
    private StorageManager storageManager;
    private WorldTimer worldTimer;
    private SimulationStatus simulationStatus;
    private Main main;

    public World()
    {
        structures = new HashMap<Integer, Structure>();

        running = false;
        demandManager = new DemandManager();
        supplyManager = new SupplyManager();
        statsManager = new StatsManager();
        storageManager = new StorageManager();
        worldTimer = new WorldTimer();
        simulationStatus = new SimulationStatus();
    }

    public void updateStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);

        if (structure instanceof Building && demandManager.syncStructures((Building)structure))
        {
            if (structure.getId() == main.selectedWorldStructureProperty().get().getId())
            {
                main.getStructureDetailsPaneController().setStructureData(structure, ((Building) structure).getLoadProfilesForWeek());
            }
        }

        if (structure instanceof Building)
        {
            storageManager.syncStructures((Building)structure);
        }

        if (supplyManager.syncStructures(structure))
        {
            statsManager.updatePriceAndEmissionsStats(supplyManager);
            main.getProductionStatisticsController().setEmissionsForDemandData(statsManager.getEmissionsForDemand());
            main.getProductionStatisticsController().setPriceForDemandData(statsManager.getPriceForDemand());
        }
    }

    public void removeStructure(Integer id)
    {
        demandManager.removeStructure(structures.get(id));
        supplyManager.removeStructure(structures.get(id));
        storageManager.removeStructure(structures.get(id));
        structures.remove(id);
    }

    public void setStartDate(LocalDate startDate)
    {
        worldTimer.setStartDate(startDate);
    }

    public void setEndDate(LocalDate endDate)
    {
        worldTimer.setEndDate(endDate);
    }

    public void runSimulation()
    {
        if (!running && !worldTimer.isTimeLimitReached())
        {
            demandManager.calculateDemandStates(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1);
            demandManager.calculateDemandProfiles(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1, storageManager);
            updateStatus();
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
            running = true;

            main.simulationStateProperty().setValue(SimulationState.RUNNING);
        }
    }

    public void pauseSimulation()
    {
        if (running)
        {
            simulationHandle.cancel(true);
            running = false;

            main.simulationStateProperty().setValue(SimulationState.PAUSED);
        }
    }

    public void resetSimulation()
    {
        pauseSimulation();
        worldTimer.reset();
        demandManager.reset();
        storageManager.reset();
        statsManager.reset();
        updateStatus();

        main.simulationStateProperty().setValue(SimulationState.RESET);
    }

    public void changeUpdateRate(WorldTimer.UpdateRate updateRate)
    {
        worldTimer.setUpdateRate(updateRate);
    }

    private void tick()
    {
        worldTimer.tick(Constants.FIXED_SIMULATION_RATE_SECONDS);

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                main.currentTimeProperty().setValue(LocalTime.ofSecondOfDay((int) (worldTimer.getTotalTimeInSeconds() % TimeUnit.DAYS.toSeconds(1))));
                main.currentDateProperty().setValue(worldTimer.getCurrentDate());
            }
        });

        demandManager.calculateDemand(worldTimer.getModifiedTimeElapsedInSeconds(), worldTimer.getTotalTimeInSeconds());
        supplyManager.calculateSupply(demandManager.getElectricityDemand());
        supplyManager.updateProductionStates();

        updateStatus();

        if (demandManager.isDailyDemandProfileReady())
        {
            statsManager.resetDailyTrends();
            statsManager.logDailyTrends(demandManager.getDemandProfileForToday());
            demandManager.calculateDaysExpenses(statsManager.getDailyPriceTrends());
            demandManager.calculateDaysEnvironmentalImpact(statsManager.getDailyEmissionTrends());
            storageManager.updateStorageStrategies(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1, demandManager, statsManager);
            demandManager.calculateDemandStates(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1);
            demandManager.calculateDemandProfiles(worldTimer.getCurrentDate().getDayOfWeek().getValue() - 1, storageManager);
            demandManager.resetDay();
        }

        if (worldTimer.isTimeLimitReached())
        {
            pauseSimulation();
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    main.getStructureComparisonsController().displayResults(demandManager.getStructures(),
                            demandManager.getStructureExpenses(),
                            demandManager.getStructureEnvironmentalImpact());
                    statsManager.setStatsForDay(0);
                    demandManager.resetDemandStates();
                    supplyManager.resetProductionStates();

                    main.simulationStateProperty().setValue(SimulationState.FINISHED);
                }
            });
        }
    }

    private void updateStatus()
    {
        simulationStatus.worldTimer = worldTimer;
        simulationStatus.powerPlants = supplyManager.getPowerPlants();
        simulationStatus.price = supplyManager.getPrice();
        simulationStatus.totalUsageInkWh = demandManager.getTotalUsageInkWh();
        simulationStatus.electricityDemand = demandManager.getElectricityDemand();
        simulationStatus.emissions = supplyManager.getEmissions();
    }

    public HashMap<Integer, Structure> getStructures()
    {
        return structures;
    }

    public void setMain(Main main)
    {
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

        main.getWorldStructureData().addListener(new ListChangeListener<Structure>()
        {
            @Override
            public void onChanged(Change<? extends Structure> c)
            {
                while (c.next())
                {
                    if (c.wasPermutated())
                    {
                    }
                    else if (c.wasUpdated())
                    {
                    }
                    else
                    {
                        for (Structure removed : c.getRemoved())
                        {
                            removeStructure(removed.getId());
                        }
                        for (Structure added : c.getAddedSubList())
                        {
                            updateStructure(added);
                        }
                    }
                }
            }
        });
    }
}
