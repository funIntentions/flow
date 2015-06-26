package com.projects.systems;

import com.projects.helper.DeviceType;
import com.projects.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dan on 6/19/2015.
 */
public class TemplateManager extends System
{
    private Template template;
    private Structure lastSelected;
    private Structure structureBeingEdited;
    private Integer deviceBeingEdited;
    public static final String PC_TEMPLATE_READY = "PC_TEMPLATE_READY";
    public static final String PC_TEMPLATE_SELECTED = "PC_TEMPLATE_SELECTED";
    public static final String PC_EDITING_STRUCTURE = "PC_EDIT_TEMPLATE";
    public static final String PC_CREATE_STRUCTURE = "PC_CREATE_STRUCTURE";
    public static final String PC_STRUCTURE_EDITED = "PC_STRUCTURE_EDITED";
    public static final String PC_ADD_DEVICE = "PC_ADD_DEVICE";
    public static final String PC_DEVICE_SELECTED = "PC_DEVICE_SELECTED";
    private static Integer nextAvailableStructureId = 0;
    private static Integer nextAvailableDeviceId = 0;
    private HashMap<Integer, Device> devices;
    private HashMap<Integer, Structure> structures;

    private static Integer getNextAvailableStructureId()
    {
        return nextAvailableStructureId++;
    }
    private static Integer getNextAvailableDeviceId()
    {
        return nextAvailableDeviceId++;
    }

    public TemplateManager()
    {
        devices = new HashMap<Integer, Device>();
        structures = new HashMap<Integer, Structure>();
        this.template = null;
        structureBeingEdited = null;
        lastSelected = null;
        deviceBeingEdited = -1;
    }

    public void newTemplate(Template template) // TODO: this and the constructor have similarities
    {
        devices.clear();
        structures.clear();
        this.template = template;
        structureBeingEdited = null;
        lastSelected = null;
        deviceBeingEdited = -1;

        List<Structure> templateStructures = template.getStructureTemplates();

        for (Structure structure : templateStructures)
        {
            structure.setId(getNextAvailableStructureId());
            structures.put(structure.getId(), structure);

            for (Device device : structure.getAppliances())
            {
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
            }

            for (Device device : structure.getEnergySources())
            {
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
            }

            for (Device device : structure.getEnergyStorageDevices())
            {
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
            }
        }

        template.getApplianceTemplate().setId(getNextAvailableDeviceId());
        devices.put(template.getApplianceTemplate().getId(), template.getApplianceTemplate());

        template.getEnergySourceTemplate().setId(getNextAvailableDeviceId());
        devices.put(template.getEnergySourceTemplate().getId(), template.getEnergySourceTemplate());

        template.getEnergyStorageTemplate().setId(getNextAvailableDeviceId());
        devices.put(template.getEnergyStorageTemplate().getId(), template.getEnergyStorageTemplate());

        changeSupport.firePropertyChange(PC_TEMPLATE_READY, null, template);
    }

    public List<Structure> syncWorldStructures(List<Structure> worldStructures)
    {
        for (Structure structure : worldStructures)
        {
            structure.setId(getNextAvailableStructureId());
            structures.put(structure.getId(), structure);

            for (Device device : structure.getAppliances())
            {
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
            }

            for (Device device : structure.getEnergySources())
            {
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
            }

            for (Device device : structure.getEnergyStorageDevices())
            {
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
            }
        }

        return worldStructures;
    }


    @Override
    public void postSetupSync()
    {
        changeSupport.firePropertyChange(PC_TEMPLATE_READY, null, template);
    }

    public void editStructure(Structure structure)
    {
        structureBeingEdited = new Structure(structures.get(structure.getId()));
        changeSupport.firePropertyChange(PC_EDITING_STRUCTURE, null, structureBeingEdited);
    }

    public void setStructure(Structure structure)
    {
        structures.put(structure.getId(), structure);
    }

    public void selectTemplateStructure(Structure structure)
    {
        lastSelected = structures.get(structure.getId());
        changeSupport.firePropertyChange(PC_TEMPLATE_SELECTED, null, structure);
    }

    public void deviceSelected(int id)
    {
        deviceBeingEdited = id;
        Device device = devices.get(id);
        changeSupport.firePropertyChange(PC_DEVICE_SELECTED, null, device);
    }

    public void editDeviceName(int index, String name)
    {
        Device device = devices.get(deviceBeingEdited);
        device.setName(name);

        switch (device.getType())
        {
            case APPLIANCE:
            {
                structureBeingEdited.getAppliances().set(index, device);
            } break;
            case ENERGY_SOURCE:
            {
                structureBeingEdited.getEnergySources().set(index, device);
            } break;
            case ENERGY_STORAGE:
            {
                structureBeingEdited.getEnergyStorageDevices().set(index, device);
            } break;
        }
    }

    public void editDeviceProperty(int index, Object value)
    {
        Device device = devices.get(deviceBeingEdited);
        device.changePropertyValue(index, value);
        List<Device> deviceList = new ArrayList<Device>();

        switch (device.getType())
        {
            case APPLIANCE:
            {
                deviceList = structureBeingEdited.getAppliances();
            } break;
            case ENERGY_SOURCE:
            {
                deviceList = structureBeingEdited.getEnergySources();
            } break;
            case ENERGY_STORAGE:
            {
                deviceList = structureBeingEdited.getEnergyStorageDevices();
            } break;
        }

        for (Device d : deviceList)
        {
            if (d.getId().intValue() == device.getId().intValue())
            {
                d.changePropertyValue(index, value);
            }
        }

    }

    public void editObjectProperty(int index, Object value)
    {
        structureBeingEdited.changePropertyValue(index, value);
        structures.put(structureBeingEdited.getId(), structureBeingEdited);
    }

    public void addDevice(DeviceType deviceType)
    {
        Device device = null;
        switch (deviceType)
        {
            case APPLIANCE:
            {
                device = new Appliance(template.getApplianceTemplate().getProperties());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingEdited.getAppliances().add(device);
            } break;
            case ENERGY_SOURCE:
            {
                device = new EnergySource(template.getEnergySourceTemplate().getProperties());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingEdited.getEnergySources().add(device);
            } break;
            case ENERGY_STORAGE:
            {
                device = new EnergyStorage(template.getEnergyStorageTemplate().getProperties());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingEdited.getEnergyStorageDevices().add(device);
            } break;
        }

        structures.put(structureBeingEdited.getId(), structureBeingEdited);
        changeSupport.firePropertyChange(PC_ADD_DEVICE, null, device);
    }

    public void removeDevice(int id)
    {
        Device device = devices.get(id);
        switch (device.getType())
        {
            case APPLIANCE:
            {
                removeDeviceFromList(structureBeingEdited.getAppliances(), device);
            } break;
            case ENERGY_SOURCE:
            {
                removeDeviceFromList(structureBeingEdited.getEnergySources(), device);
            } break;
            case ENERGY_STORAGE:
            {
                removeDeviceFromList(structureBeingEdited.getEnergyStorageDevices(), device);
            } break;
        }

        devices.remove(id);
        structures.put(structureBeingEdited.getId(), structureBeingEdited);
    }

    private void removeDeviceFromList(List<Device> deviceList, Device deviceToRemove)
    {
        for (Device device : deviceList)
        {
            if (device.getId().intValue() == deviceToRemove.getId().intValue())
            {
                deviceList.remove(device);
                return;
            }
        }
    }

    public void editName(String name)
    {
        structureBeingEdited.setName(name);
        structures.put(structureBeingEdited.getId(), structureBeingEdited);
    }

    public void editNumberOfUnits(Integer num)
    {
        structureBeingEdited.setNumberOfUnits(num);
        structures.put(structureBeingEdited.getId(), structureBeingEdited);
    }

    private void copyDevices(Structure from, Structure to)
    {

        List<Device> copy = new ArrayList<Device>();
        for (Device device : from.getAppliances())
        {
            Device copiedDevice = new Appliance(device.getName(), getNextAvailableDeviceId(), device.getProperties());
            copy.add(copiedDevice);
            devices.put(copiedDevice.getId(), copiedDevice);
        }

        to.setAppliances(copy);

        copy = new ArrayList<Device>();
        for (Device device: from.getEnergySources())
        {
            Device copiedDevice = new EnergySource(device.getName(), getNextAvailableDeviceId(), device.getProperties());
            copy.add(copiedDevice);
            devices.put(copiedDevice.getId(), copiedDevice);
        }

        to.setEnergySources(copy);

        copy = new ArrayList<Device>();
        for (Device device : from.getEnergyStorageDevices())
        {
            Device copiedDevice = new EnergyStorage(device.getName(), getNextAvailableDeviceId(), device.getProperties());
            copy.add(copiedDevice);
            devices.put(copiedDevice.getId(), copiedDevice);
        }

        to.setEnergyStorageDevices(copy);
    }

    public Structure createStructureFromTemplate(Integer id)
    {
        Structure template = structures.get(id);
        Structure structure = new Structure(template);
        copyDevices(template, structure);
        structure.setId(getNextAvailableStructureId());
        structures.put(structure.getId(), structure);
        return structure;
    }

    public Structure getStructure(Integer id)
    {
        return structures.get(id);
    }

    public Structure finishedStructureEditing()
    {
        changeSupport.firePropertyChange(PC_STRUCTURE_EDITED, null, structureBeingEdited);
        return new Structure(structureBeingEdited);
    }

    public Structure getLastSelected()
    {
        return lastSelected;
    }

    public HashMap<Integer, Structure> getStructures()
    {
        return structures;
    }

    public Template getTemplate()
    {
        return template;
    }
}
