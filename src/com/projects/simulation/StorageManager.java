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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages buildings with storage devices.
 */
public class StorageManager {
    private Main main;
    private List<Building> structures;
    private HashMap<Integer, List<Float>> deviceStorageProfiles;
    private Boolean errorEncountered = false;

    /**
     * StorageManager constructor.
     */
    public StorageManager() {
        structures = new ArrayList<>();
        deviceStorageProfiles = new HashMap<>();
    }

    /**
     * Provides a reference to main.
     *
     * @param main main
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Resets the storage devices and storage profiles.
     */
    public void reset() {
        errorEncountered = false;

        for (List<Float> profile : deviceStorageProfiles.values()) {
            profile.clear();
        }

        for (Building structure : structures) {
            List<EnergyStorage> storageDevices = structure.getEnergyStorageDevices();

            for (EnergyStorage storage : storageDevices) {
                storage.setStoredEnergy(0);
            }
        }
    }

    /**
     * Returns how much a particular building is storing or releasing energy at a particular time.
     *
     * @param structure the building
     * @param time      the time
     * @return
     */
    public float getStructuresStorageDemandAtTime(Building structure, int time) {
        List<EnergyStorage> storageDevices = structure.getEnergyStorageDevices();
        float demand = 0;

        for (EnergyStorage storage : storageDevices) {
            List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());

            if (storageProfile.size() > 0)
                demand += storageProfile.get(time);
        }

        return demand;
    }

    /**
     * Creates a new storage profile for each storage device.
     *
     * @param simulationStatus the status of the simulation at this time
     */
    public void updateStorageStrategies(World.SimulationStatus simulationStatus) {
        for (Building structure : structures) {
            List<EnergyStorage> energyStorageDevices = (List) structure.getEnergyStorageDevices();

            for (EnergyStorage storage : energyStorageDevices) {
                // Initialize if empty
                List<Float> storageProfile = deviceStorageProfiles.get(storage.getId());
                if (storageProfile == null || storageProfile.size() == 0) {
                    storageProfile = new ArrayList<>();

                    for (int time = 0; time < TimeUnit.DAYS.toMinutes(1); ++time) {
                        storageProfile.add(0f);
                    }
                }
                deviceStorageProfiles.put(storage.getId(), storageProfile);

                runLuaStrategyScript(storage, structure, simulationStatus);
            }
        }
    }

    /**
     * Runs the storage strategy script assigned to a storage device.
     *
     * @param storageDevice    the storage device
     * @param building         the building which owns the storage device
     * @param simulationStatus the status fo the simulation at this time
     */
    public void runLuaStrategyScript(EnergyStorage storageDevice, Building building, World.SimulationStatus simulationStatus) {

        StorageProfileWrapper newStorageProfileWrapper = new StorageProfileWrapper();

        try {
            LuaValue luaGlobals = JsePlatform.standardGlobals();
            luaGlobals.get("dofile").call(LuaValue.valueOf(Constants.STRATEGIES_FILE_PATH + storageDevice.getStorageStrategy()));

            LuaValue storageDeviceLua = CoerceJavaToLua.coerce(storageDevice);
            LuaValue buildingLua = CoerceJavaToLua.coerce(building);
            LuaValue simulationStatusLua = CoerceJavaToLua.coerce(simulationStatus);
            LuaValue newStorageProfileLua = CoerceJavaToLua.coerce(newStorageProfileWrapper);
            LuaValue[] luaValues = new LuaValue[4];
            luaValues[0] = storageDeviceLua;
            luaValues[1] = buildingLua;
            luaValues[2] = simulationStatusLua;
            luaValues[3] = newStorageProfileLua;
            Varargs varargs = LuaValue.varargsOf(luaValues);

            LuaValue strategize = luaGlobals.get("strategize");

            if (!strategize.isnil()) {
                strategize.invoke(varargs);
                if (newStorageProfileWrapper.storageProfile.size() != Constants.MINUTES_IN_DAY) {
                    errorEncountered = true;
                    Platform.runLater(() -> alertProblemWithStrategy("Storage Profile Wrong Size", "The storage strategy must produce a storage profile that has transfer amounts for every minute of the day (must have a length of 1440)."));
                }
            } else {
                errorEncountered = true;
                Platform.runLater(() -> alertProblemWithStrategy("Lua strategize Function Not Found", "Please make sure the strategize function is properly defined: function strategize(storageDevice, building, simulationStatus, newStorageProfile)"));
            }

        } catch (LuaError error) {
            errorEncountered = true;
            Platform.runLater(() -> displayLuaExceptionDialog(error, storageDevice.getStorageStrategy()));
        }

        List<Float> newStorageProfile = newStorageProfileWrapper.storageProfile;

        deviceStorageProfiles.put(storageDevice.getId(), newStorageProfile);
    }

    /**
     * A dialog to use when the problem doesn't result in a lua error.
     *
     * @param header  the header of the dialog
     * @param problem the main body text of the dialog
     */
    private void alertProblemWithStrategy(String header, String problem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle("Problem with Strategy");
        alert.setHeaderText(header);
        alert.setContentText(problem);

        alert.showAndWait();
    }

    /**
     * A dialog that displays the stack trace of an lua error exception.
     *
     * @param error  the exception that occurred
     * @param script the script that was running when the error occurred
     */
    private void displayLuaExceptionDialog(LuaError error, String script) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Error in Script");
        alert.setContentText("A problem occurred while trying to run " + script + " as a storage strategy.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stack Trace:");

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

    /**
     * Removes a structure if it's being managed.
     *
     * @param structureToRemove the structure to remove
     * @return true if the structure was a building being managed
     */
    public boolean removeStructure(Structure structureToRemove) {
        for (Structure structure : structures) {
            if (structure.getId() == structureToRemove.getId()) {
                structures.remove(structure);
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a structures storage devices to the list being managed.
     *
     * @param structure the building whose storage devices should be managed
     */
    public void addStructureStorageDevices(Building structure) {
        List<EnergyStorage> storageDevices = (List) structure.getEnergyStorageDevices();

        for (EnergyStorage storage : storageDevices) {
            deviceStorageProfiles.put(storage.getId(), new ArrayList<>());
        }
    }

    /**
     * Removes a structures storage devices from the list being managed.
     *
     * @param structure the building whose storage devices should not be managed
     */
    public void removeStructureStorageDevices(Building structure) {
        List<EnergyStorage> storageDevices = (List) structure.getEnergyStorageDevices();

        for (EnergyStorage storage : storageDevices) {
            deviceStorageProfiles.remove(storage.getId());
        }
    }

    /**
     * Either adds, removes, or updates a building that this manager should know about
     *
     * @param changedStructure the building in question
     * @return true if the structure was or is being managed by this manager, false if it never was and isn't now
     */
    public boolean syncStructures(Building changedStructure) {
        int structureIndex = -1;

        for (int i = 0; i < structures.size(); ++i) {
            if (changedStructure.getId() == structures.get(i).getId()) {
                structureIndex = i;
            }
        }

        int storageDeviceCount = changedStructure.getEnergyStorageDevices().size();

        if (structureIndex < 0 && storageDeviceCount > 0) {
            addStructureStorageDevices(changedStructure);
            structures.add(changedStructure);
        } else if (structureIndex >= 0) {
            if (storageDeviceCount > 0) {
                removeStructureStorageDevices(changedStructure);
                addStructureStorageDevices(changedStructure);
                structures.set(structureIndex, changedStructure);
            } else {
                removeStructureStorageDevices(changedStructure);
                structures.remove(structureIndex);
            }
        } else {
            return false;
        }

        return true;
    }

    public Boolean errorEncountered() {
        return errorEncountered;
    }

    public HashMap<Integer, List<Float>> getDeviceStorageProfiles() {
        return deviceStorageProfiles;
    }

    private class StorageProfileWrapper {
        public final List<Float> storageProfile = new ArrayList<>();

        public void set(int index, float value) {
            storageProfile.set(index, value);
        }

        public void add(float item) {
            storageProfile.add(item);
        }
    }
}

