package com.projects.simulation;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.model.Building;
import com.projects.model.EnergyStorage;
import com.projects.model.Structure;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    private List<Building> structures;
    private HashMap<Integer, List<Float>> deviceStorageProfiles;

    public StorageManager()
    {
        structures = new ArrayList<Building>();
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

        for (Building structure : structures)
        {
            List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

            for (EnergyStorage storage : storageDevices)
            {
                storage.setStoredEnergy(0);
            }
        }
    }

    public float getStructuresStorageDemandAtTime(Building structure, int time)
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

    public void updateStorageStrategies(int day, DemandManager demandManager, StatsManager statsManager)
    {
        for (Building structure : structures)
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

            if (!strategize.isnil())
            {
                strategize.invoke(varargs);
            }
            else
            {
                System.out.println("Lua function not found");
            }
        }
        catch (LuaError error)
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    displayLuaExceptionDialog(error, script);
                }
            });
        }

        List<Float> newStorageProfile = newStorageProfileWrapper.storageProfile;

        deviceStorageProfiles.put(storageDevice.getId(), newStorageProfile);
    }

    private void displayLuaExceptionDialog(LuaError error, String script)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Error in Script");
        alert.setContentText("A problem occurred while trying to run " + script + " as a storage strategy.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stacktrace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
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

    public void addStructureStorageDevices(Building structure)
    {
        List<EnergyStorage> storageDevices = (List)structure.getEnergyStorageDevices();

        for (EnergyStorage storage : storageDevices)
        {
            deviceStorageProfiles.put(storage.getId(), new ArrayList<Float>());
        }
    }

    public void removeStructureStorageDevices(Building structure)
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

    public boolean syncStructures(Building changedStructure)
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

