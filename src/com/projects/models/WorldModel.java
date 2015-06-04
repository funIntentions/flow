package com.projects.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * Created by Dan on 5/27/2015.
 */
public class WorldModel
{
    //TODO: added the ability to change the data of selected instances and have it stored
    //TODO: add prefabs
    int selectedInstance;
    ArrayList<IndividualModel> instances;
    PropertyChangeSupport changeSupport;
    public static final String PC_NEW_INSTANCE_ADDED = "PC_NEW_INSTANCE_ADDED";
    public static final String PC_INSTANCE_DELETED = "PC_INSTANCE_DELETED";
    public static final String PC_NEW_INSTANCE_SELECTED = "PC_NEW_INSTANCE_SELECTED";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";

    public WorldModel()
    {
        selectedInstance = -1;
        instances = new ArrayList<IndividualModel>();
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

        IndividualModel newIndividual = new IndividualModel(individual);
        instances.add(newIndividual);
        changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED, null, newIndividual);
    }

    public void deleteInstance(int index)
    {
        if (index < 0)
            return;

        instances.remove(index);
        changeSupport.firePropertyChange(PC_INSTANCE_DELETED, null, index);

        if (index == selectedInstance)
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

    public void changeSelectedInstance(int index)
    {
        IndividualModel previouslySelected = null;

        if (selectedInstance >= 0)
            previouslySelected = instances.get(selectedInstance);

        selectedInstance = index;
        changeSupport.firePropertyChange(PC_NEW_INSTANCE_SELECTED, previouslySelected, instances.get(selectedInstance));
    }

    public int getSelectedInstance() {return selectedInstance; }

    public IndividualModel getInstance(int index)
    {
        return instances.get(index);
    }
}
