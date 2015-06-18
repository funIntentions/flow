package com.projects.gui;

import com.projects.gui.table.DeviceTable;
import com.projects.models.Device;
import com.projects.models.Template;
import com.projects.models.TemplateManager;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Created by Dan on 6/18/2015.
 */
public class DevicePanel extends JScrollPane implements SubscribedView
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

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(TemplateManager.PC_TEMPLATE_READY))
        {
            Template template = (Template)event.getNewValue();

            List<Device> devices = template.getDeviceTemplates();

            for (Device device : devices)
            {
                deviceTable.addRow(device);
            }
        }
    }

    public JTable getIndividualTable()
    {
        return templateTable;
    }
}
