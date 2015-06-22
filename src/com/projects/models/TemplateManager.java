package com.projects.models;

import com.projects.helper.DeviceType;
import com.projects.helper.StructureType;

import java.util.HashMap;

/**
 * Created by Dan on 6/19/2015.
 */
public class TemplateManager extends System
{
    private Template template;
    private Structure structureBeingCreated;
    private Integer deviceBeingEdited;
    public static final String PC_TEMPLATE_READY = "PC_TEMPLATE_READY";
    public static final String PC_TEMPLATE_SELECTED = "PC_TEMPLATE_SELECTED";
    public static final String PC_CREATE_STRUCTURE = "PC_CREATE_STRUCTURE";
    public static final String PC_ADD_DEVICE = "PC_ADD_DEVICE";
    public static final String PC_DEVICE_SELECTED = "PC_DEVICE_SELECTED";
    private static Integer nextAvailableStructureId = 0;
    private static Integer nextAvailableDeviceId = 0;
    private HashMap<Integer, Device> devices;

    private static Integer getNextAvailableStructureId()
    {
        return nextAvailableStructureId++;
    }
    private static Integer getNextAvailableDeviceId()
    {
        return nextAvailableDeviceId++;
    }

    public TemplateManager(Template template)
    {
        devices = new HashMap<Integer, Device>();
        this.template = template;
        structureBeingCreated = null;
        deviceBeingEdited = -1;
    }

    @Override
    public void postSetupSync()
    {
        changeSupport.firePropertyChange(PC_TEMPLATE_READY, null, template);
    }

    public void createNewStructure(Structure structure)
    {
        structureBeingCreated = new Structure(structure);
        structureBeingCreated.setId(getNextAvailableStructureId());
        changeSupport.firePropertyChange(PC_CREATE_STRUCTURE, null, structureBeingCreated);
    }

    public void selectTemplateStructure(Structure structure)
    {
        changeSupport.firePropertyChange(PC_TEMPLATE_SELECTED, null, structure);
    }

    public void deviceSelected(int id)
    {
        deviceBeingEdited = id;
        Device device = devices.get(id);
        changeSupport.firePropertyChange(PC_DEVICE_SELECTED, null, device);
    }

    public void editDeviceProperty(int index, Object value)
    {
        devices.get(deviceBeingEdited).changePropertyValue(index, value);
    }

    public void addDevice(DeviceType deviceType)
    {
        Device device = null;
        switch (deviceType)
        {
            case APPLIANCE:
            {
                device = new Device(template.getApplianceTemplate());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingCreated.getAppliances().add(device);
            } break;
            case ENERGY_SOURCE:
            {
                device = new Device(template.getEnergySourceTemplate());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingCreated.getEnergySources().add(device);
            } break;
            case ENERGY_STORAGE:
            {
                device = new Device(template.getEnergyStorageTemplate());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingCreated.getEnergyStorageDevices().add(device);
            } break;
        }

        changeSupport.firePropertyChange(PC_ADD_DEVICE, null, device);
    }

    public Structure getStructureBeingCreated() {
        return structureBeingCreated;
    }
}
