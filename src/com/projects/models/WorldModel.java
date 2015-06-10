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
    HashMap<Integer, IndividualModel> prefabInstances;
    HashMap<Integer, Prefab> prefabs;
    PropertyChangeSupport changeSupport;
    public static final String PC_NEW_INSTANCE_ADDED = "PC_NEW_INSTANCE_ADDED";
    public static final String PC_INSTANCE_DELETED = "PC_INSTANCE_DELETED";
    public static final String PC_PREFAB_DELETED = "PC_PREFAB_DELETED";
    public static final String PC_NEW_INSTANCE_SELECTED = "PC_NEW_INSTANCE_SELECTED";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";
    public static final String PC_NEW_INSTANCE_ADDED_FROM_PREFAB = "PC_NEW_INSTANCE_ADDED_FROM_PREFAB";

    private static Integer getNextAvailableInstanceId()
    {
        return nextAvailableInstanceId++;
    }

    private static Integer getNextAvaibablePrefabInstanceId()
    {
        return nextAvailablePrefabInstance++;
    }

    public WorldModel()
    {
        selectedInstance = -1;
        instances = new HashMap<Integer, IndividualModel>();
        prefabInstances = new HashMap<Integer, IndividualModel>();
        prefabs = new HashMap<Integer, Prefab>();
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
        IndividualModel selected = getInstance(selectedInstance);
        selected.changeProperty(index, newValue);
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

        Prefab newPrefab = new Prefab(getNextAvaibablePrefabInstanceId(), prefab.getName(), prefab.getMemberSuffix(), instanceMembers);
        prefabs.put(newPrefab.getId(), newPrefab);

        changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED_FROM_PREFAB, null, newPrefab);
    }

    public IndividualModel addNewInstance(IndividualModel individual, Boolean prefabMember)
    {
        if (individual == null)
            return null;

        IndividualModel newIndividual = new IndividualModel(getNextAvailableInstanceId(), individual);

        if (!prefabMember)
        {
            instances.put(newIndividual.getId(), newIndividual);
            changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED, null, newIndividual);
        }
        else
        {
            prefabInstances.put(newIndividual.getId(), newIndividual);
        }

        return newIndividual;
    }

    public void removeIndividual(IndividualModel individual)
    {
        if (instances.containsKey(individual.getId()))
        {
            instances.remove(individual.getId());
            changeSupport.firePropertyChange(PC_INSTANCE_DELETED, null, individual.getId());
        }
        else if (prefabInstances.containsKey(individual.getId()))
        {
            prefabInstances.remove(individual.getId());

            for (Prefab prefab : prefabs.values())
            {
                if (prefab.getMembers().contains(individual))
                {
                    prefab.getMembers().remove(individual);
                    changeSupport.firePropertyChange(PC_INSTANCE_DELETED, null, individual.getId());
                    break;
                }
            }
        }
    }

    public void removePrefab(Prefab prefab)
    {
        for (IndividualModel individualModel : prefab.getMembers())
        {
           prefabInstances.remove(individualModel.getId());
        }

        prefabs.remove(prefab.getId());
        changeSupport.firePropertyChange(PC_PREFAB_DELETED, null, prefab.getId());
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
        if (id < 0)
            return;

        IndividualModel selected;

        if (prefabInstances.containsKey(id))
        {
            selected = prefabInstances.get(id);
        }
        else
        {
            selected = instances.get(id);
        }

        selectedInstance = id;
        changeSupport.firePropertyChange(PC_NEW_INSTANCE_SELECTED, null, selected);
    }

    public int getSelectedInstance() {return selectedInstance; }

    public IndividualModel getInstance(int id)
    {
        if (id < 0)
            return null;

        if (prefabInstances.containsKey(id))
        {
            return prefabInstances.get(id);
        }
        else
        {
            return instances.get(id);
        }
    }
}
