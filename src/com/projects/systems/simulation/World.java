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
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";
    public static final String PC_WORLD_TIME_UPDATE = "PC_WORLD_TIME_UPDATE";

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
    private Time time;

    public World()
    {
        selectedInstance = -1;
        structures = new HashMap<Integer, Structure>();
        changeSupport = new PropertyChangeSupport(this);
        lastSelected = null;

        running = false;
        consumptionManager = new ConsumptionManager();
        time = new Time();
        resetSimulation();
    }

    public void newWorld(List<Structure> structureList)
    {
        structures.clear();
        lastSelected = null;

        for (Structure structure : structureList)
        {
            setStructure(structure);
        }

        changeSupport.firePropertyChange(PC_NEW_WORLD, null, structureList);
    }

    public void setStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
        consumptionManager.syncStructures(structure);
    }

    public void removeStructure(Integer id)
    {
        if (id == lastSelected.getId().intValue())
        {
            lastSelected = null;
        }

        consumptionManager.removeStructure(structures.get(id));
        structures.remove(id);
    }

    public void addNewStructure(Structure structure)
    {
        setStructure(structure);
        changeSupport.firePropertyChange(PC_NEW_STRUCTURE, null, structure);
    }

    public void selectStructure(Structure structure)
    {
        lastSelected = structure;
        changeSupport.firePropertyChange(PC_STRUCTURE_SELECTED, null, structure);
    }

    public void runSimulation()
    {
        System.out.println("--- Run ---");
        if (!running)
            simulationHandle = scheduler.scheduleAtFixedRate(simulationTick, 0, Constants.FIXED_SIMULATION_RATE_MILLISECONDS, TimeUnit.MILLISECONDS);
        running = true;
    }

    public void pauseSimulation()
    {
        System.out.println("--- Pause ---");
        if (running)
            simulationHandle.cancel(true);
        running = false;
    }

    public void resetSimulation()
    {
        time.reset();
        changeSupport.firePropertyChange(PC_WORLD_TIME_UPDATE, null, time);
    }

    public void changeUpdateRate(Time.UpdateRate updateRate)
    {
        time.setUpdateRate(updateRate);
    }

    private void tick(double fixedTime)
    {
        time.tick(fixedTime);
        System.out.println("+++ Tick +++");
        System.out.println(consumptionManager.calculateConsumption(fixedTime));
        changeSupport.firePropertyChange(PC_WORLD_TIME_UPDATE, null, time);
    }

    public Structure getLastSelected() {
        return lastSelected;
    }

    public HashMap<Integer, Structure> getStructures()
    {
        return structures;
    }
}
