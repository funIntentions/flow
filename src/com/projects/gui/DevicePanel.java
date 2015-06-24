package com.projects.gui;

import com.projects.gui.table.DeviceTable;
import com.projects.models.Device;

import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 6/18/2015.
 */
public class DevicePanel extends JScrollPane
{
    private String title;
    private JTable templateTable;
    private DeviceTable deviceTable;
    private MouseListener selectionListener;

    public DevicePanel(String panelTitle, MouseListener listener)
    {
        title = panelTitle;
        selectionListener = listener;
        deviceTable = new DeviceTable();
        templateTable = new JTable(deviceTable);
        templateTable.addMouseListener(selectionListener);
        templateTable.setTableHeader(null);
        getViewport().add(templateTable);
    }

    public String getTitle()
    {
        return title;
    }

    public void addDevice(Device device)
    {
        deviceTable.addRow(device);
    }

    public void clearTable()
    {
        deviceTable.clearTable();
    }

    public JTable getTable()
    {
        return templateTable;
    }
}
