package com.projects.gui;

import com.projects.gui.table.DeviceTable;
import com.projects.models.Device;
import com.projects.models.Template;
import com.projects.models.TemplateManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 6/18/2015.
 */
public class DeviceTabbedPane extends JTabbedPane
{
    DevicePanel applianceTab;
    DevicePanel energySourceTab;
    DevicePanel energyStorageTab;

    public DeviceTabbedPane(MouseListener deviceSelectedListener)
    {
        applianceTab = new DevicePanel("Appliances", deviceSelectedListener);
        energySourceTab = new DevicePanel("Energy Sources", deviceSelectedListener);
        energyStorageTab = new DevicePanel("Energy Storage", deviceSelectedListener);

        addTab(applianceTab.getTitle(), applianceTab);
        addTab(energySourceTab.getTitle(), energySourceTab);
        addTab(energyStorageTab.getTitle(), energyStorageTab);
    }

    public void addAppliance(Device device)
    {
        applianceTab.addDevice(device);
    }

    public void addEnergySource(Device device)
    {
        energySourceTab.addDevice(device);
    }

    public void addEnergyStorage(Device device)
    {
        energyStorageTab.addDevice(device);
    }
}
