package com.projects.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dan on 5/27/2015.
 */
public class WorldModel
{
    //TODO: added the ability to change the data of selected instances and have it stored
    //TODO: add prefabs
    private static Integer nextAvailableInstanceId = 0;
    int selectedInstance;
    HashMap<Integer, IndividualModel> instances;
    PropertyChangeSupport changeSupport;
    public static final String PC_NEW_INSTANCE_ADDED = "PC_NEW_INSTANCE_ADDED";
    public static final String PC_INSTANCE_DELETED = "PC_INSTANCE_DELETED";
    public static final String PC_NEW_INSTANCE_SELECTED = "PC_NEW_INSTANCE_SELECTED";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";

    private static Integer getNextAvailableInstanceId()
    {
        return nextAvailableInstanceId++;
    }

    public WorldModel()
    {
        selectedInstance = -1;
        instances = new HashMap<Integer, IndividualModel>();
        changeSupport = new PropertyChangeSupport(this);
    }

    public void clearWorld()
    {
        instances.clear();
        selectedInstance = -1;
        changeSupport.firePropertyChange(PC_WORLD_CLEARED, null, null);
    }

    public void changePropertyValueOfSelected(int index, Object newValue)
    {
        if (index < 0)
            return;

        instances.get(selectedInstance).changeProperty(index, newValue);
    }

    public void addNewInstance(IndividualModel individual)
    {
        if (individual == null)
            return;

        IndividualModel newIndividual = new IndividualModel(getNextAvailableInstanceId(), individual);
        instances.put(newIndividual.getId(), newIndividual);
        changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED, null, newIndividual);
    }

    public void deleteInstance(int id)
    {
        if (id < 0)
            return;

        instances.remove(id);
        changeSupport.firePropertyChange(PC_INSTANCE_DELETED, null, id);

        if (id == selectedInstance)
            selectedInstance = -1;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.removePropertyChangeListener(l);
    }

    public void changeSelectedInstance(int id)
    {
        IndividualModel previouslySelected = null;

        if (selectedInstance >= 0)
            previouslySelected = instances.get(selectedInstance);

        selectedInstance = id;
        changeSupport.firePropertyChange(PC_NEW_INSTANCE_SELECTED, previouslySelected, instances.get(selectedInstance));
    }

    public int getSelectedInstance() {return selectedInstance; }

    public IndividualModel getInstance(int id)
    {
        return instances.get(id);
    }
}
