package com.projects.systems.simulation;

import com.projects.helper.Constants;
import com.projects.models.*;

import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 5/27/2015.
 */
public class World extends com.projects.systems.System
{
    private Structure lastSelected;
    private HashMap<Integer, Structure> structures;
    public static final String PC_NEW_WORLD = "PC_NEW_WORLD";
    public static final String PC_NEW_STRUCTURE = "PC_NEW_STRUCTURE";
    public static final String PC_STRUCTURE_SELECTED = "PC_STRUCTURE_SELECTED";
    public static final String PC_REMOVE_STRUCTURE = "PC_REMOVE_STRUCTURE";
    public static final String PC_STRUCTURE_UPDATE = "PC_STRUCTURE_UPDATE";
    public static final String PC_WORLD_UPDATE = "PC_WORLD_UPDATE";
    public static final String PC_WORLD_RESET = "PC_WORLD_RESET";
    public static final String PC_SIMULATION_STARTED = "PC_SIMULATION_STARTED";
    public static final String PC_UPDATE_RATE_CHANGE = "PC_UPDATE_RATE_CHANGE";
    public static final String PC_PRICE_STATS_UPDATED = "PC_PRICE_STATS_UPDATED";
    public static final String PC_EMISSIONS_STATS_UPDATED = "PC_EMISSIONS_STATS_UPDATED";
    public static final String PC_DAILY_STATS_UPDATED = "PC_DAILY_STATS_UPDATED";
    public static final String PC_SELECTED_LOAD_PROFILE_CHANGED = "PC_SELECTED_LOAD_PROFILE_CHANGED";

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

    public World()
    {
        structures = new HashMap<Integer, Structure>();
        changeSupport = new PropertyChangeSupport(this);
        lastSelected = null;

        running = false;
        demandManager = new DemandManager();
        supplyManager = new SupplyManager();
        statsManager = new StatsManager();
        storageManager = new StorageManager();
        worldTimer = new WorldTimer();
        simulationStatus = new SimulationStatus();
        resetSimulation();
    }

    public void newWorld(List<Structure> structureList)
    {
        pauseSimulation();
        demandManager.removeAllStructures();
        supplyManager.removeAllStructures();
        storageManager.removeAllStructures();

        structures.clear();
        lastSelected = null;

        for (Structure structure : structureList)
        {
            updateStructure(structure);
        }
        resetSimulation();
        changeSupport.firePropertyChange(PC_NEW_WORLD, null, structureList);
    }

    public void postSetupSync()
    {
        //changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus); // TODO: wont work due to the date pickers being initialized in another thread, also this is a little messy with resetSimulation and all...
    }

    public void updateStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);

        if (demandManager.syncStructures(structure))
        {
            demandManager.calculateLoadProfiles();
            changeSupport.firePropertyChange(PC_SELECTED_LOAD_PROFILE_CHANGED, null, demandManager.getLoadProfile(structure));
        }

        storageManager.syncStructures(structure);

        if (supplyManager.syncStructures(structure))
        {
            statsManager.updatePriceAndEmissionsStats(supplyManager);
            changeSupport.firePropertyChange(PC_PRICE_STATS_UPDATED, null, statsManager.getPriceForDemand());
            changeSupport.firePropertyChange(PC_EMISSIONS_STATS_UPDATED, null, statsManager.getEmissionsForDemand());
        }

        changeSupport.firePropertyChange(PC_STRUCTURE_UPDATE, null, structure);
    }

    public void removeStructure(Integer id)
    {
        if (id == lastSelected.getId())
        {
            lastSelected = null;
        }

        demandManager.removeStructure(structures.get(id));
        supplyManager.removeStructure(structures.get(id));
        storageManager.removeStructure(structures.get(id));
        structures.remove(id);
        changeSupport.firePropertyChange(PC_REMOVE_STRUCTURE, null, id);
    }

    public void addNewStructure(Structure structure)
    {
        updateStructure(structure);
        changeSupport.firePropertyChange(PC_NEW_STRUCTURE, null, structure);
    }

    public void selectStructure(Structure structure)
    {
        lastSelected = structure;

        changeSupport.firePropertyChange(PC_STRUCTURE_SELECTED, null, structure);

        if (demandManager.isConsumer(structure))
            changeSupport.firePropertyChange(PC_SELECTED_LOAD_PROFILE_CHANGED, null, demandManager.getLoadProfile(structure));
    }

    public void setTimeLimit(Double timeLimit)
    {
        worldTimer.setTimeLimit(timeLimit);
    }

    public void runSimulation()
    {
        if (!running && !worldTimer.isTimeLimitReached())
        {
            demandManager.calculateDemandProfiles(storageManager);
            updateStatus();
            changeSupport.firePropertyChange(PC_SIMULATION_STARTED, null, simulationStatus);
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
            running = true;
        }
    }

    public void pauseSimulation()
    {
        if (running)
        {
            simulationHandle.cancel(true);
            running = false;
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
        changeSupport.firePropertyChange(PC_WORLD_RESET, null, simulationStatus);
    }

    public void changeUpdateRate(WorldTimer.UpdateRate updateRate)
    {
        worldTimer.setUpdateRate(updateRate);
        changeSupport.firePropertyChange(PC_UPDATE_RATE_CHANGE, null, updateRate);
    }

    private void tick()
    {

        worldTimer.tick(Constants.FIXED_SIMULATION_RATE_SECONDS);

        demandManager.calculateDemand(worldTimer.getModifiedTimeElapsedInSeconds(), worldTimer.getTotalTimeInSeconds());
        supplyManager.calculateSupply(demandManager.getElectricityDemand());
        updateStatus();

        changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus);

        if (demandManager.isDailyDemandProfileReady())
        {
            statsManager.resetDailyTrends();
            statsManager.logDailyTrends(demandManager.getTodaysDemandProfile());
            storageManager.reset();
            storageManager.updateStorageStrategies(demandManager, statsManager);
            demandManager.calculateDemandProfiles(storageManager);
            changeSupport.firePropertyChange(PC_DAILY_STATS_UPDATED, null, statsManager);
            demandManager.resetDay();
        }

        if (worldTimer.isTimeLimitReached())
        {
            pauseSimulation();
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

    public Structure getLastSelected() {
        return lastSelected;
    }

    public HashMap<Integer, Structure> getStructures()
    {
        return structures;
    }
}
