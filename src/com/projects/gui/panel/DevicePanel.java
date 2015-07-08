package com.projects.gui.panel;

import com.projects.gui.table.DeviceTable;
import com.projects.models.Device;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.event.MouseListener;

/**
 * Created by Dan on 6/18/2015.
 */
public class DevicePanel extends JScrollPane
{
    private String title;
    private JTable templateTable;
    private DeviceTable deviceTable;

    public DevicePanel(String panelTitle, TableModelListener tableModelListener, MouseListener mouseListener)
    {
        title = panelTitle;
        deviceTable = new DeviceTable();
        deviceTable.addTableModelListener(tableModelListener);
        templateTable = new JTable(deviceTable);
        templateTable.addMouseListener(mouseListener);
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
