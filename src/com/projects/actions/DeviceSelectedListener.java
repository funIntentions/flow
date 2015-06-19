package com.projects.actions;

import com.projects.gui.table.DeviceTable;
import com.projects.management.SystemController;
import com.projects.models.Device;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dan on 6/19/2015.
 */
public class DeviceSelectedListener extends MouseAdapter
{
    SystemController controller;

    public DeviceSelectedListener(SystemController systemController)
    {
        controller = systemController;
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        super.mousePressed(event);
        JTable target = (JTable)event.getSource();
        Device device = ((DeviceTable)target.getModel()).getRow(target.getSelectedRow());
        controller.deviceSelected(device);
    }
}
