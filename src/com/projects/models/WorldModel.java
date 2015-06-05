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
    private static Integer nextAvailableInstanceId = 0;
    private static Integer nextAvailablePrefabInstance = 0;
    int selectedInstance;
    HashMap<Integer, IndividualModel> instances;
    HashMap<Integer, Prefab> prefabInstances;
    PropertyChangeSupport changeSupport;
    public static final String PC_NEW_INSTANCE_ADDED = "PC_NEW_INSTANCE_ADDED";
    public static final String PC_INSTANCE_DELETED = "PC_INSTANCE_DELETED";
    public static final String PC_NEW_INSTANCE_SELECTED = "PC_NEW_INSTANCE_SELECTED";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";
    public static final String PC_NEW_INSTANCE_ADDED_FROM_PREFAB = "PC_NEW_INSTANCE_ADDED_FROM_PREFAB";

    private static Integer getNextAvailableInstanceId()
    {
        return nextAvailableInstanceId += 2;
    }

    private static Integer getNextAvaibablePrefabInstanceId()
    {
        return nextAvailablePrefabInstance++;
    }

    public WorldModel()
    {
        selectedInstance = -1;
        instances = new HashMap<Integer, IndividualModel>();
        prefabInstances = new HashMap<Integer, Prefab>();
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

    public void addNewPrefab(Prefab prefab)
    {
        if (prefab == null)
            return;

        List<IndividualModel> instanceMembers = new ArrayList<IndividualModel>();
        List<IndividualModel> individualModels = prefab.getMembers();

        for (IndividualModel model : individualModels)
        {
            instanceMembers.add(addNewInstance(model, true));
        }

        Prefab newPrefab = new Prefab(getNextAvaibablePrefabInstanceId(), prefab.getName(), instanceMembers);
        prefabInstances.put(newPrefab.getId(), newPrefab);

        changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED_FROM_PREFAB, null, newPrefab);
    }

    public IndividualModel addNewInstance(IndividualModel individual, Boolean prefabMember)
    {
        if (individual == null)
            return null;

        IndividualModel newIndividual = new IndividualModel(getNextAvailableInstanceId(), individual);
        instances.put(newIndividual.getId(), newIndividual);

        if (!prefabMember)
            changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED, null, newIndividual);

        return newIndividual;
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
