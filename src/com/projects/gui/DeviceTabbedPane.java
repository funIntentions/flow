package com.projects.gui;

import com.projects.models.Template;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 6/18/2015.
 */
public class DeviceTabbedPane extends JTabbedPane
{
    DevicePanel applianceTab;
    DevicePanel energySourceTab;
    DevicePanel energyStorageTab;

    MouseListener deviceSelectedListener = new MouseAdapter()
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
            super.mousePressed(e);

        }
    };

    public DeviceTabbedPane()
    {
        applianceTab = new DevicePanel("Appliances", deviceSelectedListener);
        energySourceTab = new DevicePanel("Energy Sources", deviceSelectedListener);
        energyStorageTab = new DevicePanel("Energy Storage", deviceSelectedListener);

        addTab(applianceTab.getTitle(), applianceTab);
        addTab(energySourceTab.getTitle(), energySourceTab);
        addTab(energyStorageTab.getTitle(), energyStorageTab);
    }
}
