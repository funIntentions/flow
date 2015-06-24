package com.projects.gui;

import com.projects.models.Device;

import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 6/18/2015.
 */
public class DeviceTabbedPane extends JTabbedPane
{
    private DevicePanel applianceTab;
    private DevicePanel energySourceTab;
    private DevicePanel energyStorageTab;

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

    public void clearTables()
    {
        applianceTab.clearTable();
        energySourceTab.clearTable();
        energyStorageTab.clearTable();
    }
}
