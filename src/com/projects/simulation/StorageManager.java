package com.projects.simulation;

import com.projects.Main;
import com.projects.helper.StorageState;
import com.projects.model.EnergyStorage;
import com.projects.model.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/17/2015.
 */
public class StorageManager
{
    private Main main;
    private List<Structure> structures;
    private HashMap<Integer, List<Float>> deviceStorageProfiles;

    public StorageManager()
    {
        structures = new ArrayList<Structure>();
        deviceStorageProfiles = new HashMap<Integer, List<Float>>();
    }

    public void setMain(Main main)
    {
        this.main = main;
    }

    public void reset()
    {
        for (List<Float> profile : deviceStorageProfiles.values())
        {
            profile.clear();
        }

        for (Structure structure : structures)
        {
            List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : storageDevices)
            {
                storage.setStorageState(StorageState.CHARGING);
                storage.setStoredEnergy(0);
            }
        }
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

    public void runStorageStrategyTestOne(DemandManager demandManager, StatsManager statsManager, EnergyStorage storage)
    {
        // Strategy example, store when electricity has a low cost and then use that power when it's a highDemandPrice
        List<Integer> previousDaysDemandProfiles = demandManager.getDemandProfileForToday();
        List<Float> storageProfile = new ArrayList<Float>();

        float averageDemand = 0;

        for (Integer demand : previousDaysDemandProfiles)
        {
            averageDemand += demand;
        }

        averageDemand /= previousDaysDemandProfiles.size();

        float priceForAverageDemand = statsManager.getPriceForDemand((int)Math.floor(averageDemand));

        for (int time = 0; time < previousDaysDemandProfiles.size(); ++time)
        {
            int demand = previousDaysDemandProfiles.get(time);
            float priceForDemand = statsManager.getPriceForDemand(demand);
            float chargeAmount = 0;

            if (priceForDemand < priceForAverageDemand
                    && storage.getStoredEnergy() < storage.getStorageCapacity())
            {
                chargeAmount = (float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1));
                if (storage.getStorageCapacity() < storage.getStoredEnergy() + chargeAmount)
                {
                    chargeAmount = (float)(storage.getStorageCapacity() - storage.getStoredEnergy());
                }

                storage.setStoredEnergy(storage.getStoredEnergy() + chargeAmount);
            }
            else if (priceForDemand > priceForAverageDemand
                    && storage.getStoredEnergy() > 0)
            {
                chargeAmount = -(float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1));
                if (0 > storage.getStoredEnergy() + chargeAmount)
                {
                    chargeAmount = -(float)(storage.getStoredEnergy());
                }

                storage.setStoredEnergy(storage.getStoredEnergy() + chargeAmount);
            }

            storageProfile.add(chargeAmount);
        }

        deviceStorageProfiles.put(storage.getId(), storageProfile);
    }

    public void runGreedyStorageStrategy(DemandManager demandManager, StatsManager statsManager, EnergyStorage storage)
    {
        // Strategy example, charge when empty and discharge when full
        List<Integer> previousDaysDemandProfiles = demandManager.getDemandProfileForToday();
        List<Float> storageProfile = new ArrayList<Float>();

        for (int time = 0; time < previousDaysDemandProfiles.size(); ++time)
        {
            float chargeAmount = 0;

            if (storage.getStoredEnergy() == 0)
            {
                storage.setStorageState(StorageState.CHARGING);
            }
            else if (storage.getStoredEnergy() == storage.getStorageCapacity())
            {
                storage.setStorageState(StorageState.DISCHARGING);
            }

            if (storage.getStorageState() == StorageState.CHARGING)
            {
                chargeAmount = (float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1));
                if (storage.getStorageCapacity() < storage.getStoredEnergy() + chargeAmount)
                {
                    chargeAmount = (float)(storage.getStorageCapacity() - storage.getStoredEnergy());
                }

                storage.setStoredEnergy(storage.getStoredEnergy() + chargeAmount);
            }
            else if (storage.getStorageState() == StorageState.DISCHARGING)
            {
                chargeAmount = -(float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1));
                if (0 > storage.getStoredEnergy() - chargeAmount)
                {
                    chargeAmount = -(float)(storage.getStoredEnergy());
                }

                storage.setStoredEnergy(storage.getStoredEnergy() + chargeAmount);
            }

            storageProfile.add(chargeAmount);
        }

        deviceStorageProfiles.put(storage.getId(), storageProfile);
    }

    public void runLocalAverageMatchingStrategy(DemandManager demandManager, StatsManager statsManager, Structure structure, EnergyStorage storage)
    {
        // Strategy example, store when electricity has a low cost and then use that power when it's a highDemandPrice
        List<Float> previousDaysStructureDemandProfiles = demandManager.getLoadProfile(structure);
        List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());

        if (storageProfile == null || storageProfile.size() == 0)
        {
            storageProfile = new ArrayList<>();

            for (Float demand : previousDaysStructureDemandProfiles)
            {
                storageProfile.add(0f);
            }
        }

        float transferCapacity = (float)(storage.getChargingRate()/ TimeUnit.HOURS.toMinutes(1));

        float averageDemand = 0;

        for (Float demand : previousDaysStructureDemandProfiles)
        {
            averageDemand += demand;
        }

        averageDemand /= previousDaysStructureDemandProfiles.size();

        for (int time = 0; time < previousDaysStructureDemandProfiles.size(); ++time)
        {
            float demand = previousDaysStructureDemandProfiles.get(time);
            float requestedChargeAmount = Math.round(averageDemand - demand);
            float chargeAmount = 0;

            if (requestedChargeAmount < 0 && storage.getStoredEnergy() > 0) // discharge
            {
                if (Math.abs(requestedChargeAmount) < transferCapacity)
                    chargeAmount = requestedChargeAmount;
                else
                    chargeAmount = -transferCapacity;

                if (0 > storage.getStoredEnergy() + chargeAmount)
                {
                    chargeAmount = -(float)(storage.getStoredEnergy());
                }
            }
            else if (requestedChargeAmount > 0 && storage.getStorageCapacity() > storage.getStoredEnergy()) // charge
            {
                if (requestedChargeAmount < transferCapacity)
                    chargeAmount = requestedChargeAmount;
                else
                    chargeAmount = transferCapacity;

                if (storage.getStorageCapacity() < storage.getStoredEnergy() + chargeAmount)
                {
                    chargeAmount = (float)(storage.getStorageCapacity() - storage.getStoredEnergy());
                }
            }
            else // do the same as yesterday
            {
                chargeAmount = storageProfile.get(time);
            }

            storage.setStoredEnergy(storage.getStoredEnergy() + chargeAmount);

            storageProfile.set(time, chargeAmount);
        }

        deviceStorageProfiles.put(storage.getId(), storageProfile);
    }

    public void updateStorageStrategies(DemandManager demandManager, StatsManager statsManager)
    {
        for (Structure structure : structures)
        {
            List<EnergyStorage> energyStorageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : energyStorageDevices)
            {
                switch (storage.getStorageStrategy())
                {
                    case TEST_ONE:
                    {
                        runStorageStrategyTestOne(demandManager, statsManager, storage);
                    } break;
                    case TEST_TWO:
                    {
                        runGreedyStorageStrategy(demandManager, statsManager, storage);
                    } break;
                    case LOCAL_AVERAGE_MATCHING:
                    {
                        runLocalAverageMatchingStrategy(demandManager, statsManager, structure, storage);
                    } break;
                }
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
            addStructureStorageDevices(changedStructure);
            structures.add(changedStructure);
        }
        else if (structureIndex >=0)
        {
            if (storageDeviceCount > 0)
            {
                removeStructureStorageDevices(changedStructure);
                addStructureStorageDevices(changedStructure);
                structures.set(structureIndex, changedStructure);
            }
            else
            {
                removeStructureStorageDevices(changedStructure);
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

