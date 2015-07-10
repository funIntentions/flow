package com.projects.systems.simulation;

import com.projects.helper.Constants;
import com.projects.models.*;

import java.beans.PropertyChangeSupport;
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
    public static final String PC_SIMULATION_STARTED = "PC_SIMULATION_STARTED";

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
    private ConsumptionManager consumptionManager;
    private ProductionManager productionManager;
    private WorldTimer worldTimer;
    private SimulationStatus simulationStatus;

    public World()
    {
        structures = new HashMap<Integer, Structure>();
        changeSupport = new PropertyChangeSupport(this);
        lastSelected = null;

        running = false;
        consumptionManager = new ConsumptionManager();
        productionManager = new ProductionManager();
        worldTimer = new WorldTimer();
        simulationStatus = new SimulationStatus();
        resetSimulation();
    }

    public void newWorld(List<Structure> structureList)
    {
        pauseSimulation();
        resetSimulation();
        consumptionManager.removeAllStructures();
        productionManager.removeAllStructures();

        structures.clear();
        lastSelected = null;

        for (Structure structure : structureList)
        {
            updateStructure(structure);
        }
        changeSupport.firePropertyChange(PC_NEW_WORLD, null, structureList);
    }

    public void postSetupSync()
    {
        //changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus); // TODO: wont work due to the date pickers being initialized in another thread, also this is a little messy with resetSimulation and all...
    }

    public void updateStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
        consumptionManager.syncStructures(structure);
        productionManager.syncStructures(structure);
        changeSupport.firePropertyChange(PC_STRUCTURE_UPDATE, null, structure);
    }

    public void removeStructure(Integer id)
    {
        if (id == lastSelected.getId())
        {
            lastSelected = null;
        }

        consumptionManager.removeStructure(structures.get(id));
        productionManager.removeStructure(structures.get(id));
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
    }

    public void setTimeLimit(Double timeLimit)
    {
        worldTimer.setTimeLimit(timeLimit);
    }

    public void runSimulation()
    {
        if (!running)
        {
            changeSupport.firePropertyChange(PC_SIMULATION_STARTED, null, null);
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
            running = true;
        }
    }

    public void pauseSimulation()
    {
        if (running)
            simulationHandle.cancel(true);
        running = false;
    }

    public void resetSimulation()
    {
        worldTimer.reset();
        consumptionManager.reset();
        simulationStatus.totalUsageInkWh = 0;
        simulationStatus.priceOfProduction = 0;
        simulationStatus.worldTimer = worldTimer;
        simulationStatus.emissions = 0;
        changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus);
    }

    public void changeUpdateRate(WorldTimer.UpdateRate updateRate)
    {
        worldTimer.setUpdateRate(updateRate);
    }

    private void tick()
    {
        worldTimer.tick(Constants.FIXED_SIMULATION_RATE_SECONDS);
        consumptionManager.calculateConsumption(worldTimer.getModifiedTimeElapsedInSeconds(), worldTimer.getTotalTimeInSeconds());
        productionManager.calculateProduction(consumptionManager.getTotalUsageInkWh());

        simulationStatus.worldTimer = worldTimer;
        simulationStatus.priceOfProduction = productionManager.getPriceOfProduction();
        simulationStatus.totalUsageInkWh = consumptionManager.getTotalUsageInkWh();
        simulationStatus.emissions = productionManager.getEmissions();

        changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus);

        if (worldTimer.isTimeLimitReached())
        {
            pauseSimulation();
        }
    }

    public Structure getLastSelected() {
        return lastSelected;
    }

    public HashMap<Integer, Structure> getStructures()
    {
        return structures;
    }
}
