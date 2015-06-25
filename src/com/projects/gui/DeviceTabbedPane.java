package com.projects.gui;

import com.projects.models.Device;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 6/18/2015.
 */
public class DeviceTabbedPane extends JTabbedPane
{
    private DevicePanel applianceTab;
    private DevicePanel energySourceTab;
    private DevicePanel energyStorageTab;

    public DeviceTabbedPane(TableModelListener tableModelListener, MouseListener deviceSelectedListener)
    {
        applianceTab = new DevicePanel("Appliances", tableModelListener, deviceSelectedListener);
        energySourceTab = new DevicePanel("Energy Sources", tableModelListener, deviceSelectedListener);
        energyStorageTab = new DevicePanel("Energy Storage", tableModelListener, deviceSelectedListener);

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
