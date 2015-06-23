package com.projects.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dan on 5/27/2015.
 */
public class WorldModel
{
    private int selectedInstance;
    private HashMap<String, Integer> individualCount;
    private HashMap<Integer, Structure> structures;
    private HashMap<String, Integer> prefabCount;
    private PropertyChangeSupport changeSupport;
    public static final String PC_NEW_STRUCTURE = "PC_NEW_STRUCTURE";
    public static final String PC_STRUCTURE_SELECTED = "PC_STRUCTURE_SELECTED";
    public static final String PC_EDIT_STRUCTURE = "PC_EDIT_STRUCTURE";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";


    public WorldModel()
    {
        selectedInstance = -1;
        structures = new HashMap<Integer, Structure>();
        changeSupport = new PropertyChangeSupport(this);
        prefabCount = new HashMap<String, Integer>();
        individualCount = new HashMap<String, Integer>();
    }

    public void clearWorld()
    {
        selectedInstance = -1;
        changeSupport.firePropertyChange(PC_WORLD_CLEARED, null, null);
    }

    public void changePropertyValueOfSelected(int index, Object newValue)
    {
    }

    public void addNewStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
        changeSupport.firePropertyChange(PC_NEW_STRUCTURE, null, structure);
    }


    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.removePropertyChangeListener(l);
    }

    public void selectStructure(Structure structure)
    {
        changeSupport.firePropertyChange(PC_STRUCTURE_SELECTED, null, structure);
    }

    public void editStructure(Structure structure)
    {
        changeSupport.firePropertyChange(PC_EDIT_STRUCTURE, null, structure);
    }
}
