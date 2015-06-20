package com.projects.models;

import com.projects.helper.DeviceType;

import java.util.List;

/**
 * Created by Dan on 6/17/2015.
 */
public class Template
{
    private Device applianceTemplate;
    private Device energySourceTemplate;
    private Device energyStorageTempalte;
    private List<Structure> structureTemplates;

    public Template(List<Device> devices, List<Structure> structures)
    {
        structureTemplates = structures;

        for (Device device : devices)
        {
            if (device.getType() == DeviceType.APPLIANCE)
            {
                applianceTemplate = device;
            }
            else if (device.getType() == DeviceType.ENERGY_SOURCE)
            {
                energySourceTemplate = device;
            }
            else if (device.getType() == DeviceType.ENERGY_STORAGE)
            {
                energyStorageTempalte = device;
            }
        }
    }

    public List<Structure> getStructureTemplates()
    {
        return structureTemplates;
    }

    public void setStructureTemplates(List<Structure> structureTemplates)
    {
        this.structureTemplates = structureTemplates;
    }

    public Device getApplianceTemplate() {
        return applianceTemplate;
    }

    public void setApplianceTemplate(Device applianceTemplate) {
        this.applianceTemplate = applianceTemplate;
    }

    public Device getEnergySourceTemplate() {
        return energySourceTemplate;
    }

    public void setEnergySourceTemplate(Device energySourceTemplate) {
        this.energySourceTemplate = energySourceTemplate;
    }

    public Device getEnergyStorageTemplate() {
        return energyStorageTempalte;
    }

    public void setEnergyStorageTempalte(Device energyStorageTempalte) {
        this.energyStorageTempalte = energyStorageTempalte;
    }
}
