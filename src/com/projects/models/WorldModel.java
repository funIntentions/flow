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
    private int selectedInstance;
    private HashMap<Integer, IndividualModel> instances;
    private HashMap<String, Integer> individualCount;
    private HashMap<Integer, IndividualModel> prefabInstances;
    private HashMap<Integer, Prefab> prefabs;
    private HashMap<String, Integer> prefabCount;
    private PropertyChangeSupport changeSupport;
    public static final String PC_NEW_INSTANCE_ADDED = "PC_NEW_INSTANCE_ADDED";
    public static final String PC_INSTANCE_DELETED = "PC_INSTANCE_DELETED";
    private static final String PC_PREFAB_DELETED = "PC_PREFAB_DELETED";
    public static final String PC_NEW_INSTANCE_SELECTED = "PC_NEW_INSTANCE_SELECTED";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";
    public static final String PC_NEW_INSTANCE_ADDED_FROM_PREFAB = "PC_NEW_INSTANCE_ADDED_FROM_PREFAB";

    private static Integer getNextAvailableInstanceId()
    {
        return nextAvailableInstanceId++;
    }

    private static Integer getNextAvailablePrefabInstanceId()
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
        prefabCount = new HashMap<String, Integer>();
        individualCount = new HashMap<String, Integer>();
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

    public void addNewPrefab(Prefab prefab, Integer count)
    {
        if (prefab == null)
            return;

        List<IndividualModel> instanceMembers = new ArrayList<IndividualModel>();
        List<IndividualModel> individualModels = prefab.getMembers();

        for (IndividualModel model : individualModels)
        {
            instanceMembers.add(addNewInstance(model, count, true));
        }

        Prefab newPrefab = new Prefab(getNextAvailablePrefabInstanceId(), prefab.getName() + count, prefab.getMemberSuffix(), instanceMembers);
        prefabs.put(newPrefab.getId(), newPrefab);

        changeSupport.firePropertyChange(PC_NEW_INSTANCE_ADDED_FROM_PREFAB, null, newPrefab);
    }

    // TODO: Remove the ability to add single instances?
    public IndividualModel addNewInstance(IndividualModel individual, Integer count, Boolean prefabMember)
    {
        if (individual == null)
            return null;

        IndividualModel newIndividual = new IndividualModel(getNextAvailableInstanceId(), individual);
        newIndividual.setName(newIndividual.getName() + count);

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

    public Integer getPrefabCount(String name)
    {
        Integer count = prefabCount.get(name);

        if (count == null)
        {
            count = 0;
            prefabCount.put(name, count);
        }
        else
        {
            prefabCount.put(name, ++count);
        }

        return count;
    }

    public int getIndividualCount(String name)
    {
        Integer count = individualCount.get(name);

        if (count == null)
        {
            count = 0;
            individualCount.put(name, count);
        }
        else
        {
            individualCount.put(name, ++count);
        }

        return count;
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

    IndividualModel getInstance(int id)
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
