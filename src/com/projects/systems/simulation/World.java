package com.projects.systems.simulation;

import com.projects.helper.Constants;
import com.projects.models.*;
import com.projects.systems.*;

import java.beans.PropertyChangeSupport;
import java.lang.System;
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
    private int selectedInstance;
    private Structure lastSelected;
    private HashMap<Integer, Structure> structures;
    public static final String PC_NEW_WORLD = "PC_NEW_WORLD";
    public static final String PC_NEW_STRUCTURE = "PC_NEW_STRUCTURE";
    public static final String PC_STRUCTURE_SELECTED = "PC_STRUCTURE_SELECTED";
    public static final String PC_REMOVE_STRUCTURE = "PC_REMOVE_STRUCTURE";
    public static final String PC_STRUCTURE_UPDATE = "PC_STRUCTURE_UPDATE";
    public static final String PC_WORLD_UPDATE = "PC_WORLD_UPDATE";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Runnable simulationTick = new Runnable()
    {
        public void run()
        {
            tick(Constants.FIXED_SIMULATION_RATE_SECONDS);
        }
    };
    private ScheduledFuture<?> simulationHandle;

    private boolean running;
    private ConsumptionManager consumptionManager;
    private ProductionManager productionManager;
    private Time time;
    private SimulationStatus simulationStatus;

    public World()
    {
        selectedInstance = -1;
        structures = new HashMap<Integer, Structure>();
        changeSupport = new PropertyChangeSupport(this);
        lastSelected = null;

        running = false;
        consumptionManager = new ConsumptionManager();
        productionManager = new ProductionManager();
        time = new Time();
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

    public void updateStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
        consumptionManager.syncStructures(structure);
        productionManager.syncStructures(structure);
        changeSupport.firePropertyChange(PC_STRUCTURE_UPDATE, null, structure);
    }

    public void removeStructure(Integer id)
    {
        if (id == lastSelected.getId().intValue())
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

    public void runSimulation()
    {
        if (!running)
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
        running = true;
    }

    public void pauseSimulation()
    {
        if (running)
            simulationHandle.cancel(true);
        running = false;
    }

    public void resetSimulation()
    {
        time.reset();
        consumptionManager.reset();
        simulationStatus.totalUsageInkWh = 0;
        simulationStatus.priceOfProduction = 0;
        simulationStatus.time = time;
        simulationStatus.emissions = 0;
        changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus);
    }

    public void changeUpdateRate(Time.UpdateRate updateRate)
    {
        time.setUpdateRate(updateRate);
    }

    private void tick(double fixedTime)
    {
        time.tick(fixedTime);
        consumptionManager.calculateConsumption(time.getModifiedTimeElapsedInSeconds(), time.getTotalTimeInSeconds() - (time.getDay() * Time.SECONDS_IN_DAY));
        productionManager.calculateProduction(consumptionManager.getTotalUsageInkWh());

        simulationStatus.time = time;
        simulationStatus.priceOfProduction = productionManager.getPriceOfProduction();
        simulationStatus.totalUsageInkWh = consumptionManager.getTotalUsageInkWh();
        simulationStatus.emissions = productionManager.getEmissions();

        changeSupport.firePropertyChange(PC_WORLD_UPDATE, null, simulationStatus);
    }

    public Structure getLastSelected() {
        return lastSelected;
    }

    public HashMap<Integer, Structure> getStructures()
    {
        return structures;
    }
}
