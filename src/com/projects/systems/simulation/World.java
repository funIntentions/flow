package com.projects.systems.simulation;

import com.projects.models.*;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * Created by Dan on 5/27/2015.
 */
public class World extends com.projects.systems.System
{
    private int selectedInstance;
    private Structure lastSelected;
    private HashMap<Integer, Structure> structures;
    public static final String PC_NEW_STRUCTURE = "PC_NEW_STRUCTURE";
    public static final String PC_STRUCTURE_SELECTED = "PC_STRUCTURE_SELECTED";
    public static final String PC_REMOVE_STRUCTURE = "PC_REMOVE_STRUCTURE";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";


    public World()
    {
        selectedInstance = -1;
        structures = new HashMap<Integer, Structure>();
        changeSupport = new PropertyChangeSupport(this);
        lastSelected = null;
    }

    public void clearWorld()
    {
        selectedInstance = -1;
        changeSupport.firePropertyChange(PC_WORLD_CLEARED, null, null);
    }

    public void setStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
    }

    public void removeStructure(Integer id)
    {
        if (id == lastSelected.getId())
        {
            lastSelected = null;
        }

        structures.remove(id);
    }

    public void addNewStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
        changeSupport.firePropertyChange(PC_NEW_STRUCTURE, null, structure);
    }

    public void selectStructure(Structure structure)
    {
        lastSelected = structure;
        changeSupport.firePropertyChange(PC_STRUCTURE_SELECTED, null, structure);
    }

    public Structure getLastSelected() {
        return lastSelected;
    }
}
