package com.projects.systems.simulation;

import com.projects.models.EnergyStorage;
import com.projects.models.Structure;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/17/2015.
 */
public class StorageManager
{
    List<Structure> structures;
    private HashMap<Integer, List<Float>> deviceStorageProfiles;

    public StorageManager()
    {
        structures = new ArrayList<Structure>();
        deviceStorageProfiles = new HashMap<Integer, List<Float>>();
    }

    public float getStructuresStorageDemandAtTime(Structure structure, int time)
    {
        List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();
        float demand = 0;

        for (EnergyStorage storage : storageDevices)
        {
            List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());

            if (storageProfile.size() > 0)
                demand += storageProfile.get(time);
        }

        return demand;
    }

    public void updateStorageStrategy(DemandManager demandManager, StatsManager statsManager, EnergyStorage storage)
    {
        // Strategy example, store when electricity has a low cost and then use that power when it's a highDemandPrice
        List<Integer> previousDaysDemandProfiles = demandManager.getTodaysDemandProfile();
        List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());
        float lowDemandPrice = 0.6f;
        float highDemandPrice = 0.8f;

        for (int time = 0; time < previousDaysDemandProfiles.size(); ++time)
        {
            int demand = previousDaysDemandProfiles.get(time);
            float priceForDemand = statsManager.getPriceForDemand(demand);
            float chargeAmount = 0;

            if (priceForDemand <= lowDemandPrice)
            {
                chargeAmount = (float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1));
                if (storage.getStorageCapacity() < storage.getStoredEnergy() + chargeAmount)
                {
                    chargeAmount = (float)(storage.getStorageCapacity() - storage.getStoredEnergy());
                }

            }
            else if (priceForDemand >= highDemandPrice)
            {
                chargeAmount = (float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1)); // TODO: add property discharge rate to storage devices, then change charging rate here to that
                if (0 > storage.getStoredEnergy() - chargeAmount)
                {
                    chargeAmount = -(float)(storage.getStoredEnergy());
                }
            }

            storageProfile.add(chargeAmount);
        }
    }

    public void updateStorageStrategies(DemandManager demandManager, StatsManager statsManager)
    {
        for (Structure structure : structures)
        {
            List<EnergyStorage> energyStorageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : energyStorageDevices)
            {
                updateStorageStrategy(demandManager, statsManager, storage);
            }
        }
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
        deviceStorageProfiles.clear();
    }

    public void addStructureStorageDevices(Structure structure)
    {
        List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

        for (EnergyStorage storage : storageDevices)
        {
            deviceStorageProfiles.put(storage.getId(), new ArrayList<Float>());
        }
    }

    public void removeStructureStorageDevices(Structure structure)
    {
        List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

        for (EnergyStorage storage : storageDevices)
        {
            deviceStorageProfiles.remove(storage.getId());
        }
    }

    public HashMap<Integer, List<Float>> getDeviceStorageProfiles()
    {
        return deviceStorageProfiles;
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
            addStructureStorageDevices(changedStructure);
        }
        else if (structureIndex >=0)
        {
            if (storageDeviceCount > 0)
            {
                structures.set(structureIndex, changedStructure);
                addStructureStorageDevices(changedStructure); // TODO: check if structure has had storage devices removed, currently they're not removed.
            }
            else
            {
                structures.remove(structureIndex);
                removeStructureStorageDevices(changedStructure);
            }
        }
        else
        {
            return false;
        }

        return true;
    }

}
