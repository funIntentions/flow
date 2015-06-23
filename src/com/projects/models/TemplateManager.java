package com.projects.models;

import com.projects.helper.DeviceType;
import com.projects.helper.StructureType;

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

    public TemplateManager(Template template)
    {
        devices = new HashMap<Integer, Device>();
        structures = new HashMap<Integer, Structure>();
        this.template = template;
        structureBeingEdited = null;
        lastSelected = null;
        deviceBeingEdited = -1;

        List<Structure> templateStructures = template.getStructureTemplates();

        for (Structure structure : templateStructures)
        {
            structure.setId(getNextAvailableStructureId());
            structures.put(structure.getId(), structure);
        }

        template.getApplianceTemplate().setId(getNextAvailableDeviceId());
        devices.put(template.getApplianceTemplate().getId(), template.getApplianceTemplate());

        template.getEnergySourceTemplate().setId(getNextAvailableDeviceId());
        devices.put(template.getEnergySourceTemplate().getId(), template.getEnergySourceTemplate());

        template.getEnergyStorageTemplate().setId(getNextAvailableDeviceId());
        devices.put(template.getEnergyStorageTemplate().getId(), template.getEnergyStorageTemplate());

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

    public void editDeviceProperty(int index, Object value)
    {
        devices.get(deviceBeingEdited).changePropertyValue(index, value);
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
                device = new Device(template.getApplianceTemplate());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingEdited.getAppliances().add(device);
            } break;
            case ENERGY_SOURCE:
            {
                device = new Device(template.getEnergySourceTemplate());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingEdited.getEnergySources().add(device);
            } break;
            case ENERGY_STORAGE:
            {
                device = new Device(template.getEnergyStorageTemplate());
                device.setId(getNextAvailableDeviceId());
                devices.put(device.getId(), device);
                structureBeingEdited.getEnergyStorageDevices().add(device);
            } break;
        }

        structures.put(structureBeingEdited.getId(), structureBeingEdited);
        changeSupport.firePropertyChange(PC_ADD_DEVICE, null, device);
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
            Device copiedDevice = new Device(device.getName(), getNextAvailableDeviceId(), device.getType(), device.getProperties());
            copy.add(copiedDevice);
            devices.put(copiedDevice.getId(), copiedDevice);
        }

        to.setAppliances(copy);

        copy = new ArrayList<Device>();
        for (Device device: from.getEnergySources())
        {
            Device copiedDevice = new Device(device.getName(), getNextAvailableDeviceId(), device.getType(), device.getProperties());
            copy.add(copiedDevice);
            devices.put(copiedDevice.getId(), copiedDevice);
        }

        to.setEnergySources(copy);

        copy = new ArrayList<Device>();
        for (Device device : from.getEnergyStorageDevices())
        {
            Device copiedDevice = new Device(device.getName(), getNextAvailableDeviceId(), device.getType(), device.getProperties());
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

    public Structure getCopyOfStructureBeingEdited()
    {
        return new Structure(structureBeingEdited);
    }

    public Structure getLastSelected()
    {
        return lastSelected;
    }
}
