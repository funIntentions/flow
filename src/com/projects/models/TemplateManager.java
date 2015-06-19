package com.projects.models;

import com.projects.helper.DeviceType;

/**
 * Created by Dan on 6/19/2015.
 */
public class TemplateManager extends System
{
    private Template template;
    public static final String PC_TEMPLATE_READY = "PC_TEMPLATE_READY";
    public static final String PC_TEMPLATE_SELECTED = "PC_TEMPLATE_SELECTED";
    public static final String PC_ADD_DEVICE = "PC_ADD_DEVICE";

    public TemplateManager(Template template)
    {
        this.template = template;
    }

    @Override
    public void postSetupSync()
    {
        changeSupport.firePropertyChange(PC_TEMPLATE_READY, null, template);
    }

    public void structureTemplateSelected(Structure structure)
    {
        changeSupport.firePropertyChange(PC_TEMPLATE_SELECTED, null, structure);
    }

    public void addDevice(DeviceType deviceType)
    {
        Device device = null;
        switch (deviceType)
        {
            case APPLIANCE:
            {
                device = new Device(template.getApplianceTemplate());
            } break;
            case ENERGY_SOURCE:
            {
                device = new Device(template.getEnergySourceTemplate());
            } break;
            case ENERGY_STORAGE:
            {
                device = new Device(template.getEnergyStorageTempalte());
            } break;
        }

        changeSupport.firePropertyChange(PC_ADD_DEVICE, null, device);
    }
}
