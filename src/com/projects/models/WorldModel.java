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
    private static Integer nextAvailableStructureId = 0;
    private int selectedInstance;
    private HashMap<Integer, IndividualModel> instances;
    private HashMap<String, Integer> individualCount;
    private HashMap<Integer, IndividualModel> prefabInstances;
    private HashMap<Integer, Structure> structures;
    private HashMap<String, Integer> prefabCount;
    private PropertyChangeSupport changeSupport;
    public static final String PC_NEW_STRUCTURE = "PC_NEW_STRUCTURE";
    public static final String PC_STRUCTURE_SELECTED = "PC_STRUCTURE_SELECTED";
    public static final String PC_EDIT_STRUCTURE = "PC_EDIT_STRUCTURE";
    public static final String PC_INSTANCE_DELETED = "PC_INSTANCE_DELETED";
    private static final String PC_PREFAB_DELETED = "PC_PREFAB_DELETED";
    public static final String PC_NEW_INSTANCE_SELECTED = "PC_NEW_INSTANCE_SELECTED";
    public static final String PC_NEW_WORLD_PREFAB_SELECTED = "PC_NEW_WORLD_PREFAB_SELECTED";
    public static final String PC_WORLD_CLEARED = "PC_WORLD_CLEARED";
    public static final String PC_NEW_INSTANCE_ADDED_FROM_PREFAB = "PC_NEW_INSTANCE_ADDED_FROM_PREFAB";

    private static Integer getNextAvailableInstanceId()
    {
        return nextAvailableInstanceId++;
    }

    private static Integer getNextAvailableStructureId()
    {
        return nextAvailableStructureId++;
    }

    public WorldModel()
    {
        selectedInstance = -1;
        instances = new HashMap<Integer, IndividualModel>();
        prefabInstances = new HashMap<Integer, IndividualModel>();
        structures = new HashMap<Integer, Structure>();
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

    public void addNewStructure(Structure structure)
    {
        structure.setId(getNextAvailableStructureId());
        structures.put(structure.getId(), structure);
        changeSupport.firePropertyChange(PC_NEW_STRUCTURE, null, structure);
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
        /*if (instances.containsKey(individual.getId()))
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
        }*/
    }

    public void removePrefab(Prefab prefab)
    {
        for (IndividualModel individualModel : prefab.getMembers())
        {
           prefabInstances.remove(individualModel.getId());
        }

        //prefabs.remove(prefab.getId());
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

    public void changeSelectedIndividual(int id)
    {
        if (id < 0)
            return;

        IndividualModel selected;

        // TODO: Don't do the check for whether it's a prefab member in WorldModel (also this is a poor way to do it)
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

    public void selectStructure(Structure structure)
    {
        changeSupport.firePropertyChange(PC_STRUCTURE_SELECTED, null, structure);
    }

    public void editStructure(Structure structure)
    {
        changeSupport.firePropertyChange(PC_EDIT_STRUCTURE, null, structure);
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
