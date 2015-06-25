package com.projects.input.listeners;

import com.projects.management.SystemController;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Created by Dan on 6/19/2015.
 */
public class DevicePropertiesTableListener implements TableModelListener
{
    private SystemController controller;

    public DevicePropertiesTableListener(SystemController control)
    {
        controller = control;
    }

    @Override
    public void tableChanged(TableModelEvent e)
    {
        if (e.getType() == TableModelEvent.UPDATE)
        {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && column >= 0)
            {
                TableModel model = (TableModel)e.getSource();
                Object value = model.getValueAt(row, column);
                controller.editDeviceProperty(row, value);
            }
        }
    }
}
