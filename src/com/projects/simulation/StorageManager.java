package com.projects.simulation;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.model.EnergyStorage;
import com.projects.model.SingleUnitStructure;
import com.projects.model.Structure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

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
    private List<SingleUnitStructure> structures;
    private HashMap<Integer, List<Float>> deviceStorageProfiles;

    public StorageManager()
    {
        structures = new ArrayList<SingleUnitStructure>();
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

        for (SingleUnitStructure structure : structures)
        {
            List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : storageDevices)
            {
                storage.setStoredEnergy(0);
            }
        }
    }

    public float getStructuresStorageDemandAtTime(SingleUnitStructure structure, int time)
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
        List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());

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

    public void updateStorageStrategies(int day, DemandManager demandManager, StatsManager statsManager)
    {
        for (SingleUnitStructure structure : structures)
        {
            List<EnergyStorage> energyStorageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : energyStorageDevices)
            {
                // Initialize if empty
                List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());
                if (storageProfile == null || storageProfile.size() == 0)
                {
                    storageProfile = new ArrayList<>();

                    for (int time = 0; time < TimeUnit.DAYS.toMinutes(1); ++time)
                    {
                        storageProfile.add(0f);
                    }
                }
                deviceStorageProfiles.put(storage.getId(), storageProfile);

                runLuaStrategyScript(storage.getStorageStrategy(), storage, structure.getLoadProfilesForWeek().get(day), deviceStorageProfiles.get(storage.getId()));
            }
        }
    }

    private class StorageProfileWrapper
    {
        public final List<Float> storageProfile = new ArrayList<>();

        public void set(int index, float value)
        {
            storageProfile.set(index, value);
        }

        public void add(float item)
        {
            storageProfile.add(item);
        }
    }

    public void runLuaStrategyScript(String script, EnergyStorage storageDevice, List<Float> loadProfile, List<Float> oldStorageProfile)
    {

        StorageProfileWrapper newStorageProfileWrapper = new StorageProfileWrapper();

        try {
            LuaValue luaGlobals = JsePlatform.standardGlobals();
            luaGlobals.get("dofile").call(LuaValue.valueOf(Constants.STRATEGIES_FILE_PATH + script));

            LuaValue storageDeviceLua = CoerceJavaToLua.coerce(storageDevice);
            LuaValue loadProfileLua = CoerceJavaToLua.coerce(loadProfile);
            LuaValue oldStorageProfileLua = CoerceJavaToLua.coerce(oldStorageProfile);
            LuaValue newStorageProfileLua = CoerceJavaToLua.coerce(newStorageProfileWrapper);
            LuaValue[] luaValues = new LuaValue[4];
            luaValues[0] = storageDeviceLua;
            luaValues[1] = loadProfileLua;
            luaValues[2] = oldStorageProfileLua;
            luaValues[3] = newStorageProfileLua;
            Varargs varargs = LuaValue.varargsOf(luaValues);

            LuaValue strategize = luaGlobals.get("strategize");
            if (!strategize.isnil()) {
                strategize.invoke(varargs);
            } else {
                System.out.println("Lua function not found");
            }
        } catch (LuaError e){
            e.printStackTrace();
        }

        List<Float> newStorageProfile = newStorageProfileWrapper.storageProfile;

        deviceStorageProfiles.put(storageDevice.getId(), newStorageProfile);
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

    public void addStructureStorageDevices(SingleUnitStructure structure)
    {
        List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

        for (EnergyStorage storage : storageDevices)
        {
            deviceStorageProfiles.put(storage.getId(), new ArrayList<Float>());
        }
    }

    public void removeStructureStorageDevices(SingleUnitStructure structure)
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

    public boolean syncStructures(SingleUnitStructure changedStructure)
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

