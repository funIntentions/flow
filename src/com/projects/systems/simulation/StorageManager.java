package com.projects.systems.simulation;

import com.projects.models.EnergyStorage;
import com.projects.models.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dan on 7/17/2015.
 */
public class StorageManager
{
    List<Structure> structures;
    private HashMap<Integer, List<Float>> structureStorageProfiles; // TODO: it'll be something like that maybe?

    public StorageManager()
    {
        structures = new ArrayList<Structure>();
    }

    public void updateStorageStrategies()
    {

    }

    public void updateStorage()
    {

        for (Structure structure : structures)
        {
            List<EnergyStorage> energyStorageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : energyStorageDevices)
            {

            }
        }
    }

    public boolean removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId() == structureToRemove.getId())
            {
                structures.remove(structure);
                return true;
            }
        }

        return false;
    }

    public void removeAllStructures()
    {
        structures.clear();
    }

    public boolean syncStructures(Structure changedStructure)
    {
        int structureIndex = -1;

        for (int i = 0; i < structures.size(); ++i)
        {
            if (changedStructure.getId() == structures.get(i).getId())
            {
                structureIndex = i;
            }
        }

        int storageDeviceCount = changedStructure.getEnergyStorageDevices().size();

        if (structureIndex < 0 && storageDeviceCount > 0)
        {
            structures.add(changedStructure);
        }
        else if (structureIndex >=0)
        {
            if (storageDeviceCount > 0)
            {
                structures.set(structureIndex, changedStructure);
            }
            else
            {
                structures.remove(structureIndex);
            }
        }
        else
        {
            return false;
        }

        return true;
    }

}
